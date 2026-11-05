package com.example.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.CloudSyncRepository
import com.example.data.EduRepository
import com.example.model.*
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

private const val TAG = "EduViewModel"

sealed interface AiUiState {
    data object Idle : AiUiState
    data object Loading : AiUiState
    data class Streaming(val text: String) : AiUiState
    data class Success(val text: String) : AiUiState
    data class Error(val message: String) : AiUiState
}

object AppRoutes {
    const val ROUTE_HOME = "home"
    const val ROUTE_LECTURES = "lectures"
    const val ROUTE_PRACTICE = "practice"
    const val ROUTE_PROFILE = "profile"
    const val ROUTE_BATCHES = "batches"
}

enum class MainNavTab(
    val route: String,
    val label: String,
    val isBottomBarItem: Boolean = true
) {
    HOME(AppRoutes.ROUTE_HOME, "Home", true),
    LECTURES(AppRoutes.ROUTE_LECTURES, "Lectures", true),
    PRACTICE(AppRoutes.ROUTE_PRACTICE, "Practice", true),
    PROFILE(AppRoutes.ROUTE_PROFILE, "Profile", true),
    BATCHES(AppRoutes.ROUTE_BATCHES, "Batches", false);

    companion object {
        val bottomBarTabs = listOf(HOME, LECTURES, PRACTICE, PROFILE)
        val DPP = PRACTICE
        fun fromRoute(route: String?): MainNavTab {
            return entries.find { it.route == route } ?: HOME
        }
    }
}

class EduViewModel(
    private val repository: EduRepository = EduRepository(),
    private val cloudRepository: CloudSyncRepository? = null,
    private val currentUserId: String? = null,
    private val currentUserDisplayName: String? = null,
    private val currentUserEmail: String? = null
) : ViewModel() {

    // Lazy initialization of Gemini 3.8 Flash for streaming explanations, derivations & study plans
    private val aiTextModel by lazy {
        Firebase.ai.generativeModel("gemini-3.8-flash")
    }

    // Structured JSON Schema for AI Custom DPP Question Generation
    private val dppQuestionItemSchema = Schema.obj(
        properties = mapOf(
            "questionText" to Schema.string(description = "Competitive JEE/NEET level multiple-choice question statement"),
            "options" to Schema.array(
                items = Schema.string(),
                description = "Exactly 4 distinct multiple-choice options"
            ),
            "correctOptionIndex" to Schema.integer(description = "0-based index (0, 1, 2, or 3) of the correct option"),
            "explanation" to Schema.string(description = "Step-by-step mathematical/conceptual derivation and high-yield exam tip"),
            "hint" to Schema.string(description = "Short formula or conceptual hint without giving away the final answer"),
            "topic" to Schema.string(description = "Specific sub-topic name")
        )
    )

    private val dppSheetSchema = Schema.obj(
        properties = mapOf(
            "dppTitle" to Schema.string(description = "Title for the Daily Practice Problem sheet"),
            "chapterTitle" to Schema.string(description = "Chapter name for this practice set"),
            "timeLimitMinutes" to Schema.integer(description = "Recommended time limit in minutes (e.g. 12 or 15)"),
            "questions" to Schema.array(
                items = dppQuestionItemSchema,
                description = "List of generated multiple-choice questions"
            )
        )
    )

    private val aiDppJsonModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = dppSheetSchema
            }
        )
    }

    val courses = repository.courses
    val userProfile = repository.userProfile
    val mockTests = repository.mockTests
    val doubts = repository.doubts

    // AI Feature States
    private val _doubtAiState = MutableStateFlow<AiUiState>(AiUiState.Idle)
    val doubtAiState: StateFlow<AiUiState> = _doubtAiState.asStateFlow()

    private val _lectureAiSummaryState = MutableStateFlow<AiUiState>(AiUiState.Idle)
    val lectureAiSummaryState: StateFlow<AiUiState> = _lectureAiSummaryState.asStateFlow()

    private val _dppAiState = MutableStateFlow<AiUiState>(AiUiState.Idle)
    val dppAiState: StateFlow<AiUiState> = _dppAiState.asStateFlow()

    private val _studyPlannerAiState = MutableStateFlow<AiUiState>(AiUiState.Idle)
    val studyPlannerAiState: StateFlow<AiUiState> = _studyPlannerAiState.asStateFlow()

    // Navigation State
    private val _currentTab = MutableStateFlow(MainNavTab.HOME)
    val currentTab = _currentTab.asStateFlow()

    private val _activeBatchId = MutableStateFlow<String?>(null)
    val activeBatchId = _activeBatchId.asStateFlow()

    private val _activeLectureId = MutableStateFlow<String?>(null)
    val activeLectureId = _activeLectureId.asStateFlow()

    private val _activeDppSolvingId = MutableStateFlow<String?>(null)
    val activeDppSolvingId = _activeDppSolvingId.asStateFlow()

    private val _activeDppResultId = MutableStateFlow<String?>(null)
    val activeDppResultId = _activeDppResultId.asStateFlow()

    // Filters & Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _subjectFilter = MutableStateFlow<SubjectType?>(null)
    val subjectFilter = _subjectFilter.asStateFlow()

    private val _dppFilter = MutableStateFlow("ALL") // "ALL", "PENDING", "COMPLETED"
    val dppFilter = _dppFilter.asStateFlow()

    // Video Player State
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed = _playbackSpeed.asStateFlow()

    private val _videoQuality = MutableStateFlow("720p HD")
    val videoQuality = _videoQuality.asStateFlow()

    private val _lectureProgressSec = MutableStateFlow(0)
    val lectureProgressSec = _lectureProgressSec.asStateFlow()

    private var videoPlaybackJob: Job? = null

    // DPP Solving State
    private val _dppQuestionIndex = MutableStateFlow(0)
    val dppQuestionIndex = _dppQuestionIndex.asStateFlow()

    private val _dppUserAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val dppUserAnswers = _dppUserAnswers.asStateFlow()

    private val _dppUserMultiAnswers = MutableStateFlow<Map<Int, Set<Int>>>(emptyMap())
    val dppUserMultiAnswers = _dppUserMultiAnswers.asStateFlow()

    private val _dppReviewFlags = MutableStateFlow<Set<Int>>(emptySet())
    val dppReviewFlags = _dppReviewFlags.asStateFlow()

    private val _dppTimerSec = MutableStateFlow(0)
    val dppTimerSec = _dppTimerSec.asStateFlow()

    private var dppTimerJob: Job? = null

    // Notifications
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage = _snackbarMessage.asStateFlow()

    init {
        val cloud = cloudRepository
        val uid = currentUserId
        if (cloud != null && !uid.isNullOrBlank()) {
            val defaultName = currentUserDisplayName?.takeIf { it.isNotBlank() } ?: repository.userProfile.value.name
            if (!currentUserDisplayName.isNullOrBlank()) {
                repository.updateUserProfile(
                    name = defaultName,
                    targetExam = repository.userProfile.value.targetExam,
                    classLevel = repository.userProfile.value.classLevel,
                    studentGoal = repository.userProfile.value.studentGoal,
                    bio = repository.userProfile.value.bio
                )
            }
            viewModelScope.launch {
                cloud.createInitialProfileIfMissing(
                    name = defaultName,
                    email = currentUserEmail,
                    targetExam = repository.userProfile.value.targetExam,
                    classLevel = repository.userProfile.value.classLevel,
                    studentGoal = repository.userProfile.value.studentGoal,
                    bio = repository.userProfile.value.bio
                )
            }
            viewModelScope.launch {
                cloud.observeStudentProfile(uid)
                    .catch { error ->
                        val firestoreError = generateSequence(error) { it.cause }
                            .filterIsInstance<FirebaseFirestoreException>()
                            .firstOrNull()
                        if (firestoreError == null && error is CancellationException) throw error
                        Log.e(TAG, "Profile sync stream error", firestoreError ?: error)
                    }
                    .collect { profileDoc ->
                        if (profileDoc != null) {
                            repository.applyCloudProfile(profileDoc)
                        }
                    }
            }
            viewModelScope.launch {
                cloud.observeDppAttempts(uid)
                    .catch { error ->
                        val firestoreError = generateSequence(error) { it.cause }
                            .filterIsInstance<FirebaseFirestoreException>()
                            .firstOrNull()
                        if (firestoreError == null && error is CancellationException) throw error
                        Log.e(TAG, "DPP attempts sync stream error", firestoreError ?: error)
                    }
                    .collect { attempts ->
                        repository.applyCloudDppAttempts(attempts)
                    }
            }
            viewModelScope.launch {
                cloud.observeStudentDoubts(uid)
                    .catch { error ->
                        val firestoreError = generateSequence(error) { it.cause }
                            .filterIsInstance<FirebaseFirestoreException>()
                            .firstOrNull()
                        if (firestoreError == null && error is CancellationException) throw error
                        Log.e(TAG, "Student doubts sync stream error", firestoreError ?: error)
                    }
                    .collect { cloudDoubts ->
                        repository.applyCloudDoubts(cloudDoubts)
                    }
            }
        }
    }

    private fun syncProfileToCloud() {
        val cloud = cloudRepository ?: return
        if (currentUserId.isNullOrBlank()) return
        val profile = repository.userProfile.value
        val enrolledIds = repository.getEnrolledCourseIds()
        val bookmarkedIds = repository.getBookmarkedLectureIds()
        val completedIds = repository.getCompletedLectureIds()
        viewModelScope.launch {
            cloud.updateStudentProfile(
                name = profile.name,
                targetExam = profile.targetExam,
                classLevel = profile.classLevel,
                studentGoal = profile.studentGoal,
                bio = profile.bio,
                currentStreakDays = profile.currentStreakDays,
                totalDppsSolved = profile.totalDppsSolved,
                overallAccuracy = profile.overallAccuracy,
                lecturesWatched = profile.lecturesWatched,
                studyHours = profile.studyHours,
                xp = profile.xp,
                enrolledCourseIds = enrolledIds,
                bookmarkedLectureIds = bookmarkedIds,
                completedLectureIds = completedIds
            )
        }
    }

    fun switchTab(tab: MainNavTab) {
        _currentTab.value = tab
        _activeBatchId.value = null
        _activeLectureId.value = null
        _activeDppSolvingId.value = null
        _activeDppResultId.value = null
        stopVideoPlayback()
    }

    fun openBatch(batchId: String) {
        _activeBatchId.value = batchId
    }

    fun closeBatch() {
        _activeBatchId.value = null
    }

    fun openLecture(lectureId: String) {
        val lec = repository.findLectureById(lectureId)
        _activeLectureId.value = lectureId
        _lectureProgressSec.value = lec?.progressSec ?: 0
        _lectureAiSummaryState.value = AiUiState.Idle
        _isPlaying.value = true
        startVideoPlayback()
    }

    fun closeLecture() {
        val lecId = _activeLectureId.value
        if (lecId != null) {
            val lec = repository.findLectureById(lecId)
            val isDone = lec != null && _lectureProgressSec.value >= (lec.durationMinutes * 60 * 0.9f)
            repository.updateLectureProgress(lecId, _lectureProgressSec.value, isDone)
            if (isDone) {
                syncProfileToCloud()
            }
        }
        stopVideoPlayback()
        _activeLectureId.value = null
    }

    fun togglePlayPause() {
        if (_isPlaying.value) {
            stopVideoPlayback()
            _isPlaying.value = false
        } else {
            _isPlaying.value = true
            startVideoPlayback()
        }
    }

    fun seekTo(seconds: Int) {
        val lec = _activeLectureId.value?.let { repository.findLectureById(it) } ?: return
        val maxSec = lec.durationMinutes * 60
        _lectureProgressSec.value = seconds.coerceIn(0, maxSec)
    }

    fun skipForward(seconds: Int = 10) {
        seekTo(_lectureProgressSec.value + seconds)
    }

    fun skipBackward(seconds: Int = 10) {
        seekTo(_lectureProgressSec.value - seconds)
    }

    fun setPlaybackSpeed(speed: Float) {
        _playbackSpeed.value = speed
    }

    fun setVideoQuality(quality: String) {
        _videoQuality.value = quality
        showSnackbar("Video quality set to $quality")
    }

    private fun startVideoPlayback() {
        videoPlaybackJob?.cancel()
        videoPlaybackJob = viewModelScope.launch {
            while (_isPlaying.value) {
                val delayTime = (1000 / _playbackSpeed.value).toLong()
                delay(delayTime)
                val lec = _activeLectureId.value?.let { repository.findLectureById(it) }
                if (lec != null) {
                    val maxSec = lec.durationMinutes * 60
                    if (_lectureProgressSec.value < maxSec) {
                        _lectureProgressSec.value += 1
                    } else {
                        _isPlaying.value = false
                        repository.updateLectureProgress(lec.id, maxSec, true)
                        syncProfileToCloud()
                        showSnackbar("🎉 Lecture completed! +50 XP")
                    }
                }
            }
        }
    }

    private fun stopVideoPlayback() {
        videoPlaybackJob?.cancel()
        videoPlaybackJob = null
    }

    // DPP Actions
    fun startDpp(dppId: String) {
        val dpp = repository.findDppById(dppId) ?: return
        if (dpp.status == DppStatus.COMPLETED) {
            _activeDppResultId.value = dppId
            return
        }
        _activeDppSolvingId.value = dppId
        _dppQuestionIndex.value = 0
        _dppUserAnswers.value = emptyMap()
        _dppUserMultiAnswers.value = emptyMap()
        _dppReviewFlags.value = emptySet()
        _dppTimerSec.value = dpp.timeLimitMinutes * 60
        startDppTimer()
    }

    fun selectDppOption(questionIndex: Int, optionIndex: Int) {
        val activeDpp = _activeDppSolvingId.value?.let { repository.findDppById(it) }
        val question = activeDpp?.questions?.getOrNull(questionIndex)
        val isMulti = question?.isMultiSelect == true

        if (isMulti) {
            val currentMulti = _dppUserMultiAnswers.value.toMutableMap()
            val existingSet = currentMulti[questionIndex]?.toMutableSet() ?: mutableSetOf()
            if (existingSet.contains(optionIndex)) {
                existingSet.remove(optionIndex)
            } else {
                existingSet.add(optionIndex)
            }
            if (existingSet.isEmpty()) {
                currentMulti.remove(questionIndex)
            } else {
                currentMulti[questionIndex] = existingSet.toSortedSet()
            }
            _dppUserMultiAnswers.value = currentMulti

            val currentSingle = _dppUserAnswers.value.toMutableMap()
            val firstSelected = existingSet.minOrNull()
            if (firstSelected == null) {
                currentSingle.remove(questionIndex)
            } else {
                currentSingle[questionIndex] = firstSelected
            }
            _dppUserAnswers.value = currentSingle
        } else {
            val currentSingle = _dppUserAnswers.value.toMutableMap()
            currentSingle[questionIndex] = optionIndex
            _dppUserAnswers.value = currentSingle

            val currentMulti = _dppUserMultiAnswers.value.toMutableMap()
            currentMulti[questionIndex] = setOf(optionIndex)
            _dppUserMultiAnswers.value = currentMulti
        }
    }

    fun toggleReviewFlag(questionIndex: Int) {
        val current = _dppReviewFlags.value.toMutableSet()
        if (current.contains(questionIndex)) current.remove(questionIndex) else current.add(questionIndex)
        _dppReviewFlags.value = current
    }

    fun setDppQuestionIndex(index: Int) {
        _dppQuestionIndex.value = index
    }

    fun submitCurrentDpp() {
        val dppId = _activeDppSolvingId.value ?: return
        val dpp = repository.findDppById(dppId) ?: return
        dppTimerJob?.cancel()
        val timeSpent = ((dpp.timeLimitMinutes * 60) - _dppTimerSec.value).coerceAtLeast(10)

        repository.submitDpp(
            dppId = dppId,
            userAnswers = _dppUserAnswers.value,
            timeSpentSeconds = timeSpent,
            userMultiAnswers = _dppUserMultiAnswers.value
        )
        val updatedDpp = repository.findDppById(dppId)
        val cloud = cloudRepository
        if (cloud != null && !currentUserId.isNullOrBlank() && updatedDpp != null) {
            viewModelScope.launch {
                cloud.saveDppAttempt(
                    dppId = updatedDpp.id,
                    title = updatedDpp.title,
                    subject = updatedDpp.subject.name,
                    score = updatedDpp.score,
                    totalQuestions = updatedDpp.questions.size.coerceAtLeast(1),
                    timeSpentSeconds = updatedDpp.timeSpentSeconds,
                    status = "COMPLETED"
                )
            }
            syncProfileToCloud()
        }
        _activeDppSolvingId.value = null
        _activeDppResultId.value = dppId
        showSnackbar("🎯 DPP Submitted successfully! Check solutions.")
    }

    fun reattemptDpp(dppId: String) {
        repository.reattemptDpp(dppId)
        val dpp = repository.findDppById(dppId)
        val cloud = cloudRepository
        if (cloud != null && !currentUserId.isNullOrBlank() && dpp != null) {
            viewModelScope.launch {
                cloud.saveDppAttempt(
                    dppId = dpp.id,
                    title = dpp.title,
                    subject = dpp.subject.name,
                    score = 0,
                    totalQuestions = dpp.questions.size.coerceAtLeast(1),
                    timeSpentSeconds = 0,
                    status = "PENDING"
                )
            }
        }
        _activeDppResultId.value = null
        startDpp(dppId)
    }

    fun closeDppSolving() {
        dppTimerJob?.cancel()
        _activeDppSolvingId.value = null
    }

    fun closeDppResult() {
        _activeDppResultId.value = null
    }

    private fun startDppTimer() {
        dppTimerJob?.cancel()
        dppTimerJob = viewModelScope.launch {
            while (_dppTimerSec.value > 0 && _activeDppSolvingId.value != null) {
                delay(1000)
                _dppTimerSec.value -= 1
                if (_dppTimerSec.value == 0) {
                    submitCurrentDpp()
                    showSnackbar("⏰ Time is up! DPP submitted automatically.")
                }
            }
        }
    }

    // =========================================================================
    // GEMINI AI STUDY SUITE (Firebase AI Logic - gemini-3.8-flash)
    // =========================================================================

    // 1. AI Doubt Solver Tutor (Streaming with Step-by-Step Derivations & Exam Tips)
    fun postDoubt(questionText: String, subject: SubjectType) {
        if (questionText.isBlank()) return
        val doubtId = "d_${System.currentTimeMillis()}"
        val studentName = userProfile.value.name
        val initialDoubt = DoubtQuery(
            id = doubtId,
            studentName = studentName,
            questionText = questionText.trim(),
            subject = subject,
            status = "Gemini AI Tutor is solving...",
            facultyReply = "Generating step-by-step derivation...",
            timestamp = "Just now"
        )
        repository.upsertDoubt(initialDoubt)

        viewModelScope.launch {
            _doubtAiState.value = AiUiState.Loading
            val accumulatedText = StringBuilder()
            val prompt = """
                You are an expert ${subject.displayName} professor at ARCSTFUG coaching for ${userProfile.value.targetExam}.
                A student asked the following doubt:
                "$questionText"

                Respond using this exact pedagogical structure:
                1. Core Concept & Formula: State the governing principle or equation clearly.
                2. Step-by-Step Derivation / Solution: Show every mathematical or logical step clearly.
                3. High-Yield Exam Tip: Share a shortcut, common trap to avoid, or JEE/NEET exam insight.
                Keep the explanation clear, accurate, and easy to read on mobile.
            """.trimIndent()

            try {
                withTimeout(60_000L) {
                    aiTextModel.generateContentStream(prompt).collect { chunk ->
                        val chunkText = chunk.text.orEmpty()
                        accumulatedText.append(chunkText)
                        val partial = accumulatedText.toString()
                        _doubtAiState.value = AiUiState.Streaming(partial)
                        repository.upsertDoubt(
                            initialDoubt.copy(
                                status = "Gemini AI Tutor (Streaming...)",
                                facultyReply = partial
                            )
                        )
                    }
                }
                val finalAnswer = accumulatedText.toString().trim()
                if (finalAnswer.isNotEmpty()) {
                    _doubtAiState.value = AiUiState.Success(finalAnswer)
                    val solvedStatus = "Solved by Gemini AI Tutor ✨"
                    repository.upsertDoubt(
                        initialDoubt.copy(
                            status = solvedStatus,
                            facultyReply = finalAnswer
                        )
                    )
                    if (cloudRepository != null && !currentUserId.isNullOrBlank()) {
                        cloudRepository.saveStudentDoubt(
                            doubtId = doubtId,
                            studentName = studentName,
                            questionText = questionText.trim(),
                            subject = subject.name,
                            status = solvedStatus,
                            facultyReply = finalAnswer
                        )
                    }
                    showSnackbar("✨ Gemini AI Tutor solved your doubt!")
                } else {
                    val emptyMsg = "Gemini AI returned an empty response. Please try asking again."
                    _doubtAiState.value = AiUiState.Error(emptyMsg)
                    repository.upsertDoubt(
                        initialDoubt.copy(
                            status = "AI Error",
                            facultyReply = emptyMsg
                        )
                    )
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "AI Doubt Solver timed out", e)
                val msg = "AI Doubt Solver timed out after 60 seconds. Please try again."
                _doubtAiState.value = AiUiState.Error(msg)
                repository.upsertDoubt(initialDoubt.copy(status = "Timed Out", facultyReply = msg))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "AI Doubt Solver failed", e)
                val msg = e.localizedMessage ?: "Failed to generate AI doubt explanation."
                _doubtAiState.value = AiUiState.Error(msg)
                repository.upsertDoubt(initialDoubt.copy(status = "AI Error", facultyReply = msg))
            }
        }
    }

    // 2. AI Lecture Concept & Formula Summarizer (Streaming Derivations & Exam Tips)
    fun summarizeLectureWithAi(lecture: Lecture) {
        viewModelScope.launch {
            _lectureAiSummaryState.value = AiUiState.Loading
            val accumulatedText = StringBuilder()
            val existingNotes = lecture.notesSummary.joinToString("\n- ")
            val prompt = """
                You are an expert ${lecture.subject.displayName} faculty member at ARCSTFUG (${lecture.instructor}).
                Generate an in-depth "Step-by-Step Derivation & Exam Revision Sheet" for the following lecture:
                - Lecture Title: ${lecture.title}
                - Chapter: ${lecture.chapterTitle}
                - Key Topics Covered:
                - $existingNotes

                Structure your output into:
                1. Step-by-Step Key Derivations: Break down the most important formula derivations clearly.
                2. Master Formula Sheet: Bullet points of must-memorize equations and conditions.
                3. JEE/NEET Exam Tips & Common Pitfalls: 3 high-yield exam tricks and mistakes students must avoid.
            """.trimIndent()

            try {
                withTimeout(60_000L) {
                    aiTextModel.generateContentStream(prompt).collect { chunk ->
                        val chunkText = chunk.text.orEmpty()
                        accumulatedText.append(chunkText)
                        _lectureAiSummaryState.value = AiUiState.Streaming(accumulatedText.toString())
                    }
                }
                val finalSummary = accumulatedText.toString().trim()
                if (finalSummary.isNotEmpty()) {
                    _lectureAiSummaryState.value = AiUiState.Success(finalSummary)
                } else {
                    _lectureAiSummaryState.value = AiUiState.Error("Model returned an empty lecture summary.")
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Lecture AI summary timed out", e)
                _lectureAiSummaryState.value = AiUiState.Error("Lecture summary timed out after 60 seconds. Please retry.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Lecture AI summary failed", e)
                _lectureAiSummaryState.value = AiUiState.Error(e.localizedMessage ?: "Failed to generate AI lecture notes.")
            }
        }
    }

    // 3. AI Custom DPP Practice Question Generator (Structured JSON Schema)
    fun generateCustomAiDpp(
        subject: SubjectType,
        topic: String,
        difficulty: String = "JEE Main / NEET Level",
        openImmediately: Boolean = true
    ) {
        val cleanTopic = topic.trim().ifEmpty { "${subject.displayName} High-Yield Problems" }
        viewModelScope.launch {
            _dppAiState.value = AiUiState.Loading
            val prompt = """
                Generate a 3-question Daily Practice Problem (DPP) sheet for ${userProfile.value.targetExam}.
                Subject: ${subject.displayName}
                Topic: $cleanTopic
                Difficulty: $difficulty
                Requirements:
                - Each question must have exactly 4 options and a valid correctOptionIndex (0 to 3).
                - In the 'explanation' field, provide a complete step-by-step derivation and an exam tip.
                - In the 'hint' field, provide a concise formula or conceptual nudge.
            """.trimIndent()

            try {
                val response = withTimeout(60_000L) {
                    aiDppJsonModel.generateContent(prompt)
                }
                val jsonString = response.text ?: throw IllegalStateException("Model returned an empty JSON response.")
                val json = JSONObject(jsonString)

                val dppTitle = json.optString("dppTitle", "AI DPP: $cleanTopic")
                val chapterTitle = json.optString("chapterTitle", cleanTopic)
                val timeLimit = json.optInt("timeLimitMinutes", 15).coerceIn(5, 45)
                val questionsArray = json.getJSONArray("questions")

                val parsedQuestions = mutableListOf<Question>()
                for (i in 0 until questionsArray.length()) {
                    val qObj = questionsArray.getJSONObject(i)
                    val qText = qObj.getString("questionText")
                    val optsJson = qObj.getJSONArray("options")
                    val optionsList = (0 until optsJson.length()).map { optsJson.getString(it) }
                    val safeOptions = if (optionsList.size >= 4) optionsList.take(4) else optionsList + List(4 - optionsList.size) { "None of these" }
                    val correctIdx = qObj.optInt("correctOptionIndex", 0).coerceIn(0, safeOptions.lastIndex)
                    val explanation = qObj.optString("explanation", "Apply standard formula and solve step-by-step.")
                    val hint = qObj.optString("hint", "Write down the given values and apply the core chapter equation.")
                    val qTopic = qObj.optString("topic", cleanTopic)

                    parsedQuestions.add(
                        Question(
                            id = "ai_q_${System.currentTimeMillis()}_$i",
                            questionText = qText,
                            options = safeOptions,
                            correctOptionIndex = correctIdx,
                            explanation = explanation,
                            hint = hint,
                            topic = qTopic
                        )
                    )
                }

                if (parsedQuestions.isEmpty()) {
                    throw IllegalStateException("No questions were returned in the generated DPP JSON.")
                }

                val newDppId = "dpp_ai_${System.currentTimeMillis()}"
                val existingCount = repository.getAllDpps().size + 1
                val newDpp = Dpp(
                    id = newDppId,
                    number = existingCount,
                    title = if (dppTitle.startsWith("AI", ignoreCase = true)) dppTitle else "✨ $dppTitle",
                    subject = subject,
                    chapterTitle = chapterTitle,
                    courseId = "c_arjuna_jee",
                    questions = parsedQuestions,
                    timeLimitMinutes = timeLimit,
                    status = DppStatus.PENDING
                )

                repository.addCustomDpp(newDpp)
                _dppAiState.value = AiUiState.Success("Created '${newDpp.title}' with ${parsedQuestions.size} questions!")
                showSnackbar("✨ AI DPP generated! Starting practice...")

                if (openImmediately) {
                    startDpp(newDppId)
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "AI DPP generation timed out", e)
                _dppAiState.value = AiUiState.Error("AI DPP generation timed out after 60 seconds. Please try again.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to generate or parse AI DPP JSON", e)
                _dppAiState.value = AiUiState.Error(e.localizedMessage ?: "Failed to generate custom AI DPP.")
            }
        }
    }

    // 4. AI Study Planner for JEE & NEET Schedules (Streaming)
    fun generateAiStudyPlan(
        dailyHours: Int,
        focusSubject: SubjectType,
        weakTopics: String
    ) {
        viewModelScope.launch {
            _studyPlannerAiState.value = AiUiState.Loading
            val accumulatedText = StringBuilder()
            val targetExam = userProfile.value.targetExam
            val topicsNote = weakTopics.trim().ifEmpty { "Core high-weightage chapters" }
            val prompt = """
                You are the Chief Academic Mentor at ARCSTFUG. Create a high-impact 7-Day Study & Revision Schedule for a student preparing for $targetExam.
                - Student Name: ${userProfile.value.name}
                - Available Daily Study Time: $dailyHours hours/day
                - Primary Focus Subject: ${focusSubject.displayName}
                - Priority Weak Topics to Master: $topicsNote

                Structure the plan clearly:
                1. Daily Time-Block Breakdown: Morning Concept & Derivation Slot, Afternoon Timed DPP Slot, Evening Revision & Formula Recall.
                2. 7-Day Action Plan: Specific daily targets balancing ${focusSubject.displayName} with other exam subjects.
                3. Step-by-Step Problem Solving Strategy & Exam Tips: Actionable tips to boost accuracy from ${userProfile.value.overallAccuracy}% to 95%+.
            """.trimIndent()

            try {
                withTimeout(60_000L) {
                    aiTextModel.generateContentStream(prompt).collect { chunk ->
                        val chunkText = chunk.text.orEmpty()
                        accumulatedText.append(chunkText)
                        _studyPlannerAiState.value = AiUiState.Streaming(accumulatedText.toString())
                    }
                }
                val finalPlan = accumulatedText.toString().trim()
                if (finalPlan.isNotEmpty()) {
                    _studyPlannerAiState.value = AiUiState.Success(finalPlan)
                } else {
                    _studyPlannerAiState.value = AiUiState.Error("Model returned an empty study plan.")
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "AI Study Planner timed out", e)
                _studyPlannerAiState.value = AiUiState.Error("Study Planner timed out after 60 seconds. Please retry.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "AI Study Planner failed", e)
                _studyPlannerAiState.value = AiUiState.Error(e.localizedMessage ?: "Failed to generate AI study plan.")
            }
        }
    }

    fun resetDppAiState() {
        _dppAiState.value = AiUiState.Idle
    }

    fun resetDoubtAiState() {
        _doubtAiState.value = AiUiState.Idle
    }

    // Repository Delegation
    fun updateUserProfile(
        name: String,
        targetExam: String,
        classLevel: String,
        studentGoal: String,
        bio: String
    ) {
        repository.updateUserProfile(
            name = name,
            targetExam = targetExam,
            classLevel = classLevel,
            studentGoal = studentGoal,
            bio = bio
        )
        syncProfileToCloud()
        showSnackbar("✅ Student profile updated & synced to cloud!")
    }

    fun enrollCourse(courseId: String) {
        repository.enrollCourse(courseId)
        syncProfileToCloud()
        showSnackbar("Course status updated!")
    }

    fun toggleLectureBookmark(lectureId: String) {
        repository.toggleLectureBookmark(lectureId)
        syncProfileToCloud()
        showSnackbar("Bookmark updated!")
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSubjectFilter(subject: SubjectType?) {
        _subjectFilter.value = subject
    }

    fun setDppFilter(filter: String) {
        _dppFilter.value = filter
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    private fun showSnackbar(message: String) {
        _snackbarMessage.value = message
    }

    companion object {
        fun provideFactory(
            context: Context,
            userId: String?,
            displayName: String?,
            email: String?
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val appContext = context.applicationContext
                val dbId = appContext.getString(R.string.firestore_database_id)
                val firestore = FirebaseFirestore.getInstance(dbId)
                val cloudRepo = CloudSyncRepository(firestore)
                return EduViewModel(
                    repository = EduRepository(),
                    cloudRepository = cloudRepo,
                    currentUserId = userId,
                    currentUserDisplayName = displayName,
                    currentUserEmail = email
                ) as T
            }
        }
    }
}
