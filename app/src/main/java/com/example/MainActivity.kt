package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.EduBottomBar
import com.example.ui.components.EduTopBar
import com.example.ui.screens.*
import com.example.ui.theme.ARCSTFUGTheme
import com.example.ui.viewmodel.AppRoutes
import com.example.ui.viewmodel.EduViewModel
import com.example.ui.viewmodel.MainNavTab
import com.example.util.configureAppCheck
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Configure App Check with launch intent before any UI or Firebase operations
        configureAppCheck(this, intent)

        enableEdgeToEdge()
        setContent {
            ARCSTFUGTheme {
                AppRoot()
            }
        }
    }
}

@Composable
fun AppRoot() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    val isFirebaseReady = remember(context) {
        runCatching { FirebaseApp.getApps(context).isNotEmpty() }.getOrDefault(false)
    }
    var currentUser by remember {
        mutableStateOf(
            if (isFirebaseReady) runCatching { Firebase.auth.currentUser }.getOrNull() else null
        )
    }
    var isOfflineSession by remember { mutableStateOf(false) }
    var customStudentName by remember { mutableStateOf<String?>(null) }
    var customStudentEmail by remember { mutableStateOf<String?>(null) }

    DisposableEffect(isFirebaseReady) {
        if (isFirebaseReady) {
            val listener = FirebaseAuth.AuthStateListener { auth ->
                currentUser = auth.currentUser
            }
            runCatching { Firebase.auth.addAuthStateListener(listener) }
            onDispose {
                runCatching { Firebase.auth.removeAuthStateListener(listener) }
            }
        } else {
            onDispose { }
        }
    }

    val activeUser = currentUser
    if (activeUser == null && !isOfflineSession) {
        AuthScreen(
            onAuthSuccess = {
                currentUser = if (isFirebaseReady) {
                    runCatching { Firebase.auth.currentUser }.getOrNull()
                } else {
                    null
                }
            },
            onContinueOffline = {
                isOfflineSession = true
            },
            onLoginWithStudentProfile = { name, email ->
                customStudentName = name.takeIf { it.isNotBlank() }
                customStudentEmail = email.takeIf { it.isNotBlank() }
                isOfflineSession = true
            }
        )
    } else {
        val effectiveName = activeUser?.displayName ?: customStudentName
        val effectiveEmail = activeUser?.email ?: customStudentEmail
        val sessionKey = activeUser?.uid ?: "student_${effectiveName ?: "default"}"
        val eduViewModel: EduViewModel = viewModel(
            key = "edu_vm_$sessionKey",
            factory = EduViewModel.provideFactory(
                context = context,
                userId = activeUser?.uid,
                displayName = effectiveName,
                email = effectiveEmail
            )
        )
        MainAppContent(
            viewModel = eduViewModel,
            userUid = activeUser?.uid,
            userEmail = effectiveEmail,
            onSignOut = {
                signOut(
                    context = context,
                    credentialManager = credentialManager,
                    onSignOutComplete = {
                        currentUser = null
                        isOfflineSession = false
                        customStudentName = null
                        customStudentEmail = null
                    },
                    scope = coroutineScope
                )
            }
        )
    }
}

@Composable
fun MainAppContent(
    viewModel: EduViewModel = viewModel(),
    userUid: String? = null,
    userEmail: String? = null,
    onSignOut: (() -> Unit)? = null
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val courses by viewModel.courses.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val mockTests by viewModel.mockTests.collectAsStateWithLifecycle()
    val doubts by viewModel.doubts.collectAsStateWithLifecycle()
    val studyMaterials by viewModel.studyMaterials.collectAsStateWithLifecycle()
    val teacherUploads by viewModel.teacherUploads.collectAsStateWithLifecycle()

    val doubtAiState by viewModel.doubtAiState.collectAsStateWithLifecycle()
    val lectureAiSummaryState by viewModel.lectureAiSummaryState.collectAsStateWithLifecycle()
    val dppAiState by viewModel.dppAiState.collectAsStateWithLifecycle()
    val studyPlannerAiState by viewModel.studyPlannerAiState.collectAsStateWithLifecycle()

    val activeBatchId by viewModel.activeBatchId.collectAsStateWithLifecycle()
    val activeLectureId by viewModel.activeLectureId.collectAsStateWithLifecycle()
    val activeDppSolvingId by viewModel.activeDppSolvingId.collectAsStateWithLifecycle()
    val activeDppResultId by viewModel.activeDppResultId.collectAsStateWithLifecycle()
    val isTeacherAdminOpen by viewModel.isTeacherAdminOpen.collectAsStateWithLifecycle()

    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val playbackSpeed by viewModel.playbackSpeed.collectAsStateWithLifecycle()
    val videoQuality by viewModel.videoQuality.collectAsStateWithLifecycle()
    val lectureProgressSec by viewModel.lectureProgressSec.collectAsStateWithLifecycle()

    val dppQuestionIndex by viewModel.dppQuestionIndex.collectAsStateWithLifecycle()
    val dppUserAnswers by viewModel.dppUserAnswers.collectAsStateWithLifecycle()
    val dppUserMultiAnswers by viewModel.dppUserMultiAnswers.collectAsStateWithLifecycle()
    val dppReviewFlags by viewModel.dppReviewFlags.collectAsStateWithLifecycle()
    val dppTimerSec by viewModel.dppTimerSec.collectAsStateWithLifecycle()

    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    // Helper for standard Compose bottom navigation pattern
    val navigateToTab: (MainNavTab) -> Unit = { tab ->
        viewModel.switchTab(tab)
        navController.navigate(tab.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    // Aggregates for Home, Lectures & Practice screens
    val allLectures = remember(courses) {
        courses.flatMap { c -> c.subjects.flatMap { s -> s.chapters.flatMap { ch -> ch.lectures } } }
    }
    val allDpps = remember(courses) {
        courses.flatMap { c -> c.subjects.flatMap { s -> s.chapters.flatMap { ch -> ch.dpps } } }
    }

    // Active full-screen drill-down objects
    val activeBatch = remember(courses, activeBatchId) {
        courses.find { it.id == activeBatchId }
    }
    val activeLecture = remember(allLectures, activeLectureId) {
        allLectures.find { it.id == activeLectureId }
    }
    val activeDppSolving = remember(allDpps, activeDppSolvingId) {
        allDpps.find { it.id == activeDppSolvingId }
    }
    val activeDppResult = remember(allDpps, activeDppResultId) {
        allDpps.find { it.id == activeDppResultId }
    }

    when {
        // Lecture Video Player & Notes Screen (with Gemini AI Summarizer & AI Doubt Solver)
        activeLecture != null -> {
            LecturePlayerScreen(
                lecture = activeLecture,
                isPlaying = isPlaying,
                progressSec = lectureProgressSec,
                playbackSpeed = playbackSpeed,
                videoQuality = videoQuality,
                lectureAiSummaryState = lectureAiSummaryState,
                doubtAiState = doubtAiState,
                onTogglePlayPause = { viewModel.togglePlayPause() },
                onSeek = { viewModel.seekTo(it) },
                onSkipBackward = { viewModel.skipBackward() },
                onSkipForward = { viewModel.skipForward() },
                onSpeedChange = { viewModel.setPlaybackSpeed(it) },
                onQualityChange = { viewModel.setVideoQuality(it) },
                onBookmarkToggle = { viewModel.toggleLectureBookmark(activeLecture.id) },
                onBack = { viewModel.closeLecture() },
                onStartDpp = { viewModel.startDpp(it) },
                onSummarizeWithAi = { viewModel.summarizeLectureWithAi(activeLecture) },
                onPostDoubt = { viewModel.postDoubt(it, activeLecture.subject) }
            )
        }

        // DPP Timed Practice Mode Screen
        activeDppSolving != null -> {
            DppSolvingScreen(
                dpp = activeDppSolving,
                currentQuestionIndex = dppQuestionIndex,
                userAnswers = dppUserAnswers,
                userMultiAnswers = dppUserMultiAnswers,
                reviewFlags = dppReviewFlags,
                timerSec = dppTimerSec,
                onSelectOption = { qIdx, optIdx -> viewModel.selectDppOption(qIdx, optIdx) },
                onToggleReviewFlag = { viewModel.toggleReviewFlag(it) },
                onQuestionIndexChange = { viewModel.setDppQuestionIndex(it) },
                onSubmit = { viewModel.submitCurrentDpp() },
                onClose = { viewModel.closeDppSolving() }
            )
        }

        // DPP Evaluation & Step Solutions Screen
        activeDppResult != null -> {
            DppResultScreen(
                dpp = activeDppResult,
                onReattempt = { viewModel.reattemptDpp(activeDppResult.id) },
                onClose = { viewModel.closeDppResult() }
            )
        }

        // Batch Chapters & Syllabus Detail Screen
        activeBatch != null -> {
            BatchDetailScreen(
                course = activeBatch,
                onBack = { viewModel.closeBatch() },
                onOpenLecture = { viewModel.openLecture(it) },
                onStartDpp = { viewModel.startDpp(it) },
                onEnrollToggle = { viewModel.enrollCourse(activeBatch.id) }
            )
        }

        // Teacher / Faculty Admin Studio Screen
        isTeacherAdminOpen -> {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) { adminPadding ->
                TeacherAdminScreen(
                    courses = courses,
                    studyMaterials = studyMaterials,
                    teacherUploads = teacherUploads,
                    defaultInstructorName = userProfile.name,
                    userUid = userUid,
                    userEmail = userEmail,
                    onUploadVideo = { title, subj, chap, cid, instr, dur, url, notes ->
                        viewModel.uploadTeacherVideo(title, subj, chap, cid, instr, dur, url, notes)
                    },
                    onUploadDpp = { title, subj, chap, cid, time, source, qs ->
                        viewModel.uploadTeacherDpp(title, subj, chap, cid, time, source, qs)
                    },
                    onUploadMaterial = { title, subj, chap, cid, cat, url, desc, author ->
                        viewModel.uploadTeacherMaterial(title, subj, chap, cid, cat, url, desc, author)
                    },
                    onDeleteUpload = { viewModel.deleteTeacherUpload(it) },
                    onOpenLecture = {
                        viewModel.closeTeacherAdmin()
                        viewModel.openLecture(it)
                    },
                    onStartDpp = {
                        viewModel.closeTeacherAdmin()
                        viewModel.startDpp(it)
                    },
                    onBack = { viewModel.closeTeacherAdmin() },
                    onUploadCourse = { title, tag, plans, instr, price, storageUrl, desc ->
                        viewModel.uploadTeacherCourse(title, tag, plans, instr, price, storageUrl, desc)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(adminPadding)
                )
            }
        }

        // Standard Compose Navigation Scaffold with Home, Lectures, Practice, and Profile
        else -> {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    EduTopBar(
                        userProfile = userProfile,
                        onProfileClick = { navigateToTab(MainNavTab.PROFILE) }
                    )
                },
                bottomBar = {
                    EduBottomBar(
                        currentDestination = currentDestination,
                        onTabSelected = { navigateToTab(it) }
                    )
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = AppRoutes.ROUTE_HOME,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    composable(AppRoutes.ROUTE_HOME) {
                        HomeScreen(
                            userProfile = userProfile,
                            courses = courses,
                            allLectures = allLectures,
                            allDpps = allDpps,
                            onNavigateTab = { navigateToTab(it) },
                            onOpenBatch = { viewModel.openBatch(it) },
                            onOpenLecture = { viewModel.openLecture(it) },
                            onStartDpp = { viewModel.startDpp(it) },
                            studyMaterials = studyMaterials,
                            onOpenTeacherAdmin = { viewModel.openTeacherAdmin() },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    composable(AppRoutes.ROUTE_LECTURES) {
                        LecturesScreen(
                            lectures = allLectures,
                            onOpenLecture = { viewModel.openLecture(it) },
                            onBookmarkToggle = { viewModel.toggleLectureBookmark(it) },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    composable(AppRoutes.ROUTE_PRACTICE) {
                        DppHubScreen(
                            dpps = allDpps,
                            userProfile = userProfile,
                            dppAiState = dppAiState,
                            onStartDpp = { viewModel.startDpp(it) },
                            onGenerateAiDpp = { subj, topic, diff, openNow ->
                                viewModel.generateCustomAiDpp(subj, topic, diff, openNow)
                            },
                            onDismissAiState = { viewModel.resetDppAiState() },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    composable(AppRoutes.ROUTE_PROFILE) {
                        ProfileScreen(
                            userProfile = userProfile,
                            mockTests = mockTests,
                            doubts = doubts,
                            doubtAiState = doubtAiState,
                            studyPlannerAiState = studyPlannerAiState,
                            onPostDoubt = { text, subj -> viewModel.postDoubt(text, subj) },
                            onGenerateStudyPlan = { hours, subj, weakTopics ->
                                viewModel.generateAiStudyPlan(hours, subj, weakTopics)
                            },
                            onUpdateProfile = { name, targetExam, classLevel, studentGoal, bio ->
                                viewModel.updateUserProfile(name, targetExam, classLevel, studentGoal, bio)
                            },
                            onOpenTeacherAdmin = { viewModel.openTeacherAdmin() },
                            userUid = userUid,
                            userEmail = userEmail,
                            onSignOut = onSignOut,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    composable(AppRoutes.ROUTE_BATCHES) {
                        CoursesScreen(
                            courses = courses,
                            onOpenBatch = { viewModel.openBatch(it) },
                            onEnrollToggle = { viewModel.enrollCourse(it) },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    composable(AppRoutes.ROUTE_TEACHER_ADMIN) {
                        TeacherAdminScreen(
                            courses = courses,
                            studyMaterials = studyMaterials,
                            teacherUploads = teacherUploads,
                            defaultInstructorName = userProfile.name,
                            userUid = userUid,
                            userEmail = userEmail,
                            onUploadVideo = { title, subj, chap, cid, instr, dur, url, notes ->
                                viewModel.uploadTeacherVideo(title, subj, chap, cid, instr, dur, url, notes)
                            },
                            onUploadDpp = { title, subj, chap, cid, time, source, qs ->
                                viewModel.uploadTeacherDpp(title, subj, chap, cid, time, source, qs)
                            },
                            onUploadMaterial = { title, subj, chap, cid, cat, url, desc, author ->
                                viewModel.uploadTeacherMaterial(title, subj, chap, cid, cat, url, desc, author)
                            },
                            onDeleteUpload = { viewModel.deleteTeacherUpload(it) },
                            onOpenLecture = {
                                viewModel.closeTeacherAdmin()
                                viewModel.openLecture(it)
                            },
                            onStartDpp = {
                                viewModel.closeTeacherAdmin()
                                viewModel.startDpp(it)
                            },
                            onBack = { navigateToTab(MainNavTab.HOME) },
                            onUploadCourse = { title, tag, plans, instr, price, storageUrl, desc ->
                                viewModel.uploadTeacherCourse(title, tag, plans, instr, price, storageUrl, desc)
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

