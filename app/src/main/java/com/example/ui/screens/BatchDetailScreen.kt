package com.example.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.*
import com.example.ui.theme.*

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchDetailScreen(
    course: CourseBatch,
    onBack: () -> Unit,
    onOpenLecture: (String) -> Unit,
    onStartDpp: (String) -> Unit,
    onEnrollToggle: () -> Unit,
    onDonateAndSaveBatch: () -> Unit = {},
    isAutoSaveOnOpenEnabled: Boolean = true,
    onToggleAutoSaveOnOpen: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val uriHandler = LocalUriHandler.current
    val subjects = course.subjects
    var selectedSubjectIndex by remember { mutableIntStateOf(0) }
    val currentSubject = subjects.getOrNull(selectedSubjectIndex)

    var activeNotesLecture by remember { mutableStateOf<Lecture?>(null) }
    var showStudyOceanPortalModal by remember { mutableStateOf(false) }

    val totalLecturesCount = remember(course) {
        course.subjects.sumOf { s -> s.chapters.sumOf { ch -> ch.lectures.size } }
    }
    val totalDppsCount = remember(course) {
        course.subjects.sumOf { s -> s.chapters.sumOf { ch -> ch.dpps.size } }
    }
    val totalNotesCount = remember(course) {
        course.subjects.sumOf { s -> s.chapters.sumOf { ch -> ch.lectures.count { it.notesSummary.isNotEmpty() } } }
    }

    fun resolveSubjectPdfVaultUrl(subjectType: SubjectType): String {
        return when (subjectType) {
            SubjectType.PHYSICS -> ExternalStudyLinks.PHYSICS_DRIVE_URL
            SubjectType.CHEMISTRY -> ExternalStudyLinks.IOC_OM_PANDEY_URL
            SubjectType.MATHEMATICS -> ExternalStudyLinks.MATHS_SACHIN_SIR_URL
            SubjectType.BIOLOGY -> ExternalStudyLinks.ALLEN_MODULES_URL
        }
    }

    if (activeNotesLecture != null) {
        val lec = activeNotesLecture!!
        AlertDialog(
            onDismissRequest = { activeNotesLecture = null },
            title = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = IndigoPrimary.copy(alpha = 0.14f)
                    ) {
                        Text(
                            text = "📄 SMART-BOARD CLASS NOTES • ${lec.subject.displayName.uppercase()}",
                            color = IndigoPrimary,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Text(
                        text = lec.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold)
                    )
                    Text(
                        text = "Faculty: ${lec.instructor}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    lec.notesSummary.forEachIndexed { idx, slideNote ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = IndigoPrimary
                                ) {
                                    Text(
                                        text = "#${idx + 1}",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                    )
                                }
                                Text(
                                    text = slideNote,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    if (lec.timestamps.isNotEmpty()) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        Text(
                            text = "⏱️ Lecture Slide Index & Timestamps",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = IndigoPrimary
                        )
                        lec.timestamps.forEach { ts ->
                            Text(
                                text = "• [${ts.timeLabel}] ${ts.title}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            uriHandler.openUri(resolveSubjectPdfVaultUrl(lec.subject))
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Filled.PictureAsPdf, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("PDF Book", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = {
                            val targetId = lec.id
                            activeNotesLecture = null
                            onOpenLecture(targetId)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Watch Lecture", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { activeNotesLecture = null }) {
                    Text("Close")
                }
            }
        )
    }

    if (showStudyOceanPortalModal) {
        Dialog(
            onDismissRequest = { showStudyOceanPortalModal = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1E1B4B))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Public,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = "🌊 StudyOcean PW All-Batches Hub",
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold)
                                )
                                Text(
                                    text = "Live Batch Lectures • DPPs • Class Notes Connected",
                                    color = Color(0xFFBAE6FD),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilledTonalButton(
                                onClick = {
                                    uriHandler.openUri(ExternalStudyLinks.STUDYOCEAN_PW_PORTAL_URL)
                                },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("External ↗", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            IconButton(onClick = { showStudyOceanPortalModal = false }) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Close",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    AndroidView(
                        factory = { ctx ->
                            WebView(ctx).apply {
                                settings.javaScriptEnabled = true
                                settings.domStorageEnabled = true
                                settings.loadsImagesAutomatically = true
                                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                webChromeClient = WebChromeClient()
                                webViewClient = WebViewClient()
                                loadUrl(ExternalStudyLinks.STUDYOCEAN_PW_PORTAL_URL)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.testTag("batch_detail_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = course.title,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("batch_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = onDonateAndSaveBatch,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (course.isDirectSaved) EmeraldSuccess else Color(0xFFD97706)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .testTag("batch_donate_button")
                    ) {
                        Icon(
                            imageVector = if (course.isDirectSaved) Icons.Filled.CloudDone else Icons.Filled.CardGiftcard,
                            contentDescription = "Batch Donate",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (course.isDirectSaved) "🎁 Direct Saved ✓" else "🎁 Batch Donate",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    TextButton(onClick = onEnrollToggle) {
                        Text(
                            text = if (course.isEnrolled) "Enrolled ✓" else "Enroll",
                            color = if (course.isEnrolled) EmeraldSuccess else IndigoPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 28.dp)
        ) {
            // Batch Info Card
            item {
                var selectedPlan by remember(course.availablePlans) {
                    mutableStateOf(course.availablePlans.firstOrNull() ?: "Infinity")
                }
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        if (course.bannerDrawableRes != null) {
                            Image(
                                painter = painterResource(id = course.bannerDrawableRes),
                                contentDescription = course.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(210.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(course.bannerColorHex), Color(0xFF1E1B4B))
                                    )
                                )
                                .padding(18.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0x35FFFFFF)
                                        ) {
                                            Text(
                                                text = course.category,
                                                color = Color.White,
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                        if (!course.featureBadge.isNullOrBlank()) {
                                            Surface(
                                                shape = RoundedCornerShape(20.dp),
                                                color = Color(0xFFFDE68A),
                                                modifier = Modifier.border(1.dp, Color(0xFF78350F), RoundedCornerShape(20.dp))
                                            ) {
                                                Text(
                                                    text = course.featureBadge,
                                                    color = Color(0xFF1E1B4B),
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = "⭐ ${course.rating} • ${course.enrolledCount}",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                                Text(
                                    text = course.title,
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                if (course.availablePlans.isNotEmpty()) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "👑 Multiple plans inside:",
                                            color = Color(0xFFFDE68A),
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        course.availablePlans.forEach { plan ->
                                            val isSelected = selectedPlan == plan
                                            Surface(
                                                shape = RoundedCornerShape(10.dp),
                                                color = if (isSelected) Color(0xFFFDE68A) else Color(0x35FFFFFF),
                                                modifier = Modifier.clickable { selectedPlan = plan }
                                            ) {
                                                Text(
                                                    text = "👑 $plan",
                                                    color = if (isSelected) Color(0xFF1E1B4B) else Color.White,
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                                Text(
                                    text = course.description,
                                    color = Color(0xFFE2E8F0),
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Mentors: ${course.instructorTeam}",
                                    color = CyanAccent,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0x30FFFFFF)
                                    ) {
                                        Text(
                                            text = "🎥 $totalLecturesCount Lectures • 📝 $totalDppsCount DPPs • 📄 $totalNotesCount Class Notes",
                                            color = Color(0xFFFDE68A),
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 🌊 StudyOcean PW Batch Vault & Live Portal Card (Lectures + DPPs + Notes)
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = IndigoPrimary.copy(alpha = 0.10f)
                    ),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("batch_detail_studyocean_card")
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF1E1B4B)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.LibraryBooks,
                                    contentDescription = "StudyOcean PW Hub",
                                    tint = CyanAccent,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "🌊 StudyOcean PW Batch Vault Connected",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "All Video Lectures ($totalLecturesCount) • DPPs ($totalDppsCount) • Class Notes ($totalNotesCount) Active",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { showStudyOceanPortalModal = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1B4B)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("open_studyocean_modal_btn")
                            ) {
                                Icon(Icons.Filled.Public, contentDescription = null, modifier = Modifier.size(15.dp), tint = CyanAccent)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("🌊 Open StudyOcean Batch Hub", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            OutlinedButton(
                                onClick = {
                                    val firstLecWithNotes = currentSubject?.chapters
                                        ?.flatMap { it.lectures }
                                        ?.firstOrNull { it.notesSummary.isNotEmpty() }
                                    if (firstLecWithNotes != null) {
                                        activeNotesLecture = firstLecWithNotes
                                    } else {
                                        uriHandler.openUri(ExternalStudyLinks.STUDYOCEAN_PW_PORTAL_URL)
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("open_first_chapter_notes_btn")
                            ) {
                                Icon(Icons.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("📄 Class Notes", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 🎁 Batch Donate & Direct Auto-Save on Open Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = EmeraldSuccess.copy(alpha = 0.10f)
                    ),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("batch_detail_donate_card")
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(EmeraldSuccess),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.CardGiftcard,
                                        contentDescription = "Batch Donate",
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "🎁 Premium Batch Donate • Direct Save",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (course.isDirectSaved) {
                                            "✅ Open Hote Hi Direct Save Ho Gaya • Donated by ${course.donatedBy}"
                                        } else {
                                            "Tap 'Batch Donate' to permanently save this premium batch"
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = EmeraldSuccess
                                    )
                                }
                            }
                            Switch(
                                checked = isAutoSaveOnOpenEnabled,
                                onCheckedChange = onToggleAutoSaveOnOpen,
                                modifier = Modifier.testTag("batch_detail_autosave_switch")
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = onDonateAndSaveBatch,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (course.isDirectSaved) EmeraldSuccess else IndigoPrimary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("batch_detail_donate_action_btn")
                            ) {
                                Icon(
                                    imageVector = if (course.isDirectSaved) Icons.Filled.Verified else Icons.Filled.VolunteerActivism,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (course.isDirectSaved) {
                                        "🎁 Batch Donated & Direct Saved ✓"
                                    } else {
                                        "🎁 Batch Donate (Direct Save Now)"
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Subject Tab Row
            if (subjects.isNotEmpty()) {
                item {
                    TabRow(
                        selectedTabIndex = selectedSubjectIndex,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = IndigoPrimary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .testTag("batch_subjects_tab_row")
                    ) {
                        subjects.forEachIndexed { index, subj ->
                            Tab(
                                selected = selectedSubjectIndex == index,
                                onClick = { selectedSubjectIndex = index },
                                text = {
                                    Text(
                                        text = subj.type.displayName,
                                        fontWeight = if (selectedSubjectIndex == index) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            )
                        }
                    }
                }

                // Physics Google Drive Folder Link when Physics Tab is Active
                if (currentSubject?.type == SubjectType.PHYSICS) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = PhysicsColor.copy(alpha = 0.12f)
                            ),
                            border = CardDefaults.outlinedCardBorder(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { uriHandler.openUri(ExternalStudyLinks.PHYSICS_DRIVE_URL) }
                                .testTag("batch_physics_drive_card")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.FolderSpecial,
                                    contentDescription = "Physics Drive Folder",
                                    tint = PhysicsColor,
                                    modifier = Modifier.size(28.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Physics Google Drive Folder",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Tap to open complete Physics Drive materials & lectures",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Button(
                                    onClick = { uriHandler.openUri(ExternalStudyLinks.PHYSICS_DRIVE_URL) },
                                    colors = ButtonDefaults.buttonColors(containerColor = PhysicsColor),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("Open Drive", fontSize = 12.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }

                // Chapters & Lectures / DPPs / Class Notes
                if (currentSubject != null && currentSubject.chapters.isNotEmpty()) {
                    items(currentSubject.chapters) { chapter ->
                        val chapterNotesLectures = chapter.lectures.filter { it.notesSummary.isNotEmpty() }
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = CardDefaults.outlinedCardBorder(),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = if (chapter.title.startsWith("Chapter", ignoreCase = true)) chapter.title else "Chapter ${chapter.order}: ${chapter.title}",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${chapter.lectures.size} Lectures • ${chapter.dpps.size} DPP Sheets • ${chapterNotesLectures.size} Class Notes PDF",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                                // Chapter Lectures
                                Text(
                                    text = "🎥 Video Lectures",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )

                                chapter.lectures.forEach { lec ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                            .clickable { onOpenLecture(lec.id) }
                                            .padding(12.dp)
                                            .testTag("batch_lecture_row_${lec.id}"),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    if (lec.isCompleted) EmeraldSuccess else IndigoPrimary
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (lec.isCompleted) Icons.Filled.Check else Icons.Filled.PlayArrow,
                                                contentDescription = "Play",
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = lec.title,
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                                maxLines = 1,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "${lec.instructor} • ${lec.durationMinutes} mins",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        FilledTonalButton(
                                            onClick = { onOpenLecture(lec.id) },
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text(if (lec.progressSec > 0) "Resume" else "Watch", fontSize = 12.sp)
                                        }
                                    }
                                }

                                // Chapter DPPs
                                if (chapter.dpps.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "📝 Daily Practice Problems (DPP)",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = AmberStreak
                                    )

                                    chapter.dpps.forEach { dpp ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                                .clickable { onStartDpp(dpp.id) }
                                                .padding(12.dp)
                                                .testTag("batch_dpp_row_${dpp.id}"),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(
                                                        if (dpp.status == DppStatus.COMPLETED) EmeraldSuccess else AmberStreak
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = if (dpp.status == DppStatus.COMPLETED) Icons.Filled.AssignmentTurnedIn else Icons.Filled.EditNote,
                                                    contentDescription = "DPP",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = dpp.title,
                                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                                    maxLines = 1,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = "${dpp.questions.size} Questions • ${dpp.timeLimitMinutes} mins" +
                                                            if (dpp.status == DppStatus.COMPLETED) " • Score: ${dpp.score}/${dpp.questions.size}" else "",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }

                                            Button(
                                                onClick = { onStartDpp(dpp.id) },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (dpp.status == DppStatus.COMPLETED) EmeraldSuccess else AmberStreak
                                                ),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                Text(
                                                    text = if (dpp.status == DppStatus.COMPLETED) "Solutions" else "Solve",
                                                    fontSize = 12.sp,
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }
                                }

                                // Chapter Class Notes & Study Material PDF
                                if (chapterNotesLectures.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "📄 Smart-Board Class Notes & Formula PDF",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = EmeraldSuccess
                                    )

                                    chapterNotesLectures.forEach { lec ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(EmeraldSuccess.copy(alpha = 0.08f))
                                                .clickable { activeNotesLecture = lec }
                                                .padding(12.dp)
                                                .testTag("batch_notes_row_${lec.id}"),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(EmeraldSuccess),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Description,
                                                    contentDescription = "Class Notes",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "Notes • ${lec.title}",
                                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                                    maxLines = 1,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = "${lec.notesSummary.size} Smart-Board Slides • ${lec.instructor}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }

                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                OutlinedButton(
                                                    onClick = {
                                                        uriHandler.openUri(resolveSubjectPdfVaultUrl(lec.subject))
                                                    },
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                                ) {
                                                    Text("PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                                Button(
                                                    onClick = { activeNotesLecture = lec },
                                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                                ) {
                                                    Text("Read Notes", fontSize = 11.sp, color = Color.White)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Lectures, DPPs & Notes for this subject are updating soon!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

