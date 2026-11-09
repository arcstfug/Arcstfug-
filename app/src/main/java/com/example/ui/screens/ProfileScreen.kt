package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AiUiState

@Composable
fun ProfileScreen(
    userProfile: UserProfile,
    mockTests: List<TestExam>,
    doubts: List<DoubtQuery>,
    doubtAiState: AiUiState,
    studyPlannerAiState: AiUiState,
    onPostDoubt: (String, SubjectType) -> Unit,
    onGenerateStudyPlan: (Int, SubjectType, String) -> Unit,
    onUpdateProfile: (String, String, String, String, String) -> Unit = { _, _, _, _, _ -> },
    onOpenTeacherAdmin: () -> Unit = {},
    userUid: String? = null,
    userEmail: String? = null,
    onSignOut: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    var uidCopied by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var editName by remember(userProfile.name) { mutableStateOf(userProfile.name) }
    var editTargetExam by remember(userProfile.targetExam) { mutableStateOf(userProfile.targetExam) }
    var editClassLevel by remember(userProfile.classLevel) { mutableStateOf(userProfile.classLevel) }
    var editStudentGoal by remember(userProfile.studentGoal) { mutableStateOf(userProfile.studentGoal) }
    var editBio by remember(userProfile.bio) { mutableStateOf(userProfile.bio) }

    var showDoubtDialog by remember { mutableStateOf(false) }
    var doubtText by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf(SubjectType.PHYSICS) }

    var showStudyPlannerDialog by remember { mutableStateOf(false) }
    var plannerHours by remember { mutableIntStateOf(6) }
    var plannerSubject by remember { mutableStateOf(SubjectType.PHYSICS) }
    var plannerWeakTopics by remember { mutableStateOf("") }

    var selectedExamModal by remember { mutableStateOf<TestExam?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("profile_screen_scroll"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp)
    ) {
        // User Profile Card with Official ARCSTFUG Square Crest Logo & Edit Profile Action
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_header_card")
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Full Square ARCSTFUG Logo with existing background corners
                        Image(
                            painter = painterResource(id = R.drawable.arcstfug_logo),
                            contentDescription = "ARCSTFUG Official Crest",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(68.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White)
                                .border(1.5.dp, AmberStreak, RoundedCornerShape(10.dp))
                                .testTag("profile_header_logo")
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = userProfile.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${userProfile.classLevel} • ${userProfile.targetExam}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "🏆 ${userProfile.studentGoal}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = userProfile.bio,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AmberContainer
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("🔥 ${userProfile.currentStreakDays}d", fontWeight = FontWeight.Bold, color = Color(0xFF78350F))
                                Text("Streak", style = MaterialTheme.typography.labelSmall, color = Color(0xFF78350F))
                            }
                        }
                    }

                    // Cloud Sync Badge
                    if (!userEmail.isNullOrBlank()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldSuccess.copy(alpha = 0.12f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CloudDone,
                                    contentDescription = "Cloud Synced",
                                    tint = EmeraldSuccess,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Cloud Synced • $userEmail",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = EmeraldSuccess
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (onSignOut != null) {
                            OutlinedButton(
                                onClick = onSignOut,
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("sign_out_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Logout,
                                    contentDescription = "Sign Out",
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Sign Out", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        FilledTonalButton(
                            onClick = {
                                editName = userProfile.name
                                editTargetExam = userProfile.targetExam
                                editClassLevel = userProfile.classLevel
                                editStudentGoal = userProfile.studentGoal
                                editBio = userProfile.bio
                                showEditProfileDialog = true
                            },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("edit_profile_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit Student Profile",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit Student Profile", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = onOpenTeacherAdmin,
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_teacher_admin_profile_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CloudUpload,
                            contentDescription = "Teacher Admin Panel",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Open Teacher Admin Panel (Upload Video, DPP & Material)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    // Stats Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ProfileStatItem(
                            label = "Study Hours",
                            value = "${userProfile.studyHours}h",
                            modifier = Modifier.weight(1f)
                        )
                        ProfileStatItem(
                            label = "Lectures",
                            value = "${userProfile.lecturesWatched}",
                            modifier = Modifier.weight(1f)
                        )
                        ProfileStatItem(
                            label = "DPP Solved",
                            value = "${userProfile.totalDppsSolved}",
                            modifier = Modifier.weight(1f)
                        )
                        ProfileStatItem(
                            label = "Accuracy",
                            value = "${userProfile.overallAccuracy}%",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // About Us & Founders Section (Abhishek Kumar & Anirudhh Mishra)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "About Us",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("about_us_founders_card")
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = "About Us",
                                tint = IndigoPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Column {
                                Text(
                                    text = "About ARCSTFUG",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Founders & Visionaries",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = "ARCSTFUG is a premier educational platform dedicated to helping JEE, NEET, and Board aspirants excel through interactive lectures, daily practice problems (DPP), All-India Test Series, and AI-powered tutoring.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            FounderBadgeCard(
                                name = "Abhishek Kumar",
                                role = "Founder • ARCSTFUG",
                                initials = "AK",
                                accentColor = IndigoPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            FounderBadgeCard(
                                name = "Anirudhh Mishra",
                                role = "Founder • ARCSTFUG",
                                initials = "AM",
                                accentColor = CyanAccent,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Gemini AI Study Planner for JEE & NEET Schedules
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                ),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_study_planner_card")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
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
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = "Gemini AI Study Planner",
                                tint = AmberStreak
                            )
                            Column {
                                Text(
                                    text = "Gemini AI JEE / NEET Study Planner",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Personalized 7-day timetable, revision slots & exam strategy",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Button(
                            onClick = { showStudyPlannerDialog = true },
                            enabled = studyPlannerAiState !is AiUiState.Loading,
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("open_ai_study_planner_btn")
                        ) {
                            Text("✨ Plan Week", fontSize = 12.sp)
                        }
                    }

                    when (studyPlannerAiState) {
                        is AiUiState.Idle -> {
                            Text(
                                text = "Tap '✨ Plan Week' to generate a customized 7-day JEE/NEET schedule tailored to your daily study hours and weak topics.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        is AiUiState.Loading -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = IndigoPrimary
                                )
                                Text(
                                    text = "Gemini AI is building your 7-day JEE/NEET timetable...",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        is AiUiState.Streaming -> {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "⚡ Streaming 7-Day Study Plan...",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = CyanAccent
                                    )
                                    Text(
                                        text = studyPlannerAiState.text,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                        is AiUiState.Success -> {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "✨ Your Custom 7-Day JEE/NEET Schedule",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = EmeraldSuccess
                                    )
                                    Text(
                                        text = studyPlannerAiState.text,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                        is AiUiState.Error -> {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = RoseContainer,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "Study Planner Error: ${studyPlannerAiState.message}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = RoseError
                                    )
                                    TextButton(onClick = { showStudyPlannerDialog = true }) {
                                        Text("Retry", color = RoseError, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Achievements & Badges
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Study Badges & Achievements",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(end = 8.dp)
                ) {
                    items(userProfile.badges) { badge ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (badge.isUnlocked) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = CardDefaults.outlinedCardBorder(),
                            modifier = Modifier.width(160.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(badge.icon, fontSize = 24.sp)
                                Text(
                                    text = badge.title,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (badge.isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = badge.description,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }

        // Mock Test Series Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "All-India Test Series (AITS)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Official Portal: deltastudy.fun/missionjeet/251",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Button(
                        onClick = { uriHandler.openUri(ExternalStudyLinks.ALL_INDIA_TEST_SERIES_URL) },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("open_aits_main_btn")
                    ) {
                        Text("Launch Portal", fontSize = 11.sp, color = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Filled.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.White
                        )
                    }
                }

                // ARCSTFUG Official Web App Portal Card inside Profile
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = IndigoPrimary.copy(alpha = 0.1f)),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { uriHandler.openUri(ExternalStudyLinks.ARCSTFUG_WEB_PORTAL_URL) }
                        .testTag("profile_arcstfug_web_card")
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
                                .background(IndigoPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Language,
                                contentDescription = "ARCSTFUG Web App",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ARCSTFUG Official Web App",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "https://arcstfug-app.netlify.app/",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        FilledTonalButton(
                            onClick = { uriHandler.openUri(ExternalStudyLinks.ARCSTFUG_WEB_PORTAL_URL) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Open Web", fontSize = 12.sp)
                        }
                    }
                }

                // Physics Google Drive Quick Access Card inside Profile
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PhysicsColor.copy(alpha = 0.1f)),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { uriHandler.openUri(ExternalStudyLinks.PHYSICS_DRIVE_URL) }
                        .testTag("profile_physics_drive_card")
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
                                .background(PhysicsColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.FolderSpecial,
                                contentDescription = "Physics Drive",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Physics Complete Google Drive Vault",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Notes, Lectures & PYQ Drive Folder",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        FilledTonalButton(
                            onClick = { uriHandler.openUri(ExternalStudyLinks.PHYSICS_DRIVE_URL) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Open Drive", fontSize = 12.sp)
                        }
                    }
                }

                mockTests.forEach { testExam ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedExamModal = testExam }
                            .testTag("test_exam_card_${testExam.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(IndigoPrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Assessment,
                                    contentDescription = null,
                                    tint = IndigoPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = testExam.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${testExam.pattern} • ${testExam.questionsCount} Qs • ${testExam.durationMinutes} mins • ${testExam.totalMarks} Marks",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            FilledTonalButton(
                                onClick = { selectedExamModal = testExam },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Attempt", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Gemini AI Doubt Solver & Student Forum
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = AmberStreak, modifier = Modifier.size(18.dp))
                        Text(
                            text = "Gemini AI Doubt Solver & Forum",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Button(
                        onClick = { showDoubtDialog = true },
                        enabled = doubtAiState !is AiUiState.Loading,
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("ask_doubt_modal_btn")
                    ) {
                        Icon(imageVector = Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ask AI Tutor", fontSize = 12.sp)
                    }
                }

                if (doubtAiState is AiUiState.Error) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = RoseContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "AI Doubt Solver Error: ${doubtAiState.message}",
                            style = MaterialTheme.typography.bodySmall,
                            color = RoseError,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                doubts.forEach { doubt ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier.fillMaxWidth()
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
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Text(
                                            text = doubt.subject.displayName,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = doubt.status,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = IndigoPrimary
                                    )
                                }
                                Text(
                                    text = doubt.timestamp,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Text(
                                text = "Q: ${doubt.questionText}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            if (doubt.facultyReply != null) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "✨ Step-by-Step Solution & Exam Tip:",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = IndigoPrimary
                                        )
                                        Text(
                                            text = doubt.facultyReply,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface
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

    // Ask Gemini AI Doubt Dialog
    if (showDoubtDialog) {
        AlertDialog(
            onDismissRequest = { showDoubtDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = AmberStreak)
                    Text("Ask Gemini AI Tutor")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Select Subject:", style = MaterialTheme.typography.labelSmall)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SubjectType.values().forEach { subj ->
                            val isSel = selectedSubject == subj
                            FilterChip(
                                selected = isSel,
                                onClick = { selectedSubject = subj },
                                label = { Text(subj.displayName.take(4), fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = doubtText,
                        onValueChange = { doubtText = it },
                        placeholder = { Text("Enter any Physics, Chemistry, Maths or Biology problem for step-by-step derivation...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_doubt_input"),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (doubtText.isNotBlank()) {
                            onPostDoubt(doubtText, selectedSubject)
                            doubtText = ""
                            showDoubtDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                    modifier = Modifier.testTag("dialog_doubt_submit")
                ) {
                    Text("Solve with AI")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDoubtDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // AI Study Planner Configuration Dialog
    if (showStudyPlannerDialog) {
        AlertDialog(
            onDismissRequest = { showStudyPlannerDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = AmberStreak)
                    Text("AI 7-Day Study Planner")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Daily Study Hours:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(4, 6, 8, 10).forEach { hrs ->
                            FilterChip(
                                selected = plannerHours == hrs,
                                onClick = { plannerHours = hrs },
                                label = { Text("${hrs}h/day", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Text(
                        text = "Priority Focus Subject:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SubjectType.values().forEach { subj ->
                            FilterChip(
                                selected = plannerSubject == subj,
                                onClick = { plannerSubject = subj },
                                label = { Text(subj.displayName.take(4), fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = plannerWeakTopics,
                        onValueChange = { plannerWeakTopics = it },
                        label = { Text("Weak Chapters / Topics") },
                        placeholder = { Text("e.g. Rotational Mechanics, Ionic Equilibrium, Calculus") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_study_planner_topics_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showStudyPlannerDialog = false
                        onGenerateStudyPlan(plannerHours, plannerSubject, plannerWeakTopics)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                    modifier = Modifier.testTag("confirm_generate_study_plan_btn")
                ) {
                    Text("Generate Schedule")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStudyPlannerDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Exam Modal
    if (selectedExamModal != null) {
        val exam = selectedExamModal!!
        val targetUrl = exam.externalUrl ?: ExternalStudyLinks.ALL_INDIA_TEST_SERIES_URL
        AlertDialog(
            onDismissRequest = { selectedExamModal = null },
            title = { Text(exam.title) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("• Pattern: ${exam.pattern}")
                    Text("• Total Questions: ${exam.questionsCount}")
                    Text("• Total Duration: ${exam.durationMinutes} minutes")
                    Text("• Total Marks: ${exam.totalMarks}")
                    Text("• Status: ${exam.status} (${exam.scheduledDate})")
                    Text("• Portal: $targetUrl", color = IndigoPrimary, style = MaterialTheme.typography.labelSmall)
                    Text("Rules: +4 for correct answer, -1 for wrong response. Calculators not permitted.")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedExamModal = null
                        uriHandler.openUri(targetUrl)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                ) {
                    Text("Enter Exam Room")
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Filled.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedExamModal = null }) {
                    Text("Close")
                }
            }
        )
    }

    // Edit Student Profile Dialog
    if (showEditProfileDialog) {
        val targetExamPresets = listOf(
            "JEE Main & Advanced 2026",
            "NEET-UG 2026",
            "Class 12th Board + JEE",
            "Class 10th Board Booster"
        )
        val classPresets = listOf("Class 10th", "Class 11th", "Class 12th", "Dropper / Target")

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = null, tint = IndigoPrimary)
                    Text("Edit Student Profile")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Student Full Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("edit_profile_name_input")
                    )

                    Text(
                        text = "Select Class / Batch Level:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(classPresets) { preset ->
                            FilterChip(
                                selected = editClassLevel == preset,
                                onClick = { editClassLevel = preset },
                                label = { Text(preset, fontSize = 11.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = editTargetExam,
                        onValueChange = { editTargetExam = it },
                        label = { Text("Target Exam") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("edit_profile_exam_input")
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(targetExamPresets) { examPreset ->
                            SuggestionChip(
                                onClick = { editTargetExam = examPreset },
                                label = { Text(examPreset, fontSize = 10.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = editStudentGoal,
                        onValueChange = { editStudentGoal = it },
                        label = { Text("Target Rank / Academic Goal") },
                        placeholder = { Text("e.g. Aim: Top 100 AIR") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("edit_profile_goal_input")
                    )

                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text("Student Bio / Motto") },
                        maxLines = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("edit_profile_bio_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateProfile(
                            editName,
                            editTargetExam,
                            editClassLevel,
                            editStudentGoal,
                            editBio
                        )
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                    modifier = Modifier.testTag("save_profile_button")
                ) {
                    Text("Save Profile")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun FounderBadgeCard(
    name: String,
    role: String,
    initials: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = CardDefaults.outlinedCardBorder(),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.18f))
                    .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = accentColor
                )
            }
            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = role,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun ProfileStatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
            )
        }
    }
}
