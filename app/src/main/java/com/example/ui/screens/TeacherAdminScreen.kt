package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.*
import com.example.ui.theme.*

const val TEACHER_ADMIN_PASSWORD = "ASDFTREW"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherAdminScreen(
    courses: List<CourseBatch>,
    studyMaterials: List<StudyMaterial>,
    teacherUploads: List<TeacherUploadItem>,
    defaultInstructorName: String,
    onUploadVideo: (
        title: String,
        subject: SubjectType,
        chapterTitle: String,
        courseId: String,
        instructorName: String,
        durationMinutes: Int,
        videoUrl: String,
        notesSummaryText: String
    ) -> Unit,
    onUploadDpp: (
        title: String,
        subject: SubjectType,
        chapterTitle: String,
        courseId: String,
        timeLimitMinutes: Int,
        sourceBookTag: String,
        questions: List<Question>
    ) -> Unit,
    onUploadMaterial: (
        title: String,
        subject: SubjectType,
        chapterTitle: String,
        courseId: String,
        category: String,
        resourceUrl: String,
        description: String,
        authorName: String
    ) -> Unit,
    onDeleteUpload: (String) -> Unit,
    onOpenLecture: (String) -> Unit,
    onStartDpp: (String) -> Unit,
    onBack: () -> Unit,
    onUploadCourse: (
        title: String,
        categoryTag: String,
        plansCsv: String,
        instructorName: String,
        priceRupees: Int,
        storageBucketOrBannerUrl: String,
        description: String
    ) -> Unit = { _, _, _, _, _, _, _ -> },
    userUid: String? = null,
    userEmail: String? = null,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    var adminUidCopied by remember { mutableStateOf(false) }
    var isAdminUnlocked by remember { mutableStateOf(false) }
    var passwordInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var selectedSection by remember { mutableIntStateOf(0) } // 0: Video, 1: DPP, 2: Material, 3: Manage, 4: Storage & Course

    // --- Tab 0: Video Upload Form State ---
    var videoTitle by remember { mutableStateOf("") }
    var videoSubject by remember { mutableStateOf(SubjectType.PHYSICS) }
    var videoCourseId by remember { mutableStateOf(courses.firstOrNull()?.id ?: "c_arjuna_jee") }
    var videoChapter by remember { mutableStateOf("Chapter 1: Introduction to Physics") }
    var videoInstructor by remember(defaultInstructorName) { mutableStateOf(defaultInstructorName.ifBlank { "Prof. H.C. Verma" }) }
    var videoDuration by remember { mutableStateOf("55") }
    var videoUrl by remember { mutableStateOf("") }
    var videoNotes by remember {
        mutableStateOf(
            "1. Fundamental concepts and dimensional analysis.\n2. Step-by-step derivation of governing equations.\n3. High-yield JEE & NEET solved examples."
        )
    }

    // Zero-permission Android Photo Picker for selecting local video lecture files
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            videoUrl = uri.toString()
        }
    }

    // --- Tab 1: DPP Builder & Upload State ---
    var dppTitle by remember { mutableStateOf("") }
    var dppSubject by remember { mutableStateOf(SubjectType.PHYSICS) }
    var dppCourseId by remember { mutableStateOf(courses.firstOrNull()?.id ?: "c_arjuna_jee") }
    var dppChapter by remember { mutableStateOf("Chapter 1: Introduction to Physics") }
    var dppTimeLimit by remember { mutableStateOf("15") }
    var dppSourceBook by remember { mutableStateOf("Faculty Custom DPP (Objective I & II)") }

    var draftQuestionText by remember { mutableStateOf("") }
    var draftOptA by remember { mutableStateOf("") }
    var draftOptB by remember { mutableStateOf("") }
    var draftOptC by remember { mutableStateOf("") }
    var draftOptD by remember { mutableStateOf("") }
    var draftIsMultiSelect by remember { mutableStateOf(false) }
    var draftCorrectIndices by remember { mutableStateOf(setOf(0)) }
    var draftExplanation by remember { mutableStateOf("") }
    var draftHint by remember { mutableStateOf("") }
    var draftTopic by remember { mutableStateOf("Core Concept") }
    var draftQuestions by remember { mutableStateOf<List<Question>>(emptyList()) }

    // --- Tab 2: Other Study Material Upload State ---
    var matTitle by remember { mutableStateOf("") }
    var matSubject by remember { mutableStateOf(SubjectType.PHYSICS) }
    var matCourseId by remember { mutableStateOf(courses.firstOrNull()?.id ?: "c_arjuna_jee") }
    var matChapter by remember { mutableStateOf("Chapter 1: Introduction to Physics") }
    var matCategory by remember { mutableStateOf("PDF Class Notes") }
    var matAuthor by remember(defaultInstructorName) { mutableStateOf(defaultInstructorName.ifBlank { "ARCSTFUG Faculty" }) }
    var matUrl by remember { mutableStateOf("") }
    var matDescription by remember { mutableStateOf("") }

    // Zero-permission System Document Picker for selecting PDF / Notes / Doc files
    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            matUrl = uri.toString()
        }
    }

    val materialCategories = remember {
        listOf(
            "PDF Class Notes",
            "Formula Sheet",
            "Google Drive Folder",
            "PYQ Booklet",
            "Handwritten Notes",
            "Test Series Link"
        )
    }

    // --- Tab 4: Firebase Storage & Course Batch Publisher State ---
    var courseTitle by remember { mutableStateOf("HACKING COURSE 2027") }
    var courseCategoryTag by remember { mutableStateOf("HACKER") }
    var coursePlans by remember { mutableStateOf("Infinity, Pro") }
    var courseInstructor by remember {
        mutableStateOf("Lead Hacker Mentor (Sunglasses & Blue-Green Emblem Faculty)")
    }
    var coursePrice by remember { mutableStateOf("499") }
    var courseStorageUrl by remember {
        mutableStateOf("${ExternalStudyLinks.FIREBASE_STORAGE_BUCKET}/courses/hacking_course_2027")
    }
    var courseDescription by remember {
        mutableStateOf("Multiple plans inside: Infinity, Pro 👑 • Official HACKING COURSE 2027 featuring designated HACKER badge, hands-on Cyber Defense, Ethical Hacking labs & Firebase Cloud Storage assets.")
    }

    val bannerPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            courseStorageUrl = uri.toString()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Teacher & Faculty Admin Studio",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                        )
                        Text(
                            text = if (isAdminUnlocked) {
                                "Unlocked • Upload Video Lectures, DPPs & Study Materials"
                            } else {
                                "Protected • Faculty Password Required"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isAdminUnlocked) EmeraldSuccess else MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("teacher_admin_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (isAdminUnlocked) {
                        OutlinedButton(
                            onClick = {
                                isAdminUnlocked = false
                                passwordInput = ""
                                passwordError = null
                            },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .testTag("teacher_admin_lock_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = "Lock Admin Panel",
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Lock", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            )
        },
        modifier = modifier
            .fillMaxSize()
            .testTag("teacher_admin_screen")
    ) { innerPadding ->
        if (!isAdminUnlocked) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 460.dp)
                        .testTag("teacher_admin_password_card")
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(IndigoPrimary.copy(alpha = 0.14f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = "Admin Password Protected",
                                tint = IndigoPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Teacher Admin Security Gate",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Enter the ARCSTFUG Faculty Password to upload or manage Video Lectures, Objective I/II DPPs, and Study Materials.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = {
                                passwordInput = it
                                if (passwordError != null) passwordError = null
                            },
                            label = { Text("Faculty Admin Password") },
                            placeholder = { Text("Enter password") },
                            singleLine = true,
                            isError = passwordError != null,
                            visualTransformation = if (passwordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Key,
                                    contentDescription = null,
                                    tint = IndigoPrimary
                                )
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = { passwordVisible = !passwordVisible },
                                    modifier = Modifier.testTag("teacher_admin_password_toggle")
                                ) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                        contentDescription = if (passwordVisible) "Hide Password" else "Show Password"
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("teacher_admin_password_input")
                        )

                        if (passwordError != null) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = RoseContainer,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("teacher_admin_password_error")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ErrorOutline,
                                        contentDescription = null,
                                        tint = RoseError,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = passwordError!!,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = RoseError
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = onBack,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("teacher_admin_cancel_button")
                            ) {
                                Text("Back", fontWeight = FontWeight.SemiBold)
                            }

                            Button(
                                onClick = {
                                    if (passwordInput.trim() == TEACHER_ADMIN_PASSWORD) {
                                        isAdminUnlocked = true
                                        passwordError = null
                                    } else {
                                        passwordError = "Invalid password! Please enter the valid Faculty Admin password."
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1.4f)
                                    .testTag("teacher_admin_unlock_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.LockOpen,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Unlock Panel", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp)
        ) {
            // Admin Banner & Stats Summary
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        IndigoPrimary.copy(alpha = 0.16f),
                                        CyanAccent.copy(alpha = 0.14f)
                                    )
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(IndigoPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.CloudUpload,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "ARCSTFUG Faculty Content Publisher",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Instantly publish Video Lectures, Objective I/II DPPs, and PDFs/Drive Notes to batches & Cloud Firestore.",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Sub-tab selector
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                AdminTabButton(
                                    label = "🎬 Video",
                                    selected = selectedSection == 0,
                                    testTag = "upload_video_tab",
                                    onClick = { selectedSection = 0 },
                                    modifier = Modifier.weight(1f)
                                )
                                AdminTabButton(
                                    label = "📝 DPP",
                                    selected = selectedSection == 1,
                                    testTag = "upload_dpp_tab",
                                    onClick = { selectedSection = 1 },
                                    modifier = Modifier.weight(1f)
                                )
                                AdminTabButton(
                                    label = "📚 Material",
                                    selected = selectedSection == 2,
                                    testTag = "upload_material_tab",
                                    onClick = { selectedSection = 2 },
                                    modifier = Modifier.weight(1f)
                                )
                                AdminTabButton(
                                    label = "☁️ Course",
                                    selected = selectedSection == 4,
                                    testTag = "upload_course_storage_tab",
                                    onClick = { selectedSection = 4 },
                                    modifier = Modifier.weight(1f)
                                )
                                AdminTabButton(
                                    label = "🗂️ (${teacherUploads.size})",
                                    selected = selectedSection == 3,
                                    testTag = "manage_uploads_tab",
                                    onClick = { selectedSection = 3 },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            when (selectedSection) {
                // =============================================================
                // TAB 0: UPLOAD VIDEO LECTURE
                // =============================================================
                0 -> {
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = CardDefaults.outlinedCardBorder(),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.VideoCall,
                                        contentDescription = null,
                                        tint = IndigoPrimary
                                    )
                                    Text(
                                        text = "Upload New Video Lecture",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }

                                OutlinedTextField(
                                    value = videoTitle,
                                    onValueChange = { videoTitle = it },
                                    label = { Text("Lecture Title *") },
                                    placeholder = { Text("e.g. Lec 04: Circular Motion & Banking of Roads") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("teacher_video_title_input")
                                )

                                Text(
                                    text = "Select Subject:",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    SubjectType.entries.forEach { subj ->
                                        FilterChip(
                                            selected = videoSubject == subj,
                                            onClick = { videoSubject = subj },
                                            label = { Text(subj.displayName, fontSize = 11.sp) },
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("video_subject_chip_${subj.name.lowercase()}")
                                        )
                                    }
                                }

                                Text(
                                    text = "Target Batch:",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(courses) { course ->
                                        FilterChip(
                                            selected = videoCourseId == course.id,
                                            onClick = { videoCourseId = course.id },
                                            label = { Text(course.title, fontSize = 11.sp) }
                                        )
                                    }
                                }

                                OutlinedTextField(
                                    value = videoChapter,
                                    onValueChange = { videoChapter = it },
                                    label = { Text("Chapter Name *") },
                                    placeholder = { Text("e.g. Chapter 7: Circular Motion") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("teacher_video_chapter_input")
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedTextField(
                                        value = videoInstructor,
                                        onValueChange = { videoInstructor = it },
                                        label = { Text("Faculty / Teacher Name") },
                                        singleLine = true,
                                        modifier = Modifier
                                            .weight(1.4f)
                                            .testTag("teacher_video_instructor_input")
                                    )
                                    OutlinedTextField(
                                        value = videoDuration,
                                        onValueChange = { videoDuration = it.filter { ch -> ch.isDigit() } },
                                        label = { Text("Duration (min)") },
                                        singleLine = true,
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("teacher_video_duration_input")
                                    )
                                }

                                OutlinedTextField(
                                    value = videoUrl,
                                    onValueChange = { videoUrl = it },
                                    label = { Text("Video URL (YouTube / Google Drive / Stream Link)") },
                                    placeholder = { Text("https://drive.google.com/... or select local video below") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("teacher_video_url_input")
                                )

                                OutlinedButton(
                                    onClick = {
                                        videoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                        )
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 48.dp)
                                        .testTag("pick_local_video_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.VideoLibrary,
                                        contentDescription = "Select Video from Device",
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Select Video File from Device (Photo Picker)")
                                }

                                OutlinedTextField(
                                    value = videoNotes,
                                    onValueChange = { videoNotes = it },
                                    label = { Text("Lecture Key Notes & Formula Summary (1 point per line)") },
                                    minLines = 3,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("teacher_video_notes_input")
                                )

                                Button(
                                    onClick = {
                                        val durationInt = videoDuration.toIntOrNull() ?: 50
                                        onUploadVideo(
                                            videoTitle.ifBlank { "Lec: $videoChapter" },
                                            videoSubject,
                                            videoChapter,
                                            videoCourseId,
                                            videoInstructor,
                                            durationInt,
                                            videoUrl,
                                            videoNotes
                                        )
                                        videoTitle = ""
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 52.dp)
                                        .testTag("publish_video_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.CloudUpload,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Publish Video Lecture",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }
                }

                // =============================================================
                // TAB 1: CREATE & UPLOAD DPP (OBJECTIVE I & OBJECTIVE II)
                // =============================================================
                1 -> {
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = CardDefaults.outlinedCardBorder(),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Assignment,
                                        contentDescription = null,
                                        tint = AmberStreak
                                    )
                                    Column {
                                        Text(
                                            text = "Create & Publish Chapter DPP Sheet",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "Supports both Objective I (Single Choice) & Objective II (Multi-Select)",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                OutlinedTextField(
                                    value = dppTitle,
                                    onValueChange = { dppTitle = it },
                                    label = { Text("DPP Title *") },
                                    placeholder = { Text("e.g. DPP: Rotational Mechanics (H.C. Verma & JEE Adv)") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("teacher_dpp_title_input")
                                )

                                Text(
                                    text = "Subject:",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    SubjectType.entries.forEach { subj ->
                                        FilterChip(
                                            selected = dppSubject == subj,
                                            onClick = { dppSubject = subj },
                                            label = { Text(subj.displayName, fontSize = 11.sp) },
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("dpp_subject_chip_${subj.name.lowercase()}")
                                        )
                                    }
                                }

                                OutlinedTextField(
                                    value = dppChapter,
                                    onValueChange = { dppChapter = it },
                                    label = { Text("Chapter Title *") },
                                    placeholder = { Text("e.g. Chapter 10: Rotational Mechanics") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("teacher_dpp_chapter_input")
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedTextField(
                                        value = dppTimeLimit,
                                        onValueChange = { dppTimeLimit = it.filter { ch -> ch.isDigit() } },
                                        label = { Text("Time Limit (min)") },
                                        singleLine = true,
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("teacher_dpp_time_input")
                                    )
                                    OutlinedTextField(
                                        value = dppSourceBook,
                                        onValueChange = { dppSourceBook = it },
                                        label = { Text("Source / Book Tag") },
                                        singleLine = true,
                                        modifier = Modifier
                                            .weight(1.6f)
                                            .testTag("teacher_dpp_source_input")
                                    )
                                }

                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                                // MCQ Question Builder Box
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Add Question #${draftQuestions.size + 1}",
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                color = IndigoPrimary
                                            )
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Text(
                                                    text = if (draftIsMultiSelect) "Objective II (Multi)" else "Objective I (Single)",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = if (draftIsMultiSelect) AmberStreak else IndigoPrimary
                                                )
                                                Switch(
                                                    checked = draftIsMultiSelect,
                                                    onCheckedChange = { isMulti ->
                                                        draftIsMultiSelect = isMulti
                                                        if (!isMulti && draftCorrectIndices.size > 1) {
                                                            draftCorrectIndices = setOf(draftCorrectIndices.minOrNull() ?: 0)
                                                        }
                                                    },
                                                    modifier = Modifier.testTag("dpp_question_multiselect_switch")
                                                )
                                            }
                                        }

                                        OutlinedTextField(
                                            value = draftQuestionText,
                                            onValueChange = { draftQuestionText = it },
                                            label = { Text("Question Statement *") },
                                            placeholder = { Text("Enter the MCQ question statement...") },
                                            minLines = 2,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("dpp_builder_question_input")
                                        )

                                        Text(
                                            text = "Options (Tap circle/checkbox to mark correct answer${if (draftIsMultiSelect) "s" else ""}):",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                        )

                                        val optionValues = listOf(draftOptA, draftOptB, draftOptC, draftOptD)
                                        val optionSetters: List<(String) -> Unit> = listOf(
                                            { draftOptA = it },
                                            { draftOptB = it },
                                            { draftOptC = it },
                                            { draftOptD = it }
                                        )
                                        val optionLabels = listOf("A", "B", "C", "D")

                                        optionLabels.forEachIndexed { idx, label ->
                                            val isSelected = draftCorrectIndices.contains(idx)
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                IconButton(
                                                    onClick = {
                                                        if (draftIsMultiSelect) {
                                                            val next = draftCorrectIndices.toMutableSet()
                                                            if (next.contains(idx) && next.size > 1) {
                                                                next.remove(idx)
                                                            } else {
                                                                next.add(idx)
                                                            }
                                                            draftCorrectIndices = next
                                                        } else {
                                                            draftCorrectIndices = setOf(idx)
                                                        }
                                                    },
                                                    modifier = Modifier.testTag("dpp_correct_toggle_$idx")
                                                ) {
                                                    Icon(
                                                        imageVector = if (draftIsMultiSelect) {
                                                            if (isSelected) Icons.Filled.CheckBox else Icons.Filled.CheckBoxOutlineBlank
                                                        } else {
                                                            if (isSelected) Icons.Filled.RadioButtonChecked else Icons.Filled.RadioButtonUnchecked
                                                        },
                                                        contentDescription = "Mark Option $label Correct",
                                                        tint = if (isSelected) EmeraldSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                OutlinedTextField(
                                                    value = optionValues[idx],
                                                    onValueChange = optionSetters[idx],
                                                    label = { Text("Option ($label)") },
                                                    singleLine = true,
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .testTag("dpp_builder_opt_$idx")
                                                )
                                            }
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            OutlinedTextField(
                                                value = draftHint,
                                                onValueChange = { draftHint = it },
                                                label = { Text("Formula Hint") },
                                                singleLine = true,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .testTag("dpp_builder_hint_input")
                                            )
                                            OutlinedTextField(
                                                value = draftTopic,
                                                onValueChange = { draftTopic = it },
                                                label = { Text("Sub-topic Tag") },
                                                singleLine = true,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .testTag("dpp_builder_topic_input")
                                            )
                                        }

                                        OutlinedTextField(
                                            value = draftExplanation,
                                            onValueChange = { draftExplanation = it },
                                            label = { Text("Step-by-Step Solution / Derivation") },
                                            minLines = 2,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("dpp_builder_explanation_input")
                                        )

                                        FilledTonalButton(
                                            onClick = {
                                                if (draftQuestionText.isNotBlank()) {
                                                    val opts = listOf(
                                                        draftOptA.ifBlank { "Option (a)" },
                                                        draftOptB.ifBlank { "Option (b)" },
                                                        draftOptC.ifBlank { "Option (c)" },
                                                        draftOptD.ifBlank { "Option (d)" }
                                                    )
                                                    val safeIndices = draftCorrectIndices.ifEmpty { setOf(0) }
                                                    val q = Question(
                                                        id = "tq_${System.currentTimeMillis()}_${draftQuestions.size}",
                                                        questionText = draftQuestionText.trim(),
                                                        options = opts,
                                                        correctOptionIndex = safeIndices.minOrNull() ?: 0,
                                                        explanation = draftExplanation.ifBlank { "Apply the chapter formula and solve step-by-step." },
                                                        hint = draftHint.ifBlank { "Use the fundamental definition from $dppChapter." },
                                                        topic = if (draftIsMultiSelect) "Objective II • $draftTopic" else "Objective I • $draftTopic",
                                                        correctOptionIndices = safeIndices,
                                                        isMultiSelect = draftIsMultiSelect || safeIndices.size > 1
                                                    )
                                                    draftQuestions = draftQuestions + q
                                                    draftQuestionText = ""
                                                    draftOptA = ""
                                                    draftOptB = ""
                                                    draftOptC = ""
                                                    draftOptD = ""
                                                    draftExplanation = ""
                                                    draftHint = ""
                                                    draftCorrectIndices = setOf(0)
                                                }
                                            },
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .heightIn(min = 48.dp)
                                                .testTag("add_question_to_dpp_button")
                                        ) {
                                            Icon(Icons.Filled.AddCircle, contentDescription = null)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Add Question to DPP Sheet",
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                // Added Questions Preview List
                                if (draftQuestions.isNotEmpty()) {
                                    Text(
                                        text = "Questions in Draft (${draftQuestions.size}):",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                        color = EmeraldSuccess
                                    )
                                    draftQuestions.forEachIndexed { qIdx, q ->
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = "Q${qIdx + 1}: ${q.questionText}",
                                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                                        maxLines = 2
                                                    )
                                                    val ansLabels = q.correctOptionIndices.sorted().map { 'A' + it }.joinToString(", ")
                                                    Text(
                                                        text = "${if (q.isMultiSelect) "Objective II (Multi)" else "Objective I"} • Correct: ($ansLabels)",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = IndigoPrimary
                                                    )
                                                }
                                                IconButton(
                                                    onClick = {
                                                        draftQuestions = draftQuestions.filterIndexed { i, _ -> i != qIdx }
                                                    }
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.DeleteOutline,
                                                        contentDescription = "Remove Question",
                                                        tint = RoseError
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Button(
                                    onClick = {
                                        val finalQuestions = if (draftQuestions.isNotEmpty()) {
                                            draftQuestions
                                        } else if (draftQuestionText.isNotBlank()) {
                                            val opts = listOf(
                                                draftOptA.ifBlank { "Option (a)" },
                                                draftOptB.ifBlank { "Option (b)" },
                                                draftOptC.ifBlank { "Option (c)" },
                                                draftOptD.ifBlank { "Option (d)" }
                                            )
                                            val safeIndices = draftCorrectIndices.ifEmpty { setOf(0) }
                                            listOf(
                                                Question(
                                                    id = "tq_${System.currentTimeMillis()}_0",
                                                    questionText = draftQuestionText.trim(),
                                                    options = opts,
                                                    correctOptionIndex = safeIndices.minOrNull() ?: 0,
                                                    explanation = draftExplanation.ifBlank { "Apply the chapter formula and solve step-by-step." },
                                                    hint = draftHint.ifBlank { "Use the fundamental definition from $dppChapter." },
                                                    topic = if (draftIsMultiSelect) "Objective II • $draftTopic" else "Objective I • $draftTopic",
                                                    correctOptionIndices = safeIndices,
                                                    isMultiSelect = draftIsMultiSelect || safeIndices.size > 1
                                                )
                                            )
                                        } else {
                                            emptyList()
                                        }

                                        onUploadDpp(
                                            dppTitle.ifBlank { "DPP: $dppChapter" },
                                            dppSubject,
                                            dppChapter,
                                            dppCourseId,
                                            dppTimeLimit.toIntOrNull() ?: 15,
                                            dppSourceBook,
                                            finalQuestions
                                        )
                                        if (finalQuestions.isNotEmpty()) {
                                            dppTitle = ""
                                            draftQuestions = emptyList()
                                            draftQuestionText = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 52.dp)
                                        .testTag("publish_dpp_button")
                                ) {
                                    Icon(Icons.Filled.CloudUpload, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Publish DPP Sheet (${if (draftQuestions.isNotEmpty()) draftQuestions.size else if (draftQuestionText.isNotBlank()) 1 else 0} Qs)",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }
                }

                // =============================================================
                // TAB 2: UPLOAD OTHER STUDY MATERIAL (PDF / FORMULA SHEET / DRIVE)
                // =============================================================
                2 -> {
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = CardDefaults.outlinedCardBorder(),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.PictureAsPdf,
                                        contentDescription = null,
                                        tint = EmeraldSuccess
                                    )
                                    Column {
                                        Text(
                                            text = "Upload Study Material, PDF Notes & Links",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "Share Chapter PDFs, Formula Sheets, PYQ Booklets or Google Drive Folders",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                OutlinedTextField(
                                    value = matTitle,
                                    onValueChange = { matTitle = it },
                                    label = { Text("Material Title *") },
                                    placeholder = { Text("e.g. Rotational Mechanics Complete Formula & Short Notes PDF") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("teacher_material_title_input")
                                )

                                Text(
                                    text = "Material Type / Category:",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(materialCategories) { cat ->
                                        FilterChip(
                                            selected = matCategory == cat,
                                            onClick = { matCategory = cat },
                                            label = { Text(cat, fontSize = 11.sp) }
                                        )
                                    }
                                }

                                Text(
                                    text = "Subject:",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    SubjectType.entries.forEach { subj ->
                                        FilterChip(
                                            selected = matSubject == subj,
                                            onClick = { matSubject = subj },
                                            label = { Text(subj.displayName, fontSize = 11.sp) },
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("material_subject_chip_${subj.name.lowercase()}")
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedTextField(
                                        value = matChapter,
                                        onValueChange = { matChapter = it },
                                        label = { Text("Chapter Title *") },
                                        singleLine = true,
                                        modifier = Modifier
                                            .weight(1.3f)
                                            .testTag("teacher_material_chapter_input")
                                    )
                                    OutlinedTextField(
                                        value = matAuthor,
                                        onValueChange = { matAuthor = it },
                                        label = { Text("Faculty Name") },
                                        singleLine = true,
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("teacher_material_author_input")
                                    )
                                }

                                OutlinedTextField(
                                    value = matUrl,
                                    onValueChange = { matUrl = it },
                                    label = { Text("Material Link (Google Drive / PDF URL / Local URI)") },
                                    placeholder = { Text("https://drive.google.com/... or pick PDF document below") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("teacher_material_url_input")
                                )

                                OutlinedButton(
                                    onClick = {
                                        documentPickerLauncher.launch(
                                            arrayOf("application/pdf", "image/*", "text/*")
                                        )
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 48.dp)
                                        .testTag("pick_local_document_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.AttachFile,
                                        contentDescription = "Select PDF or Document",
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Attach PDF / Notes File from Device (Document Picker)")
                                }

                                OutlinedTextField(
                                    value = matDescription,
                                    onValueChange = { matDescription = it },
                                    label = { Text("Description & Key Contents") },
                                    placeholder = { Text("Describe what students will learn from this PDF, formula sheet, or booklet...") },
                                    minLines = 3,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("teacher_material_desc_input")
                                )

                                Button(
                                    onClick = {
                                        onUploadMaterial(
                                            matTitle.ifBlank { "$matCategory: $matChapter" },
                                            matSubject,
                                            matChapter,
                                            matCourseId,
                                            matCategory,
                                            matUrl.ifBlank { ExternalStudyLinks.PHYSICS_DRIVE_URL },
                                            matDescription.ifBlank { "$matCategory for $matChapter uploaded by $matAuthor." },
                                            matAuthor
                                        )
                                        matTitle = ""
                                        matDescription = ""
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 52.dp)
                                        .testTag("publish_material_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.CloudUpload,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Publish Study Material",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                // =============================================================
                // TAB 3: MANAGE UPLOADED CONTENT & STUDY VAULT
                // =============================================================
                3 -> {
                    item {
                        Text(
                            text = "Teacher Uploaded Items (${teacherUploads.size})",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (teacherUploads.isEmpty()) {
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = CardDefaults.outlinedCardBorder(),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.CloudQueue,
                                        contentDescription = null,
                                        tint = IndigoPrimary,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Text(
                                        text = "No Custom Teacher Uploads Yet",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Use the Video, DPP, or Material tabs above to publish new content for students.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        items(teacherUploads, key = { it.id }) { item ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = CardDefaults.outlinedCardBorder(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("teacher_upload_card_${item.id}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = when (item.uploadType) {
                                                    "VIDEO" -> IndigoPrimary.copy(alpha = 0.15f)
                                                    "DPP" -> AmberContainer
                                                    else -> EmeraldSuccess.copy(alpha = 0.15f)
                                                }
                                            ) {
                                                Text(
                                                    text = "${item.uploadType} • ${item.materialCategory}",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = when (item.uploadType) {
                                                        "VIDEO" -> IndigoPrimary
                                                        "DPP" -> Color(0xFF78350F)
                                                        else -> EmeraldSuccess
                                                    },
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                )
                                            }
                                            Text(
                                                text = item.subject.displayName,
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        IconButton(
                                            onClick = { onDeleteUpload(item.id) },
                                            modifier = Modifier.testTag("delete_upload_${item.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.DeleteOutline,
                                                contentDescription = "Delete Upload",
                                                tint = RoseError
                                            )
                                        }
                                    }

                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Chapter: ${item.chapterTitle} • By ${item.authorName}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = item.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 3
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        when (item.uploadType) {
                                            "VIDEO" -> {
                                                FilledTonalButton(
                                                    onClick = { onOpenLecture(item.id) },
                                                    shape = RoundedCornerShape(10.dp)
                                                ) {
                                                    Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("Open in Player", fontSize = 12.sp)
                                                }
                                            }
                                            "DPP" -> {
                                                FilledTonalButton(
                                                    onClick = { onStartDpp(item.id) },
                                                    shape = RoundedCornerShape(10.dp)
                                                ) {
                                                    Icon(Icons.Filled.PlayCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("Attempt DPP", fontSize = 12.sp)
                                                }
                                            }
                                            else -> {
                                                if (item.resourceUrl.startsWith("http")) {
                                                    FilledTonalButton(
                                                        onClick = { uriHandler.openUri(item.resourceUrl) },
                                                        shape = RoundedCornerShape(10.dp)
                                                    ) {
                                                        Icon(Icons.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text("Open Material", fontSize = 12.sp)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // All Study Materials Vault Preview
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "All Published Study Materials (${studyMaterials.size})",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    items(studyMaterials, key = { "vault_${it.id}" }) { mat ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = CardDefaults.outlinedCardBorder(),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(EmeraldSuccess.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.MenuBook,
                                        contentDescription = null,
                                        tint = EmeraldSuccess,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = mat.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${mat.category} • ${mat.subject.displayName} • ${mat.authorName}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (mat.resourceUrl.startsWith("http")) {
                                    IconButton(onClick = { uriHandler.openUri(mat.resourceUrl) }) {
                                        Icon(
                                            imageVector = Icons.Filled.OpenInNew,
                                            contentDescription = "Open Link",
                                            tint = IndigoPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // =============================================================
                // TAB 4: FIREBASE CLOUD STORAGE & COURSE BATCH PUBLISHER
                // =============================================================
                else -> {
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = CardDefaults.outlinedCardBorder(),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.CloudQueue,
                                            contentDescription = null,
                                            tint = IndigoPrimary
                                        )
                                        Column {
                                            Text(
                                                text = "Firebase Cloud Storage & Course Studio",
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Text(
                                                text = "Bucket: ${ExternalStudyLinks.FIREBASE_STORAGE_BUCKET}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    FilledTonalButton(
                                        onClick = { uriHandler.openUri(ExternalStudyLinks.FIREBASE_STORAGE_CONSOLE_URL) },
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                        modifier = Modifier.testTag("open_firebase_storage_console_btn")
                                    ) {
                                        Icon(Icons.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Storage Console", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                // Featured HACKING COURSE 2027 Banner Preview
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                                    border = CardDefaults.outlinedCardBorder(),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column {
                                        Image(
                                            painter = painterResource(id = R.drawable.hacking_course_2027_banner),
                                            contentDescription = "HACKING COURSE 2027 Poster",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(175.dp)
                                        )
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "👑 Multiple plans inside: Infinity, Pro",
                                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                                                    color = Color(0xFF78350F)
                                                )
                                                Surface(
                                                    shape = RoundedCornerShape(12.dp),
                                                    color = Color(0xFFFDE68A),
                                                    modifier = Modifier.border(1.dp, Color(0xFF78350F), RoundedCornerShape(12.dp))
                                                ) {
                                                    Text(
                                                        text = "HACKER",
                                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                                        color = Color(0xFF1E1B4B),
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = "HACKING COURSE 2027 (Active in Batches & Home)",
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                color = Color(0xFF1E1B4B)
                                            )
                                        }
                                    }
                                }

                                OutlinedTextField(
                                    value = courseTitle,
                                    onValueChange = { courseTitle = it },
                                    label = { Text("Course Name *") },
                                    placeholder = { Text("HACKING COURSE 2027") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("teacher_course_title_input")
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedTextField(
                                        value = courseCategoryTag,
                                        onValueChange = { courseCategoryTag = it },
                                        label = { Text("Feature Badge / Tag") },
                                        placeholder = { Text("HACKER") },
                                        singleLine = true,
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("teacher_course_tag_input")
                                    )
                                    OutlinedTextField(
                                        value = coursePlans,
                                        onValueChange = { coursePlans = it },
                                        label = { Text("Plans (👑)") },
                                        placeholder = { Text("Infinity, Pro") },
                                        singleLine = true,
                                        modifier = Modifier
                                            .weight(1.3f)
                                            .testTag("teacher_course_plans_input")
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedTextField(
                                        value = courseInstructor,
                                        onValueChange = { courseInstructor = it },
                                        label = { Text("Instructor Name") },
                                        singleLine = true,
                                        modifier = Modifier
                                            .weight(1.6f)
                                            .testTag("teacher_course_instructor_input")
                                    )
                                    OutlinedTextField(
                                        value = coursePrice,
                                        onValueChange = { coursePrice = it.filter { ch -> ch.isDigit() } },
                                        label = { Text("Price (₹)") },
                                        singleLine = true,
                                        modifier = Modifier
                                            .weight(0.8f)
                                            .testTag("teacher_course_price_input")
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = courseStorageUrl,
                                        onValueChange = { courseStorageUrl = it },
                                        label = { Text("Firebase Storage Bucket Path / Banner URL") },
                                        singleLine = true,
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("teacher_course_storage_url_input")
                                    )
                                    FilledTonalButton(
                                        onClick = {
                                            bannerPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                                            )
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.testTag("pick_storage_banner_btn")
                                    ) {
                                        Icon(Icons.Filled.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Pick File", fontSize = 12.sp)
                                    }
                                }

                                OutlinedTextField(
                                    value = courseDescription,
                                    onValueChange = { courseDescription = it },
                                    label = { Text("Course Description & Plan Highlights") },
                                    minLines = 3,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("teacher_course_desc_input")
                                )

                                Button(
                                    onClick = {
                                        val parsedPrice = coursePrice.toIntOrNull() ?: 499
                                        onUploadCourse(
                                            courseTitle,
                                            courseCategoryTag,
                                            coursePlans,
                                            courseInstructor,
                                            parsedPrice,
                                            courseStorageUrl,
                                            courseDescription
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                    shape = RoundedCornerShape(14.dp),
                                    contentPadding = PaddingValues(vertical = 14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("publish_course_storage_button")
                                ) {
                                    Icon(Icons.Filled.CloudDone, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Publish Course & Sync Firebase Storage Path",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun AdminTabButton(
    label: String,
    selected: Boolean,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (selected) IndigoPrimary else MaterialTheme.colorScheme.surface,
        border = if (selected) null else CardDefaults.outlinedCardBorder(),
        modifier = modifier
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}
