package com.example.data

import com.example.R
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.json.JSONArray
import org.json.JSONObject

class EduRepository {

    private val _courses = MutableStateFlow<List<CourseBatch>>(createInitialCourses())
    val courses: StateFlow<List<CourseBatch>> = _courses.asStateFlow()

    private val _userProfile = MutableStateFlow(
        UserProfile(
            name = "Student Name",
            targetExam = "JEE Main & Advanced 2026",
            currentStreakDays = 14,
            totalDppsSolved = 28,
            overallAccuracy = 82,
            lecturesWatched = 42,
            studyHours = 56.5f,
            xp = 3450,
            badges = listOf(
                StudyBadge("b1", "14-Day Streak Fire", "Studied 14 days without skipping a day", "🔥", true),
                StudyBadge("b2", "DPP Ace", "Solved 25+ Daily Practice Problem sets", "🎯", true),
                StudyBadge("b3", "Physics Prodigy", "Scored 100% on Kinematics DPP", "⚡", true),
                StudyBadge("b4", "Night Owl Scholar", "Completed 5 lectures post 10 PM", "🦉", true),
                StudyBadge("b5", "Grandmaster", "Complete 50 DPPs and 10 Full Mock tests", "👑", false)
            )
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _mockTests = MutableStateFlow(createInitialMockTests())
    val mockTests: StateFlow<List<TestExam>> = _mockTests.asStateFlow()

    private val _doubts = MutableStateFlow(createInitialDoubts())
    val doubts: StateFlow<List<DoubtQuery>> = _doubts.asStateFlow()

    private val _studyMaterials = MutableStateFlow(createInitialStudyMaterials())
    val studyMaterials: StateFlow<List<StudyMaterial>> = _studyMaterials.asStateFlow()

    private val _teacherUploads = MutableStateFlow<List<TeacherUploadItem>>(emptyList())
    val teacherUploads: StateFlow<List<TeacherUploadItem>> = _teacherUploads.asStateFlow()

    private val _announcements = MutableStateFlow<List<AnnouncementNotice>>(createInitialAnnouncements())
    val announcements: StateFlow<List<AnnouncementNotice>> = _announcements.asStateFlow()

    fun updateUserProfile(
        name: String,
        targetExam: String,
        classLevel: String,
        studentGoal: String,
        bio: String
    ) {
        _userProfile.update { current ->
            current.copy(
                name = name.trim().ifEmpty { current.name },
                targetExam = targetExam.trim().ifEmpty { current.targetExam },
                classLevel = classLevel.trim().ifEmpty { current.classLevel },
                studentGoal = studentGoal.trim().ifEmpty { current.studentGoal },
                bio = bio.trim().ifEmpty { current.bio }
            )
        }
    }

    fun enrollCourse(courseId: String) {
        _courses.update { list ->
            list.map { course ->
                if (course.id == courseId) course.copy(isEnrolled = !course.isEnrolled) else course
            }
        }
    }

    fun updateLectureProgress(lectureId: String, progressSec: Int, isCompleted: Boolean) {
        _courses.update { list ->
            list.map { course ->
                course.copy(
                    subjects = course.subjects.map { subj ->
                        subj.copy(
                            chapters = subj.chapters.map { chap ->
                                chap.copy(
                                    lectures = chap.lectures.map { lec ->
                                        if (lec.id == lectureId) {
                                            lec.copy(progressSec = progressSec, isCompleted = isCompleted)
                                        } else lec
                                    }
                                )
                            }
                        )
                    }
                )
            }
        }
        if (isCompleted) {
            _userProfile.update { current ->
                current.copy(
                    lecturesWatched = current.lecturesWatched + 1,
                    studyHours = current.studyHours + 0.8f,
                    xp = current.xp + 50
                )
            }
        }
    }

    fun toggleLectureBookmark(lectureId: String) {
        _courses.update { list ->
            list.map { course ->
                course.copy(
                    subjects = course.subjects.map { subj ->
                        subj.copy(
                            chapters = subj.chapters.map { chap ->
                                chap.copy(
                                    lectures = chap.lectures.map { lec ->
                                        if (lec.id == lectureId) {
                                            lec.copy(isBookmarked = !lec.isBookmarked)
                                        } else lec
                                    }
                                )
                            }
                        )
                    }
                )
            }
        }
    }

    fun submitDpp(
        dppId: String,
        userAnswers: Map<Int, Int>,
        timeSpentSeconds: Int,
        userMultiAnswers: Map<Int, Set<Int>> = emptyMap()
    ) {
        var correctCount = 0
        var totalCount = 0

        _courses.update { list ->
            list.map { course ->
                course.copy(
                    subjects = course.subjects.map { subj ->
                        subj.copy(
                            chapters = subj.chapters.map { chap ->
                                chap.copy(
                                    dpps = chap.dpps.map { dpp ->
                                        if (dpp.id == dppId) {
                                            totalCount = dpp.questions.size
                                            correctCount = dpp.questions.mapIndexed { index, q ->
                                                val chosenSet = userMultiAnswers[index]?.takeIf { it.isNotEmpty() }
                                                    ?: userAnswers[index]?.let { setOf(it) }
                                                    ?: emptySet()
                                                if (chosenSet == q.correctOptionIndices) 1 else 0
                                            }.sum()

                                            dpp.copy(
                                                status = DppStatus.COMPLETED,
                                                userAnswers = userAnswers,
                                                userMultiAnswers = userMultiAnswers,
                                                score = correctCount,
                                                timeSpentSeconds = timeSpentSeconds,
                                                completedAt = System.currentTimeMillis()
                                            )
                                        } else dpp
                                    }
                                )
                            }
                        )
                    }
                )
            }
        }

        val accuracyScore = if (totalCount > 0) ((correctCount.toFloat() / totalCount) * 100).toInt() else 0
        _userProfile.update { current ->
            current.copy(
                totalDppsSolved = current.totalDppsSolved + 1,
                xp = current.xp + (correctCount * 30) + 40,
                overallAccuracy = ((current.overallAccuracy + accuracyScore) / 2)
            )
        }
    }

    fun reattemptDpp(dppId: String) {
        _courses.update { list ->
            list.map { course ->
                course.copy(
                    subjects = course.subjects.map { subj ->
                        subj.copy(
                            chapters = subj.chapters.map { chap ->
                                chap.copy(
                                    dpps = chap.dpps.map { dpp ->
                                        if (dpp.id == dppId) {
                                            dpp.copy(
                                                status = DppStatus.PENDING,
                                                userAnswers = emptyMap(),
                                                userMultiAnswers = emptyMap(),
                                                score = 0,
                                                timeSpentSeconds = 0,
                                                completedAt = null
                                            )
                                        } else dpp
                                    }
                                )
                            }
                        )
                    }
                )
            }
        }
    }

    fun postDoubt(questionText: String, subject: SubjectType) {
        val newDoubt = DoubtQuery(
            id = "d_${System.currentTimeMillis()}",
            studentName = _userProfile.value.name,
            questionText = questionText,
            subject = subject,
            status = "Answered by AI Tutor",
            facultyReply = null,
            timestamp = "Just now"
        )
        _doubts.update { listOf(newDoubt) + it }
    }

    fun upsertDoubt(doubt: DoubtQuery) {
        _doubts.update { currentList ->
            val exists = currentList.any { it.id == doubt.id }
            if (exists) {
                currentList.map { if (it.id == doubt.id) doubt else it }
            } else {
                listOf(doubt) + currentList
            }
        }
    }

    fun addCustomLecture(lecture: Lecture) {
        _courses.update { courseList ->
            val alreadyExists = courseList.any { c ->
                c.subjects.any { s -> s.chapters.any { ch -> ch.lectures.any { it.id == lecture.id } } }
            }
            if (alreadyExists) return@update courseList

            var added = false
            val updated = courseList.map { course ->
                if (!added && (course.id == lecture.courseId || course.subjects.any { it.type == lecture.subject })) {
                    val hasSubj = course.subjects.any { it.type == lecture.subject }
                    if (hasSubj) {
                        course.copy(
                            subjects = course.subjects.map { subj ->
                                if (!added && subj.type == lecture.subject) {
                                    added = true
                                    val matchingChapIdx = subj.chapters.indexOfFirst {
                                        it.title.equals(lecture.chapterTitle, ignoreCase = true) ||
                                            it.id == lecture.chapterId
                                    }
                                    if (matchingChapIdx >= 0) {
                                        subj.copy(
                                            chapters = subj.chapters.mapIndexed { idx, chap ->
                                                if (idx == matchingChapIdx) {
                                                    chap.copy(lectures = listOf(lecture) + chap.lectures)
                                                } else chap
                                            }
                                        )
                                    } else if (subj.chapters.isNotEmpty()) {
                                        val newChap = Chapter(
                                            id = lecture.chapterId.ifBlank { "ch_custom_${subj.chapters.size + 1}" },
                                            subject = lecture.subject,
                                            title = lecture.chapterTitle.ifBlank { "Teacher Uploaded Lectures" },
                                            order = subj.chapters.size + 1,
                                            lectures = listOf(lecture),
                                            dpps = emptyList()
                                        )
                                        subj.copy(chapters = listOf(newChap) + subj.chapters)
                                    } else {
                                        val newChap = Chapter(
                                            id = lecture.chapterId.ifBlank { "ch_custom_1" },
                                            subject = lecture.subject,
                                            title = lecture.chapterTitle.ifBlank { "Teacher Uploaded Lectures" },
                                            order = 1,
                                            lectures = listOf(lecture),
                                            dpps = emptyList()
                                        )
                                        subj.copy(chapters = listOf(newChap))
                                    }
                                } else subj
                            }
                        )
                    } else course
                } else course
            }
            updated
        }
    }

    fun addCustomDpp(dpp: Dpp) {
        _courses.update { courseList ->
            val alreadyExists = courseList.any { c ->
                c.subjects.any { s -> s.chapters.any { ch -> ch.dpps.any { it.id == dpp.id } } }
            }
            if (alreadyExists) return@update courseList

            var added = false
            val updated = courseList.map { course ->
                if (!added && course.subjects.any { it.type == dpp.subject }) {
                    course.copy(
                        subjects = course.subjects.map { subj ->
                            if (!added && subj.type == dpp.subject && subj.chapters.isNotEmpty()) {
                                added = true
                                val matchingIdx = subj.chapters.indexOfFirst {
                                    it.title.equals(dpp.chapterTitle, ignoreCase = true)
                                }.let { if (it >= 0) it else 0 }
                                subj.copy(
                                    chapters = subj.chapters.mapIndexed { idx, chap ->
                                        if (idx == matchingIdx) chap.copy(dpps = listOf(dpp) + chap.dpps) else chap
                                    }
                                )
                            } else subj
                        }
                    )
                } else course
            }
            if (!added && updated.isNotEmpty()) {
                val firstCourse = updated.first()
                val firstSubj = firstCourse.subjects.firstOrNull()
                if (firstSubj != null && firstSubj.chapters.isNotEmpty()) {
                    updated.mapIndexed { cIdx, c ->
                        if (cIdx == 0) {
                            c.copy(
                                subjects = c.subjects.mapIndexed { sIdx, s ->
                                    if (sIdx == 0) {
                                        s.copy(
                                            chapters = s.chapters.mapIndexed { chIdx, ch ->
                                                if (chIdx == 0) ch.copy(dpps = listOf(dpp) + ch.dpps) else ch
                                            }
                                        )
                                    } else s
                                }
                            )
                        } else c
                    }
                } else updated
            } else updated
        }
    }

    fun addStudyMaterial(material: StudyMaterial) {
        _studyMaterials.update { current ->
            val exists = current.any { it.id == material.id }
            if (exists) {
                current.map { if (it.id == material.id) material else it }
            } else {
                listOf(material) + current
            }
        }
    }

    fun upsertTeacherUploadItem(item: TeacherUploadItem) {
        _teacherUploads.update { current ->
            val exists = current.any { it.id == item.id }
            if (exists) {
                current.map { if (it.id == item.id) item else it }
            } else {
                listOf(item) + current
            }
        }
    }

    fun removeTeacherUploadById(uploadId: String) {
        _teacherUploads.update { current -> current.filterNot { it.id == uploadId } }
        _studyMaterials.update { current -> current.filterNot { it.id == uploadId } }
        _announcements.update { current -> current.filterNot { it.id == uploadId } }
        _courses.update { courseList ->
            courseList
                .filterNot { it.id == uploadId }
                .map { course ->
                    course.copy(
                        subjects = course.subjects.map { subj ->
                            subj.copy(
                                chapters = subj.chapters.map { chap ->
                                    chap.copy(
                                        lectures = chap.lectures.filterNot { it.id == uploadId },
                                        dpps = chap.dpps.filterNot { it.id == uploadId }
                                    )
                                }
                            )
                        }
                    )
                }
        }
    }

    fun addAnnouncement(notice: AnnouncementNotice) {
        _announcements.update { existing ->
            listOf(notice) + existing.filterNot { it.id == notice.id }
        }
    }

    fun addCourseBatch(course: CourseBatch) {
        _courses.update { existing ->
            listOf(course) + existing.filterNot { it.id == course.id }
        }
    }

    fun applyCloudTeacherUploads(uploads: List<TeacherUploadDoc>) {
        val sorted = uploads.sortedByDescending { it.updatedAt?.seconds ?: 0L }
        val items = sorted.map { doc ->
            val subjectEnum = SubjectType.entries.find { it.name.equals(doc.subject, ignoreCase = true) }
                ?: SubjectType.PHYSICS
            TeacherUploadItem(
                id = doc.id,
                authorId = doc.authorId,
                authorName = doc.authorName,
                uploadType = doc.uploadType,
                title = doc.title,
                subject = subjectEnum,
                chapterTitle = doc.chapterTitle,
                courseId = doc.courseId,
                resourceUrl = doc.resourceUrl,
                materialCategory = doc.materialCategory,
                description = doc.description,
                durationMinutes = doc.durationMinutes.toInt()
            )
        }
        _teacherUploads.value = items

        sorted.forEach { doc ->
            val subjectEnum = SubjectType.entries.find { it.name.equals(doc.subject, ignoreCase = true) }
                ?: SubjectType.PHYSICS
            when (doc.uploadType.uppercase()) {
                "VIDEO" -> {
                    val notes = doc.description.lines().map { it.trim() }.filter { it.isNotEmpty() }
                    val lec = Lecture(
                        id = doc.id,
                        courseId = doc.courseId.ifBlank { "c_arjuna_jee" },
                        subject = subjectEnum,
                        chapterId = "ch_teacher_${doc.chapterTitle.hashCode()}",
                        chapterTitle = doc.chapterTitle.ifBlank { "Faculty Video Lectures" },
                        title = doc.title,
                        lectureNumber = getAllLectures().size + 1,
                        instructor = doc.authorName.ifBlank { "ARCSTFUG Faculty" },
                        durationMinutes = doc.durationMinutes.toInt().coerceAtLeast(10),
                        notesSummary = if (notes.isNotEmpty()) notes else listOf(doc.description),
                        timestamps = listOf(
                            LectureTimestamp(0, "00:00", "Introduction & Core Concepts"),
                            LectureTimestamp(600, "10:00", "Formula Derivations & Key Examples")
                        ),
                        videoUrl = doc.resourceUrl.takeIf { it.isNotBlank() }
                    )
                    addCustomLecture(lec)
                }
                "DPP" -> {
                    val parsedQuestions = parseTeacherDppQuestions(doc.dppPayloadJson, doc.title, doc.chapterTitle)
                    val dpp = Dpp(
                        id = doc.id,
                        number = getAllDpps().size + 1,
                        title = doc.title,
                        subject = subjectEnum,
                        chapterTitle = doc.chapterTitle.ifBlank { "Teacher Uploaded DPPs" },
                        courseId = doc.courseId.ifBlank { "c_arjuna_jee" },
                        questions = parsedQuestions,
                        timeLimitMinutes = doc.durationMinutes.toInt().coerceIn(5, 180),
                        status = DppStatus.PENDING,
                        sourceBook = doc.materialCategory.ifBlank { "Teacher DPP (${doc.authorName})" }
                    )
                    addCustomDpp(dpp)
                }
                "COURSE" -> {
                    val plans = doc.chapterTitle.split(",")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
                        .ifEmpty { listOf("Infinity", "Pro") }
                    val resolvedCourseId = doc.courseId.ifBlank { doc.id }
                    val isPwCourse = doc.title.contains("PW", ignoreCase = true) ||
                        doc.materialCategory.contains("PW", ignoreCase = true) ||
                        resolvedCourseId.startsWith("c_pw_")
                    val customCourse = CourseBatch(
                        id = resolvedCourseId,
                        title = doc.title,
                        category = doc.materialCategory.ifBlank { "HACKER" },
                        tag = doc.materialCategory.ifBlank { "HACKER" },
                        rating = 5.0f,
                        enrolledCount = "Live Batch",
                        price = if (doc.durationMinutes > 0) "₹${doc.durationMinutes}" else "₹2,999",
                        originalPrice = "₹9,999",
                        instructorTeam = doc.authorName.ifBlank { "ARCSTFUG Lead Instructor" },
                        description = doc.description,
                        syllabusCoveragePercent = 100,
                        isEnrolled = true,
                        bannerColorHex = if (isPwCourse) 0xFF1E1B4B else 0xFF78350F,
                        subjects = if (isPwCourse) {
                            PwCourseApiService.createPwBridgeSubjects(resolvedCourseId)
                        } else {
                            listOf(
                                SubjectDetail(
                                    type = subjectEnum,
                                    chapters = emptyList()
                                )
                            )
                        },
                        bannerDrawableRes = if (doc.title.contains("HACKING", ignoreCase = true)) {
                            R.drawable.hacking_course_2027_banner
                        } else null,
                        availablePlans = plans,
                        featureBadge = doc.materialCategory.ifBlank { "HACKER" },
                        storageBucketUrl = doc.resourceUrl.ifBlank { ExternalStudyLinks.FIREBASE_STORAGE_BUCKET }
                    )
                    addCourseBatch(customCourse)
                }
                "NOTICE" -> {
                    val notice = AnnouncementNotice(
                        id = doc.id,
                        title = doc.title,
                        message = doc.description,
                        categoryTag = doc.materialCategory.ifBlank { "OFFICIAL NOTICE" },
                        authorName = doc.authorName.ifBlank { "ARCSTFUG Official Portal" },
                        timestampLabel = "Live Notice",
                        actionUrl = doc.resourceUrl.ifBlank { ExternalStudyLinks.ARCSTFUG_WEB_PORTAL_URL },
                        isPinned = true
                    )
                    addAnnouncement(notice)
                }
                else -> {
                    val mat = StudyMaterial(
                        id = doc.id,
                        title = doc.title,
                        subject = subjectEnum,
                        chapterTitle = doc.chapterTitle,
                        courseId = doc.courseId.ifBlank { "c_arjuna_jee" },
                        category = doc.materialCategory.ifBlank { "PDF Class Notes" },
                        description = doc.description,
                        resourceUrl = doc.resourceUrl,
                        authorName = doc.authorName.ifBlank { "ARCSTFUG Faculty" },
                        uploadedAtLabel = "Synced from Teacher Studio",
                        isTeacherUpload = true
                    )
                    addStudyMaterial(mat)
                }
            }
        }
    }

    private fun parseTeacherDppQuestions(
        jsonStr: String?,
        fallbackTitle: String,
        fallbackTopic: String
    ): List<Question> {
        if (jsonStr.isNullOrBlank()) {
            return listOf(
                Question(
                    id = "q_default_${fallbackTitle.hashCode()}",
                    questionText = "Practice problem from $fallbackTitle ($fallbackTopic)",
                    options = listOf("Option (a)", "Option (b)", "Option (c)", "Option (d)"),
                    correctOptionIndex = 0,
                    explanation = "Refer to the faculty solution notes for $fallbackTitle.",
                    hint = "Review the core formula from $fallbackTopic.",
                    topic = fallbackTopic
                )
            )
        }
        return try {
            val arr = JSONArray(jsonStr)
            val result = mutableListOf<Question>()
            for (i in 0 until arr.length()) {
                val obj: JSONObject = arr.getJSONObject(i)
                val qText = obj.optString("questionText", "Question ${i + 1}")
                val optsArr = obj.optJSONArray("options")
                val opts = if (optsArr != null && optsArr.length() >= 4) {
                    (0 until 4).map { optsArr.optString(it, "Option ${it + 1}") }
                } else {
                    listOf("Option A", "Option B", "Option C", "Option D")
                }
                val correctIndicesArr = obj.optJSONArray("correctOptionIndices")
                val correctSet = if (correctIndicesArr != null && correctIndicesArr.length() > 0) {
                    (0 until correctIndicesArr.length()).map { correctIndicesArr.optInt(it, 0).coerceIn(0, 3) }.toSet()
                } else {
                    setOf(obj.optInt("correctOptionIndex", 0).coerceIn(0, 3))
                }
                val primaryIdx = correctSet.minOrNull() ?: 0
                result.add(
                    Question(
                        id = obj.optString("id", "tq_${i}_${qText.hashCode()}"),
                        questionText = qText,
                        options = opts,
                        correctOptionIndex = primaryIdx,
                        explanation = obj.optString("explanation", "Detailed faculty solution."),
                        hint = obj.optString("hint", "Apply the chapter concept."),
                        topic = obj.optString("topic", fallbackTopic),
                        correctOptionIndices = correctSet,
                        isMultiSelect = correctSet.size > 1 || obj.optBoolean("isMultiSelect", false)
                    )
                )
            }
            if (result.isEmpty()) {
                listOf(
                    Question(
                        id = "q_fallback_${fallbackTitle.hashCode()}",
                        questionText = "Practice problem from $fallbackTitle",
                        options = listOf("Option (a)", "Option (b)", "Option (c)", "Option (d)"),
                        correctOptionIndex = 0,
                        explanation = "Refer to faculty notes.",
                        hint = "Check core definitions.",
                        topic = fallbackTopic
                    )
                )
            } else result
        } catch (e: Exception) {
            listOf(
                Question(
                    id = "q_err_${fallbackTitle.hashCode()}",
                    questionText = "Practice problem from $fallbackTitle",
                    options = listOf("Option (a)", "Option (b)", "Option (c)", "Option (d)"),
                    correctOptionIndex = 0,
                    explanation = "Refer to faculty notes.",
                    hint = "Check core definitions.",
                    topic = fallbackTopic
                )
            )
        }
    }

    // Helper functions to get aggregated items
    fun getAllLectures(): List<Lecture> {
        return _courses.value.flatMap { course ->
            course.subjects.flatMap { subj ->
                subj.chapters.flatMap { chap -> chap.lectures }
            }
        }
    }

    fun getAllDpps(): List<Dpp> {
        return _courses.value.flatMap { course ->
            course.subjects.flatMap { subj ->
                subj.chapters.flatMap { chap -> chap.dpps }
            }
        }
    }

    fun findDppById(dppId: String): Dpp? {
        return getAllDpps().find { it.id == dppId }
    }

    fun findLectureById(lectureId: String): Lecture? {
        return getAllLectures().find { it.id == lectureId }
    }

    fun getEnrolledCourseIds(): List<String> {
        return _courses.value.filter { it.isEnrolled }.map { it.id }
    }

    fun getBookmarkedLectureIds(): List<String> {
        return getAllLectures().filter { it.isBookmarked }.map { it.id }
    }

    fun getCompletedLectureIds(): List<String> {
        return getAllLectures().filter { it.isCompleted }.map { it.id }
    }

    fun applyCloudProfile(doc: StudentProfileDoc) {
        _userProfile.update { current ->
            current.copy(
                name = doc.name.takeIf { it.isNotBlank() && it != "Arun Sharma" } ?: current.name,
                targetExam = doc.targetExam.ifBlank { current.targetExam },
                classLevel = doc.classLevel.ifBlank { current.classLevel },
                studentGoal = doc.studentGoal.ifBlank { current.studentGoal },
                bio = doc.bio.ifBlank { current.bio },
                currentStreakDays = doc.currentStreakDays.toInt(),
                totalDppsSolved = doc.totalDppsSolved.toInt(),
                overallAccuracy = doc.overallAccuracy.toInt(),
                lecturesWatched = doc.lecturesWatched.toInt(),
                studyHours = doc.studyHours.toFloat(),
                xp = doc.xp.toInt()
            )
        }
        val enrolledSet = doc.enrolledCourseIds.toSet()
        val bookmarkedSet = doc.bookmarkedLectureIds.toSet()
        val completedSet = doc.completedLectureIds.toSet()

        _courses.update { courseList ->
            courseList.map { course ->
                course.copy(
                    isEnrolled = if (enrolledSet.isNotEmpty()) course.id in enrolledSet else course.isEnrolled,
                    subjects = course.subjects.map { subj ->
                        subj.copy(
                            chapters = subj.chapters.map { chap ->
                                chap.copy(
                                    lectures = chap.lectures.map { lec ->
                                        lec.copy(
                                            isBookmarked = if (bookmarkedSet.isNotEmpty()) lec.id in bookmarkedSet else lec.isBookmarked,
                                            isCompleted = if (completedSet.isNotEmpty()) lec.id in completedSet else lec.isCompleted
                                        )
                                    }
                                )
                            }
                        )
                    }
                )
            }
        }
    }

    fun applyCloudDppAttempts(attempts: List<DppAttemptDoc>) {
        if (attempts.isEmpty()) return
        val attemptMap = attempts.associateBy { it.dppId }
        _courses.update { courseList ->
            courseList.map { course ->
                course.copy(
                    subjects = course.subjects.map { subj ->
                        subj.copy(
                            chapters = subj.chapters.map { chap ->
                                chap.copy(
                                    dpps = chap.dpps.map { dpp ->
                                        val cloudAttempt = attemptMap[dpp.id]
                                        if (cloudAttempt != null) {
                                            dpp.copy(
                                                status = if (cloudAttempt.status == "PENDING") DppStatus.PENDING else DppStatus.COMPLETED,
                                                score = cloudAttempt.score.toInt(),
                                                timeSpentSeconds = cloudAttempt.timeSpentSeconds.toInt(),
                                                completedAt = cloudAttempt.updatedAt?.toDate()?.time ?: dpp.completedAt
                                            )
                                        } else {
                                            dpp
                                        }
                                    }
                                )
                            }
                        )
                    }
                )
            }
        }
    }

    fun applyCloudDoubts(cloudDoubts: List<StudentDoubtDoc>) {
        if (cloudDoubts.isEmpty()) return
        val mapped = cloudDoubts
            .sortedByDescending { it.updatedAt?.seconds ?: 0L }
            .map { doc ->
                val subjectEnum = SubjectType.entries.find { it.name.equals(doc.subject, ignoreCase = true) }
                    ?: SubjectType.PHYSICS
                DoubtQuery(
                    id = doc.id.ifBlank { "d_cloud_${doc.questionText.hashCode()}" },
                    studentName = doc.studentName.ifBlank { _userProfile.value.name },
                    questionText = doc.questionText,
                    subject = subjectEnum,
                    status = doc.status,
                    facultyReply = doc.facultyReply,
                    timestamp = "Synced in Cloud"
                )
            }
        _doubts.update { existing ->
            val cloudIds = mapped.map { it.id }.toSet()
            val remainingLocal = existing.filterNot { it.id in cloudIds }
            mapped + remainingLocal
        }
    }

    private fun createInitialCourses(): List<CourseBatch> {
        return listOf(
            PwCourseApiService.buildPwPresetCourseBatch(
                presetKey = "LAKSHYA_JEE_2026",
                apiEndpointUrl = PwCourseApiService.DEFAULT_PW_BATCHES_API_URL
            ),
            CourseBatch(
                id = "c_hacking_2027",
                title = "HACKING COURSE 2027",
                category = "HACKER",
                tag = "HACKER",
                rating = 5.0f,
                enrolledCount = "Multiple plans: Infinity, Pro",
                price = "₹2,499",
                originalPrice = "₹8,999",
                instructorTeam = "Lead Hacker Mentor (Sunglasses & Blue-Green Emblem Faculty)",
                description = "Multiple plans inside: Infinity, Pro 👑 • Official HACKING COURSE 2027 with designated HACKER badge, hands-on Cyber Defense, Ethical Hacking labs, Network Security, Linux Mastery & Firebase Cloud Storage resources.",
                syllabusCoveragePercent = 100,
                isEnrolled = true,
                bannerColorHex = 0xFF78350F,
                bannerDrawableRes = R.drawable.hacking_course_2027_banner,
                availablePlans = listOf("Infinity", "Pro"),
                featureBadge = "HACKER",
                storageBucketUrl = "${ExternalStudyLinks.FIREBASE_STORAGE_BUCKET}/courses/hacking_course_2027",
                subjects = listOf(
                    SubjectDetail(
                        type = SubjectType.PHYSICS,
                        chapters = listOf(
                            Chapter(
                                id = "hack_ch1",
                                subject = SubjectType.PHYSICS,
                                title = "Module 01: Ethical Hacking & Network Defense",
                                order = 1,
                                lectures = listOf(
                                    Lecture(
                                        id = "lec_hack_2027_1",
                                        courseId = "c_hacking_2027",
                                        subject = SubjectType.PHYSICS,
                                        chapterId = "hack_ch1",
                                        chapterTitle = "Module 01: Ethical Hacking & Network Defense",
                                        title = "Lec 01: HACKING COURSE 2027 Orientation (Infinity & Pro Plans)",
                                        lectureNumber = 1,
                                        instructor = "Lead Hacker Mentor",
                                        durationMinutes = 60,
                                        progressSec = 0,
                                        isCompleted = false,
                                        isBookmarked = true,
                                        notesSummary = listOf(
                                            "Course Roadmap for HACKING COURSE 2027 — Infinity & Pro Crown Plans.",
                                            "OSI & TCP/IP 5-Layer Network Architecture, Packet Inspection & Firewall Rules.",
                                            "Setting up isolated Linux Virtual Labs & Cryptography Fundamentals (SHA-256, AES, RSA).",
                                            "Zero-Trust Security Architecture & Firebase Cloud Storage Access Control."
                                        ),
                                        timestamps = listOf(
                                            LectureTimestamp(0, "00:00", "Welcome to HACKING COURSE 2027 (Infinity & Pro)"),
                                            LectureTimestamp(720, "12:00", "Network Protocols, Ports & Defense Architecture"),
                                            LectureTimestamp(1800, "30:00", "Linux Terminal Commands & Security Auditing"),
                                            LectureTimestamp(2700, "45:00", "Hands-On Lab Setup & Cloud Storage Resources")
                                        ),
                                        dppId = "dpp_hack_2027_1"
                                    )
                                ),
                                dpps = listOf(
                                    Dpp(
                                        id = "dpp_hack_2027_1",
                                        number = 1,
                                        title = "DPP 01: Network Security, Cryptography & Linux Fundamentals",
                                        subject = SubjectType.PHYSICS,
                                        chapterTitle = "Module 01: Ethical Hacking & Network Defense",
                                        courseId = "c_hacking_2027",
                                        timeLimitMinutes = 15,
                                        status = DppStatus.PENDING,
                                        sourceBook = "HACKING COURSE 2027 (Infinity & Pro Lab Sheet)",
                                        questions = listOf(
                                            Question(
                                                id = "q_hack_1",
                                                questionText = "Which layer of the OSI model is primarily responsible for end-to-end encryption, TLS/SSL session formatting, and data representation?",
                                                options = listOf(
                                                    "(a) Network Layer (Layer 3)",
                                                    "(b) Transport Layer (Layer 4)",
                                                    "(c) Presentation Layer (Layer 6)",
                                                    "(d) Data Link Layer (Layer 2)"
                                                ),
                                                correctOptionIndex = 2,
                                                explanation = "The Presentation Layer (Layer 6) handles data translation, character encoding, compression, and encryption/decryption before passing data to the Application Layer.",
                                                hint = "Think of the layer right below the Application Layer (Layer 7).",
                                                topic = "OSI Security Architecture"
                                            ),
                                            Question(
                                                id = "q_hack_2",
                                                questionText = "Choose the correct statements regarding Asymmetric (Public-Key) Cryptography (Objective II - Multiple Correct):",
                                                options = listOf(
                                                    "(a) It uses a mathematically linked pair of Public and Private keys.",
                                                    "(b) The Private key must never be shared over an untrusted channel.",
                                                    "(c) RSA and Elliptic Curve Cryptography (ECC) are asymmetric algorithms.",
                                                    "(d) Both encryption and decryption always use the exact same secret key."
                                                ),
                                                correctOptionIndex = 0,
                                                correctOptionIndices = setOf(0, 1, 2),
                                                isMultiSelect = true,
                                                explanation = "Statements (a), (b), and (c) are true. Asymmetric cryptography uses a public key and a private key (e.g., RSA, ECC). Statement (d) describes symmetric cryptography (like AES).",
                                                hint = "Asymmetric means two distinct keys are involved.",
                                                topic = "Cryptography"
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            ),
            CourseBatch(
                id = "c_arjuna_jee",
                title = "Arjuna JEE Main & Advanced 2026",
                category = "JEE Main & Adv",
                tag = "Class 11th + 12th Comprehensive",
                rating = 4.9f,
                enrolledCount = "48,200+ Students",
                price = "₹3,999",
                originalPrice = "₹12,000",
                instructorTeam = "Prof. Verma (Physics) • Dr. Sneha (Chemistry) • Amit Sir (Maths)",
                description = "Master JEE Main & Advanced with India's top faculty. Includes daily live classes, recorded 4K lectures, structured daily DPPs with video solutions, all India mock tests, and 24/7 doubts discussion.",
                syllabusCoveragePercent = 42,
                isEnrolled = true,
                bannerColorHex = 0xFF4F46E5,
                subjects = listOf(
                    SubjectDetail(
                        type = SubjectType.PHYSICS,
                        chapters = listOf(
                            Chapter(
                                id = "phy_ch1",
                                subject = SubjectType.PHYSICS,
                                title = "Kinematics in 1D & 2D",
                                order = 1,
                                lectures = listOf(
                                    Lecture(
                                        id = "lec_phy_1",
                                        courseId = "c_arjuna_jee",
                                        subject = SubjectType.PHYSICS,
                                        chapterId = "phy_ch1",
                                        chapterTitle = "Kinematics in 1D & 2D",
                                        title = "Lec 01: Frame of Reference, Distance & Displacement",
                                        lectureNumber = 1,
                                        instructor = "Prof. Verma",
                                        durationMinutes = 52,
                                        progressSec = 3120,
                                        isCompleted = true,
                                        isBookmarked = false,
                                        notesSummary = listOf(
                                            "Displacement is the shortest straight line vector connecting initial and final points.",
                                            "Distance >= |Displacement|. Equality holds ONLY during unidirectional straight line motion without turning.",
                                            "Average velocity v_avg = Δr / Δt. Instantaneous velocity v = dr/dt.",
                                            "Rate of change of speed is |a_tangential| = d|v|/dt, which is different from |dv/dt|."
                                        ),
                                        timestamps = listOf(
                                            LectureTimestamp(0, "00:00", "Introduction & Vectors Recap"),
                                            LectureTimestamp(620, "10:20", "1D Motion & Position Vector"),
                                            LectureTimestamp(1540, "25:40", "Derivation of Calculus Equations"),
                                            LectureTimestamp(2480, "41:20", "JEE Advanced Multi-Step Question")
                                        ),
                                        dppId = "dpp_phy_1"
                                    ),
                                    Lecture(
                                        id = "lec_phy_2",
                                        courseId = "c_arjuna_jee",
                                        subject = SubjectType.PHYSICS,
                                        chapterId = "phy_ch1",
                                        chapterTitle = "Kinematics in 1D & 2D",
                                        title = "Lec 02: Projectile Motion & Trajectory Equation",
                                        lectureNumber = 2,
                                        instructor = "Prof. Verma",
                                        durationMinutes = 58,
                                        progressSec = 1800,
                                        isCompleted = false,
                                        isBookmarked = true,
                                        notesSummary = listOf(
                                            "Time of Flight: T = (2 u sin θ) / g",
                                            "Maximum Height: H_max = (u² sin² θ) / (2g)",
                                            "Horizontal Range: R = (u² sin 2θ) / g. Max range occurs at θ = 45°.",
                                            "Equation of trajectory: y = x tan θ - (g x²)/(2 u² cos² θ) = x tan θ (1 - x/R).",
                                            "Radius of curvature at apex: ρ = (u cos θ)² / g."
                                        ),
                                        timestamps = listOf(
                                            LectureTimestamp(0, "00:00", "Horizontal & Vertical Independence"),
                                            LectureTimestamp(720, "12:00", "Derivation of Range & Max Height"),
                                            LectureTimestamp(1800, "30:00", "Trajectory Formula (y = x tan θ (1 - x/R))"),
                                            LectureTimestamp(2880, "48:00", "Inclined Plane Projectile Motion")
                                        ),
                                        dppId = "dpp_phy_2"
                                    ),
                                    Lecture(
                                        id = "lec_phy_3",
                                        courseId = "c_arjuna_jee",
                                        subject = SubjectType.PHYSICS,
                                        chapterId = "phy_ch1",
                                        chapterTitle = "Kinematics in 1D & 2D",
                                        title = "Lec 03: Relative Velocity & Rain-Man Problems",
                                        lectureNumber = 3,
                                        instructor = "Prof. Verma",
                                        durationMinutes = 64,
                                        progressSec = 0,
                                        isCompleted = false,
                                        isBookmarked = false,
                                        notesSummary = listOf(
                                            "Relative velocity formula: v_AB = v_A - v_B.",
                                            "River-Swimmer: Shortest path when swimmer angles upstream at sin α = v_river / v_swimmer.",
                                            "Rain-Man: Velocity of rain wrt man v_rm = v_rain - v_man."
                                        ),
                                        timestamps = listOf(
                                            LectureTimestamp(0, "00:00", "Concept of Relative Frame"),
                                            LectureTimestamp(840, "14:00", "Rain-Man Vector Diagrams"),
                                            LectureTimestamp(1920, "32:00", "River Boat Crossing Time vs Drift"),
                                            LectureTimestamp(3100, "51:40", "Shortest Distance of Approach")
                                        ),
                                        dppId = "dpp_phy_1"
                                    )
                                ),
                                dpps = listOf(
                                    Dpp(
                                        id = "dpp_phy_1",
                                        number = 1,
                                        title = "DPP 01: Distance, Displacement & Acceleration",
                                        subject = SubjectType.PHYSICS,
                                        chapterTitle = "Kinematics in 1D & 2D",
                                        courseId = "c_arjuna_jee",
                                        timeLimitMinutes = 12,
                                        status = DppStatus.COMPLETED,
                                        score = 3,
                                        timeSpentSeconds = 540,
                                        userAnswers = mapOf(0 to 1, 1 to 0, 2 to 2),
                                        questions = listOf(
                                            Question(
                                                id = "q_p1_1",
                                                questionText = "A particle moves along a semicircular path of radius R from point A to point B. What is the ratio of distance traveled to the magnitude of displacement?",
                                                options = listOf("2 : π", "π : 2", "1 : 1", "π : 1"),
                                                correctOptionIndex = 1,
                                                explanation = "Distance traveled along the semicircle is πR. The straight-line displacement between diameter endpoints is 2R. Hence the ratio is πR / 2R = π : 2.",
                                                hint = "Calculate arc length for distance, and diameter length for displacement.",
                                                topic = "Displacement vs Distance"
                                            ),
                                            Question(
                                                id = "q_p1_2",
                                                questionText = "The displacement x of a particle varies with time t as x = 4t² - 16t + 12. At what time does the particle momentarily come to rest?",
                                                options = listOf("t = 2 s", "t = 4 s", "t = 1 s", "t = 0.5 s"),
                                                correctOptionIndex = 0,
                                                explanation = "Velocity v = dx/dt = d(4t² - 16t + 12)/dt = 8t - 16. Setting v = 0 gives 8t = 16 => t = 2 seconds.",
                                                hint = "Differentiate displacement with respect to time to get velocity.",
                                                topic = "Instantaneous Velocity"
                                            ),
                                            Question(
                                                id = "q_p1_3",
                                                questionText = "A car accelerates uniformly from rest to 72 km/h in 10 seconds. What is the distance covered during this interval?",
                                                options = listOf("50 m", "200 m", "100 m", "150 m"),
                                                correctOptionIndex = 2,
                                                explanation = "Initial velocity u = 0. Final velocity v = 72 * (5/18) = 20 m/s. Acceleration a = (v - u)/t = 20/10 = 2 m/s². Distance s = ut + 0.5 a t² = 0 + 0.5 * 2 * 10² = 100 m.",
                                                hint = "First convert speed from km/h to m/s by multiplying by 5/18.",
                                                topic = "Equations of Motion"
                                            )
                                        )
                                    ),
                                    Dpp(
                                        id = "dpp_phy_2",
                                        number = 2,
                                        title = "DPP 02: Projectile Motion at Angles",
                                        subject = SubjectType.PHYSICS,
                                        chapterTitle = "Kinematics in 1D & 2D",
                                        courseId = "c_arjuna_jee",
                                        timeLimitMinutes = 15,
                                        status = DppStatus.PENDING,
                                        questions = listOf(
                                            Question(
                                                id = "q_p2_1",
                                                questionText = "A projectile is thrown with initial speed u = 40 m/s at an angle θ = 30° with the horizontal (g = 10 m/s²). What is its maximum height achieved?",
                                                options = listOf("10 m", "20 m", "40 m", "15 m"),
                                                correctOptionIndex = 1,
                                                explanation = "H = (u² sin² θ) / (2g) = (40² * sin² 30°) / (2 * 10) = (1600 * 0.25) / 20 = 400 / 20 = 20 meters.",
                                                hint = "Use H = u_y² / (2g).",
                                                topic = "Projectile Maximum Height"
                                            ),
                                            Question(
                                                id = "q_p2_2",
                                                questionText = "For two complementary angles of projection θ and (90° - θ) with the same initial launch speed u, the horizontal ranges R1 and R2 satisfy:",
                                                options = listOf("R1 > R2", "R1 < R2", "R1 = R2", "R1 + R2 = u²/g"),
                                                correctOptionIndex = 2,
                                                explanation = "Range R = (u² sin 2θ)/g. For 90° - θ, sin(2(90° - θ)) = sin(180° - 2θ) = sin 2θ. Hence R1 = R2.",
                                                hint = "Recall the trigonometric identity sin(180° - 2θ) = sin 2θ.",
                                                topic = "Complementary Angles Range"
                                            ),
                                            Question(
                                                id = "q_p2_3",
                                                questionText = "At the highest point of its trajectory, what is the angle between the velocity vector and acceleration vector of a projectile?",
                                                options = listOf("0°", "45°", "90°", "180°"),
                                                correctOptionIndex = 2,
                                                explanation = "At the peak of trajectory, the vertical velocity component is zero, so velocity is purely horizontal (u cos θ î). Gravity is always vertically downward (-g ĵ). The angle between them is exactly 90°.",
                                                hint = "Consider the horizontal direction of motion and the downward gravity.",
                                                topic = "Trajectory Kinematics"
                                            )
                                        )
                                    )
                                )
                            )
                        ) + HcVermaPhysicsDpps.getHcVermaChapters()
                    ),
                    SubjectDetail(
                        type = SubjectType.CHEMISTRY,
                        chapters = listOf(
                            Chapter(
                                id = "chem_ch1",
                                subject = SubjectType.CHEMISTRY,
                                title = "Chemical Bonding & Molecular Structure",
                                order = 1,
                                lectures = listOf(
                                    Lecture(
                                        id = "lec_chem_1",
                                        courseId = "c_arjuna_jee",
                                        subject = SubjectType.CHEMISTRY,
                                        chapterId = "chem_ch1",
                                        chapterTitle = "Chemical Bonding & Molecular Structure",
                                        title = "Lec 01: VSEPR Theory, Hybridization & Shapes",
                                        lectureNumber = 1,
                                        instructor = "Dr. Sneha Sen",
                                        durationMinutes = 60,
                                        progressSec = 3600,
                                        isCompleted = true,
                                        isBookmarked = true,
                                        notesSummary = listOf(
                                            "Steric Number = (Number of σ bonds) + (Number of lone pairs on central atom).",
                                            "SN = 2: sp (Linear, 180°), SN = 3: sp² (Trigonal Planar, 120°).",
                                            "SN = 4: sp³ (Tetrahedral 109.5°, Pyramidal 107° for NH3, Bent 104.5° for H2O).",
                                            "Repulsion Order: Lone Pair-Lone Pair > Lone Pair-Bond Pair > Bond Pair-Bond Pair.",
                                            "SN = 5: sp³d (Trigonal Bipyramidal, lone pairs prefer equatorial positions)."
                                        ),
                                        timestamps = listOf(
                                            LectureTimestamp(0, "00:00", "Lewis Structures Limitations"),
                                            LectureTimestamp(900, "15:00", "VSEPR Postulates & Repulsion"),
                                            LectureTimestamp(2100, "35:00", "Determining Hybridization via Steric Number"),
                                            LectureTimestamp(3000, "50:00", "Bent Rule & Axial Bond Lengths")
                                        ),
                                        dppId = "dpp_chem_1"
                                    )
                                ),
                                dpps = listOf(
                                    Dpp(
                                        id = "dpp_chem_1",
                                        number = 1,
                                        title = "DPP 01: Hybridization & Molecular Geometries",
                                        subject = SubjectType.CHEMISTRY,
                                        chapterTitle = "Chemical Bonding & Molecular Structure",
                                        courseId = "c_arjuna_jee",
                                        timeLimitMinutes = 15,
                                        status = DppStatus.PENDING,
                                        questions = listOf(
                                            Question(
                                                id = "q_c1_1",
                                                questionText = "What is the hybridization and molecular geometry of Xenon Tetrafluoride (XeF4)?",
                                                options = listOf("sp³d, See-saw", "sp³d², Square Planar", "sp³d², Octahedral", "sp³, Tetrahedral"),
                                                correctOptionIndex = 1,
                                                explanation = "In XeF4, Xenon has 8 valence electrons. 4 form σ-bonds with F atoms, leaving 4 non-bonding electrons (2 lone pairs). Steric number = 4 + 2 = 6 (sp³d²). With 2 lone pairs opposite each other, the shape is Square Planar.",
                                                hint = "Count total valence electrons of Xe (8) and subtract bonding pairs.",
                                                topic = "VSEPR and Noble Gas Compounds"
                                            ),
                                            Question(
                                                id = "q_c1_2",
                                                questionText = "Which of the following molecules possesses a zero dipole moment (non-polar)?",
                                                options = listOf("NH3", "BF3", "H2O", "SO2"),
                                                correctOptionIndex = 1,
                                                explanation = "BF3 has trigonal planar geometry with 120° bond angles. The three identical B-F dipole vectors cancel each other out symmetrically, resulting in a net dipole moment μ = 0.",
                                                hint = "Check for symmetrical cancellation in planar structures.",
                                                topic = "Dipole Moment"
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    ),
                    SubjectDetail(
                        type = SubjectType.MATHEMATICS,
                        chapters = listOf(
                            Chapter(
                                id = "math_ch1",
                                subject = SubjectType.MATHEMATICS,
                                title = "Quadratic Equations & Complex Roots",
                                order = 1,
                                lectures = listOf(
                                    Lecture(
                                        id = "lec_math_1",
                                        courseId = "c_arjuna_jee",
                                        subject = SubjectType.MATHEMATICS,
                                        chapterId = "math_ch1",
                                        chapterTitle = "Quadratic Equations & Complex Roots",
                                        title = "Lec 01: Relations between Roots & Coefficients",
                                        lectureNumber = 1,
                                        instructor = "Amit Sir",
                                        durationMinutes = 55,
                                        progressSec = 1200,
                                        isCompleted = false,
                                        isBookmarked = false,
                                        notesSummary = listOf(
                                            "For ax² + bx + c = 0, Sum of roots (α + β) = -b/a, Product (αβ) = c/a.",
                                            "Discriminant D = b² - 4ac determines nature of roots (D > 0 real distinct, D = 0 equal, D < 0 imaginary conjugate pairs).",
                                            "Formation of equation: x² - (Sum)x + (Product) = 0.",
                                            "Condition for common root between two quadratics."
                                        ),
                                        timestamps = listOf(
                                            LectureTimestamp(0, "00:00", "Quadratic Basics & Vieta Formulas"),
                                            LectureTimestamp(780, "13:00", "Symmetric Expressions of Roots (α³ + β³)"),
                                            LectureTimestamp(1860, "31:00", "Location of Roots & Graph Transformations"),
                                            LectureTimestamp(2700, "45:00", "JEE Advanced 2024 Past Year Problem")
                                        ),
                                        dppId = "dpp_math_1"
                                    )
                                ),
                                dpps = listOf(
                                    Dpp(
                                        id = "dpp_math_1",
                                        number = 1,
                                        title = "DPP 01: Roots, Vieta & Quadratic Graphs",
                                        subject = SubjectType.MATHEMATICS,
                                        chapterTitle = "Quadratic Equations & Complex Roots",
                                        courseId = "c_arjuna_jee",
                                        timeLimitMinutes = 15,
                                        status = DppStatus.PENDING,
                                        questions = listOf(
                                            Question(
                                                id = "q_m1_1",
                                                questionText = "If α and β are the roots of the equation x² - 5x + 6 = 0, find the value of α² + β².",
                                                options = listOf("13", "19", "25", "12"),
                                                correctOptionIndex = 0,
                                                explanation = "Here a=1, b=-5, c=6. α + β = 5 and αβ = 6. Now α² + β² = (α + β)² - 2αβ = (5)² - 2(6) = 25 - 12 = 13.",
                                                hint = "Express α² + β² in terms of (α + β) and αβ.",
                                                topic = "Symmetric Roots"
                                            ),
                                            Question(
                                                id = "q_m1_2",
                                                questionText = "For what value of k does the quadratic equation 4x² + kx + 9 = 0 have real and equal roots?",
                                                options = listOf("± 6", "± 12", "± 36", "± 18"),
                                                correctOptionIndex = 1,
                                                explanation = "For real and equal roots, discriminant D = b² - 4ac = 0. Here k² - 4(4)(9) = 0 => k² - 144 = 0 => k = ±12.",
                                                hint = "Set the discriminant D = 0.",
                                                topic = "Nature of Roots"
                                            )
                                        )
                                    )
                                )
                            )
                        ) + CengageAlgebraDpps.getCengageChapters()
                    )
                )
            ),
            CourseBatch(
                id = "c_lakshya_neet",
                title = "Lakshya NEET-UG 2025/26",
                category = "NEET-UG",
                tag = "Medical Rankers Target",
                rating = 4.8f,
                enrolledCount = "39,500+ Students",
                price = "₹3,499",
                originalPrice = "₹10,000",
                instructorTeam = "Dr. R.K. Mishra (Bio) • Dr. Sneha (Chem) • Prof. Verma (Phy)",
                description = "Strictly NCERT-aligned medical entrance course. Detailed 3D biological animations, line-by-line NCERT breakdowns, assertion-reason drills, and 100+ topic-wise DPP sets.",
                syllabusCoveragePercent = 65,
                isEnrolled = false,
                bannerColorHex = 0xFF0284C7,
                subjects = listOf(
                    SubjectDetail(
                        type = SubjectType.BIOLOGY,
                        chapters = listOf(
                            Chapter(
                                id = "bio_ch1",
                                subject = SubjectType.BIOLOGY,
                                title = "Cell Cycle & Cell Division",
                                order = 1,
                                lectures = listOf(
                                    Lecture(
                                        id = "lec_bio_1",
                                        courseId = "c_lakshya_neet",
                                        subject = SubjectType.BIOLOGY,
                                        chapterId = "bio_ch1",
                                        chapterTitle = "Cell Cycle & Cell Division",
                                        title = "Lec 01: Mitosis, Interphase (G1, S, G2) & Karyokinesis",
                                        lectureNumber = 1,
                                        instructor = "Dr. R.K. Mishra",
                                        durationMinutes = 48,
                                        progressSec = 0,
                                        isCompleted = false,
                                        isBookmarked = false,
                                        notesSummary = listOf(
                                            "Interphase constitutes >95% of cell cycle duration.",
                                            "S Phase (Synthesis): DNA replication occurs, amount of DNA doubles (2C to 4C) but chromosome number remains 2n.",
                                            "G0 Phase (Quiescent stage): Cells remain metabolically active but do not proliferate (e.g. heart cells).",
                                            "Colchicine inhibits spindle fiber formation at Metaphase."
                                        ),
                                        timestamps = listOf(
                                            LectureTimestamp(0, "00:00", "Cell Division Significance"),
                                            LectureTimestamp(600, "10:00", "Interphase Stages Breakdown"),
                                            LectureTimestamp(1500, "25:00", "DNA Content vs Chromosome Count"),
                                            LectureTimestamp(2300, "38:20", "Mitosis Prophase & Metaphase Plates")
                                        ),
                                        dppId = "dpp_bio_1"
                                    )
                                ),
                                dpps = listOf(
                                    Dpp(
                                        id = "dpp_bio_1",
                                        number = 1,
                                        title = "DPP 01: Cell Cycle Stages & DNA Replication",
                                        subject = SubjectType.BIOLOGY,
                                        chapterTitle = "Cell Cycle & Cell Division",
                                        courseId = "c_lakshya_neet",
                                        timeLimitMinutes = 10,
                                        status = DppStatus.PENDING,
                                        questions = listOf(
                                            Question(
                                                id = "q_b1_1",
                                                questionText = "During which phase of the cell cycle does DNA replication and duplication of centriole occur in animal cells?",
                                                options = listOf("G1 phase", "S phase", "G2 phase", "M phase"),
                                                correctOptionIndex = 1,
                                                explanation = "In the S (synthesis) phase, DNA replication occurs inside the nucleus, and the centriole duplicates in the cytoplasm.",
                                                hint = "S stands for synthesis of genetic material.",
                                                topic = "Interphase S Phase"
                                            ),
                                            Question(
                                                id = "q_b1_2",
                                                questionText = "Which stage of mitosis is best suited to study the morphology, size, and shape of chromosomes?",
                                                options = listOf("Prophase", "Metaphase", "Anaphase", "Telophase"),
                                                correctOptionIndex = 1,
                                                explanation = "At metaphase, chromosomes are condensed to the maximum extent and aligned along the equatorial metaphase plate, making their morphology easiest to examine under a microscope.",
                                                hint = "Look for chromosomes arranged along the equatorial plate.",
                                                topic = "Metaphase Characteristics"
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            ),
            CourseBatch(
                id = "c_udaan_10th",
                title = "Udaan Class 10th Board Booster",
                category = "Class 10 Board",
                tag = "100/100 Board Target",
                rating = 4.9f,
                enrolledCount = "62,000+ Students",
                price = "₹1,499",
                originalPrice = "₹5,000",
                instructorTeam = "Master Faculty Panel (Science & Maths)",
                description = "Complete CBSE & State Board syllabus preparation with sample question papers, formula sheets, chapter revision marathons, and weekly subjective tests.",
                syllabusCoveragePercent = 80,
                isEnrolled = false,
                bannerColorHex = 0xFF059669,
                subjects = emptyList()
            )
        )
    }

    private fun createInitialMockTests(): List<TestExam> {
        return listOf(
            TestExam(
                id = "test_0",
                title = "All-India Test Series (Mission JEET Portal)",
                pattern = "Live Online Test Series • DeltaStudy",
                questionsCount = 75,
                durationMinutes = 180,
                totalMarks = 300,
                status = "Live Portal",
                scheduledDate = "Open Now",
                externalUrl = ExternalStudyLinks.ALL_INDIA_TEST_SERIES_URL
            ),
            TestExam(
                id = "test_1",
                title = "ARCSTFUG All-India JEE Main Mock 01",
                pattern = "JEE Main (NTA Pattern)",
                questionsCount = 75,
                durationMinutes = 180,
                totalMarks = 300,
                status = "Live Now",
                scheduledDate = "Active Today",
                externalUrl = ExternalStudyLinks.ALL_INDIA_TEST_SERIES_URL
            ),
            TestExam(
                id = "test_2",
                title = "NEET Speed Test - Full Class 11th Syllabus",
                pattern = "NEET-UG (200 Questions)",
                questionsCount = 200,
                durationMinutes = 200,
                totalMarks = 720,
                status = "Upcoming",
                scheduledDate = "Sunday 10:00 AM",
                externalUrl = ExternalStudyLinks.ALL_INDIA_TEST_SERIES_URL
            ),
            TestExam(
                id = "test_3",
                title = "Kinematics & Newton's Laws Chapter Test",
                pattern = "Subject Minor Test",
                questionsCount = 30,
                durationMinutes = 60,
                totalMarks = 120,
                status = "Available Anytime",
                scheduledDate = "Self-Paced",
                externalUrl = ExternalStudyLinks.ALL_INDIA_TEST_SERIES_URL
            )
        )
    }

    private fun createInitialDoubts(): List<DoubtQuery> {
        return listOf(
            DoubtQuery(
                id = "d1",
                studentName = "Rohan Gupta",
                questionText = "Why does a body rolling down an inclined plane have both rotational and translational kinetic energy?",
                subject = SubjectType.PHYSICS,
                status = "Answered by Faculty",
                facultyReply = "When a body rolls without slipping, every point undergoes translation of center of mass (1/2 M v_cm²) and rotation about CM (1/2 I_cm ω²). Since v_cm = Rω, Total KE = 1/2 M v_cm² (1 + k²/R²).",
                timestamp = "2 hours ago"
            ),
            DoubtQuery(
                id = "d2",
                studentName = "Pooja Verma",
                questionText = "How do we distinguish between sp³d hybrid shapes like See-saw, T-shaped, and Linear?",
                subject = SubjectType.CHEMISTRY,
                status = "Answered by Faculty",
                facultyReply = "In sp³d, lone pairs strictly occupy equatorial positions to minimize 90° LP-BP repulsions: 1 LP -> See-saw (SF4), 2 LP -> T-shaped (ClF3), 3 LP -> Linear (XeF2).",
                timestamp = "5 hours ago"
            )
        )
    }

    private fun createInitialStudyMaterials(): List<StudyMaterial> {
        return listOf(
            StudyMaterial(
                id = "mat_arcstfug_web_portal",
                title = "ARCSTFUG Official Web App Portal",
                subject = SubjectType.PHYSICS,
                chapterTitle = "Complete Online Study Portal & Live Classes",
                courseId = "c_arjuna_jee",
                category = "Official Web Portal",
                description = "Access the official ARCSTFUG Web Platform anytime with one tap.",
                resourceUrl = ExternalStudyLinks.ARCSTFUG_WEB_PORTAL_URL,
                authorName = "Abhishek Kumar & Anirudhh Mishra",
                uploadedAtLabel = "Official Web App"
            ),
            StudyMaterial(
                id = "mat_phy_vault_dc_pandey",
                title = "📘 DC Pandey — ⚡ JEE PHYSICS VAULT | DROP #01",
                subject = SubjectType.PHYSICS,
                chapterTitle = "Mechanics, Electricity, Magnetism, Optics & Modern Physics",
                courseId = "c_arjuna_jee",
                category = "JEE Physics Vault",
                description = "Complete DC Pandey JEE Physics series Google Drive folder for JEE Main & Advanced problem solving.",
                resourceUrl = ExternalStudyLinks.DC_PANDEY_DRIVE_URL,
                authorName = "Abhishek Kumar • JEE Physics Vault",
                uploadedAtLabel = "DROP #01"
            ),
            StudyMaterial(
                id = "mat_phy_vault_hc_verma",
                title = "📗 HC Verma — ⚡ JEE PHYSICS VAULT | DROP #01",
                subject = SubjectType.PHYSICS,
                chapterTitle = "Concepts of Physics Vol 1 & Vol 2 + Solutions",
                courseId = "c_arjuna_jee",
                category = "JEE Physics Vault",
                description = "Complete H.C. Verma Concepts of Physics Google Drive folder for conceptual mastery and olympiad/JEE prep.",
                resourceUrl = ExternalStudyLinks.HC_VERMA_DRIVE_URL,
                authorName = "Abhishek Kumar • JEE Physics Vault",
                uploadedAtLabel = "DROP #01"
            ),
            StudyMaterial(
                id = "mat_phy_vault_irodov",
                title = "📕 I.E. Irodov — ⚡ JEE PHYSICS VAULT | DROP #01",
                subject = SubjectType.PHYSICS,
                chapterTitle = "Problems in General Physics (Advanced Level)",
                courseId = "c_arjuna_jee",
                category = "JEE Physics Vault",
                description = "I.E. Irodov advanced physics problems & solutions Google Drive folder for Top 100 AIR JEE Advanced rankers.",
                resourceUrl = ExternalStudyLinks.IRODOV_DRIVE_URL,
                authorName = "Abhishek Kumar • JEE Physics Vault",
                uploadedAtLabel = "DROP #01"
            ),
            StudyMaterial(
                id = "mat_ejee_ioc_om_pandey",
                title = "📘 Inorganic Chemistry (IOC) – Om Pandey Sir",
                subject = SubjectType.CHEMISTRY,
                chapterTitle = "Complete Inorganic Chemistry EJEE Book PDF",
                courseId = "c_arjuna_jee",
                category = "EJEE Books PDF",
                description = "All-in-One EJEE Inorganic Chemistry preparation PDF by Om Pandey Sir — access and download anytime.",
                resourceUrl = ExternalStudyLinks.IOC_OM_PANDEY_URL,
                authorName = "Om Pandey Sir",
                uploadedAtLabel = "EJEE Books PDF"
            ),
            StudyMaterial(
                id = "mat_ejee_oc_rohit_agarwal",
                title = "📗 Organic Chemistry – Rohit Agarwal Sir",
                subject = SubjectType.CHEMISTRY,
                chapterTitle = "GOC, Isomerism, Reaction Mechanisms & Named Reactions",
                courseId = "c_arjuna_jee",
                category = "EJEE Books PDF",
                description = "Complete Organic Chemistry EJEE preparation PDF by Rohit Agarwal Sir (RA Sir) for JEE Main & Advanced.",
                resourceUrl = ExternalStudyLinks.OC_ROHIT_AGARWAL_URL,
                authorName = "Rohit Agarwal Sir",
                uploadedAtLabel = "EJEE Books PDF"
            ),
            StudyMaterial(
                id = "mat_ejee_physical_chem",
                title = "📙 Physical Chemistry – Complete EJEE Book PDF",
                subject = SubjectType.CHEMISTRY,
                chapterTitle = "Mole Concept, Thermodynamics, Equilibrium, Electrochemistry & Kinetics",
                courseId = "c_arjuna_jee",
                category = "EJEE Books PDF",
                description = "Comprehensive Physical Chemistry EJEE book PDF with formulas, solved numericals, and practice sheets.",
                resourceUrl = ExternalStudyLinks.PHYSICAL_CHEM_URL,
                authorName = "EJEE Chemistry Faculty",
                uploadedAtLabel = "EJEE Books PDF"
            ),
            StudyMaterial(
                id = "mat_ejee_maths_sachin_sir",
                title = "📕 Maths (Class 11) – Sachin Sir",
                subject = SubjectType.MATHEMATICS,
                chapterTitle = "Class 11 Complete JEE Mathematics Book PDF",
                courseId = "c_arjuna_jee",
                category = "EJEE Books PDF",
                description = "Class 11 JEE Mathematics complete preparation PDF book by Sachin Sir.",
                resourceUrl = ExternalStudyLinks.MATHS_SACHIN_SIR_URL,
                authorName = "Sachin Sir",
                uploadedAtLabel = "EJEE Books PDF"
            ),
            StudyMaterial(
                id = "mat_ejee_skc_class_11",
                title = "📓 SKC Class 11 Full Book – Dhirendra Kumar",
                subject = SubjectType.PHYSICS,
                chapterTitle = "Class 11 Full Book PDF (SKC)",
                courseId = "c_arjuna_jee",
                category = "EJEE Books PDF",
                description = "SKC Class 11 Full Book PDF by Dhirendra Kumar for complete foundation & JEE preparation.",
                resourceUrl = ExternalStudyLinks.SKC_CLASS_11_BOOK_URL,
                authorName = "Dhirendra Kumar",
                uploadedAtLabel = "EJEE Books PDF"
            ),
            StudyMaterial(
                id = "mat_allen_modules",
                title = "📂 Allen Modules — Complete Google Drive Folder",
                subject = SubjectType.PHYSICS,
                chapterTitle = "Physics, Chemistry & Mathematics Allen Kota Modules",
                courseId = "c_arjuna_jee",
                category = "Allen Study Material",
                description = "Complete Allen Kota chapterwise study modules with theory, illustrations, and exercise sheets.",
                resourceUrl = ExternalStudyLinks.ALLEN_MODULES_URL,
                authorName = "Allen Study Vault",
                uploadedAtLabel = "Official Drive"
            ),
            StudyMaterial(
                id = "mat_allen_material",
                title = "📂 Allen Material — Practice Sheets, Race & Handbooks",
                subject = SubjectType.CHEMISTRY,
                chapterTitle = "Complete Allen Classroom Study Material & Question Banks",
                courseId = "c_arjuna_jee",
                category = "Allen Study Material",
                description = "Allen Kota complete study material folder including Race, chapterwise sheets, and revision packages.",
                resourceUrl = ExternalStudyLinks.ALLEN_MATERIAL_URL,
                authorName = "Allen Study Vault",
                uploadedAtLabel = "Official Drive"
            ),
            StudyMaterial(
                id = "mat_jee_adv_pyq_2010_2025",
                title = "📚 JEE Advanced Previous Year Question Papers (2010–2025)",
                subject = SubjectType.MATHEMATICS,
                chapterTitle = "Year-wise Original Papers (2010 to 2025)",
                courseId = "c_arjuna_jee",
                category = "JEE Advanced PYQs",
                description = "Year-wise Question Papers (2010–2025) • Ideal for Revision & Mock Practice • Useful for Understanding Exam Trends.",
                resourceUrl = ExternalStudyLinks.JEE_ADV_PYQ_2010_2025_URL,
                authorName = "Abhishek Kumar • JEE Vault",
                uploadedAtLabel = "2010–2025 PYQs"
            ),
            StudyMaterial(
                id = "mat_phy_drive_vault",
                title = "Physics Complete Study Material (Google Drive Vault)",
                subject = SubjectType.PHYSICS,
                chapterTitle = "All Chapters (Mechanics, Optics, Waves & Thermodynamics)",
                courseId = "c_arjuna_jee",
                category = "Google Drive Folder",
                description = "Chapterwise Handwritten Notes, H.C. Verma Part 1 Solutions, Formula Sheets, PYQ Booklets & Video Lectures Folder.",
                resourceUrl = ExternalStudyLinks.PHYSICS_DRIVE_URL,
                authorName = "Prof. H.C. Verma & ARCSTFUG Physics Faculty",
                uploadedAtLabel = "Official Vault"
            ),
            StudyMaterial(
                id = "mat_aits_mission_jeet",
                title = "All-India Test Series (Mission JEET CBT Exam Portal)",
                subject = SubjectType.CHEMISTRY,
                chapterTitle = "Full Syllabus CBT Mock Tests",
                courseId = "c_arjuna_jee",
                category = "Test Series Link",
                description = "Live NTA-pattern JEE & NEET Computer Based Test Series with instant percentile and rank predictor.",
                resourceUrl = ExternalStudyLinks.ALL_INDIA_TEST_SERIES_URL,
                authorName = "ARCSTFUG Exam Cell",
                uploadedAtLabel = "Live Portal"
            )
        )
    }

    private fun createInitialAnnouncements(): List<AnnouncementNotice> {
        return listOf(
            AnnouncementNotice(
                id = "notice_arcstfug_web_ai_1",
                title = "🌐 ARCSTFUG Official Portal & AI Mentor Live!",
                message = "Welcome to ARCSTFUG Official Portal. Use our integrated ARCSTFUG AI Assistant below for instant JEE/NEET doubt solving, formula revision, and live exam notices.",
                categoryTag = "ARCSTFUG AI & PORTAL",
                authorName = "ARCSTFUG Official Portal",
                timestampLabel = "Pinned • Official",
                actionUrl = ExternalStudyLinks.ARCSTFUG_WEB_PORTAL_URL,
                isPinned = true
            ),
            AnnouncementNotice(
                id = "notice_pw_all_courses_2",
                title = "⚡ PW All Courses (Class 9–12, JEE & NEET 2026) Activated",
                message = "All 10 PW Batches — Lakshya JEE, Arjuna JEE, Prayas JEE, Yakeen NEET, Lakshya NEET, Arjuna NEET, Parishram Class 12, Uday Class 11, Udaan Class 10 & Neev Class 9 — are now bridged with Physics, Chemistry, Maths & Biology Lectures + DPPs.",
                categoryTag = "PW BATCH NOTICE",
                authorName = "ARCSTFUG Academic Cell",
                timestampLabel = "Live Update",
                actionUrl = ExternalStudyLinks.ARCSTFUG_WEB_PORTAL_URL,
                isPinned = true
            ),
            AnnouncementNotice(
                id = "notice_jee_neet_vault_3",
                title = "📚 New Study Vault Drop: DC Pandey, HC Verma, Irodov, EJEE & Allen Modules",
                message = "Direct Google Drive folders for Physics Vault Drop #01, Om Pandey Sir IOC, Rohit Agarwal Sir OC, Sachin Sir Maths, Allen Modules & JEE Advanced PYQs (2010–2025) are available on the Home Screen.",
                categoryTag = "STUDY VAULT",
                authorName = "Abhishek Kumar • Academic Vault",
                timestampLabel = "Today",
                actionUrl = ExternalStudyLinks.JEE_ADV_PYQ_2010_2025_URL,
                isPinned = false
            )
        )
    }
}

