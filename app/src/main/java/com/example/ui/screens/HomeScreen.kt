package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.example.R
import com.example.data.PwCourseApiService
import com.example.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AiUiState
import com.example.ui.viewmodel.MainNavTab

@Composable
fun HomeScreen(
    userProfile: UserProfile,
    courses: List<CourseBatch>,
    allLectures: List<Lecture>,
    allDpps: List<Dpp>,
    onNavigateTab: (MainNavTab) -> Unit,
    onOpenBatch: (String) -> Unit,
    onOpenLecture: (String) -> Unit,
    onStartDpp: (String) -> Unit,
    studyMaterials: List<StudyMaterial> = emptyList(),
    onOpenTeacherAdmin: () -> Unit = { onNavigateTab(MainNavTab.TEACHER_ADMIN) },
    pwApiSyncState: AiUiState = AiUiState.Idle,
    lastSyncedPwBatchId: String = "c_pw_lakshya_jee_2026",
    onSendPwApiRequest: (String, String, Boolean) -> Unit = { _, _, _ -> },
    announcements: List<AnnouncementNotice> = emptyList(),
    arcstfugPortalAiState: AiUiState = AiUiState.Idle,
    onAskArcstfugPortalAi: (String, Boolean) -> Unit = { _, _ -> },
    onPostCustomAnnouncement: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current
    var showNewNoticeForm by remember { mutableStateOf(false) }
    var noticeTitleInput by remember { mutableStateOf("") }
    var noticeCategoryInput by remember { mutableStateOf("OFFICIAL NOTICE") }
    var noticeMessageInput by remember { mutableStateOf("") }
    var arcstfugAiQueryInput by remember { mutableStateOf("") }

    val activeLecture = allLectures.firstOrNull { it.progressSec > 0 && !it.isCompleted }
        ?: allLectures.firstOrNull()

    val pendingDpp = allDpps.firstOrNull { it.status == DppStatus.PENDING }
        ?: allDpps.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("home_screen_scroll"),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp)
    ) {
        // Welcome & Daily Motivation Card with Official ARCSTFUG Square Crest Logo
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("welcome_streak_card")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                    CyanContainer.copy(alpha = 0.3f)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Full Square ARCSTFUG Logo with existing background corners
                            Image(
                                painter = painterResource(id = R.drawable.arcstfug_logo),
                                contentDescription = "ARCSTFUG Crest Emblem",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White)
                                    .border(1.5.dp, AmberStreak, RoundedCornerShape(8.dp))
                                    .testTag("home_hero_logo")
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Welcome back, ${userProfile.name} 👋",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Target: ${userProfile.targetExam}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = AmberStreak,
                                modifier = Modifier.testTag("streak_badge_hero")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("🔥", fontSize = 16.sp)
                                    Text(
                                        "${userProfile.currentStreakDays} Days",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), thickness = 1.dp)

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("💡", fontSize = 16.sp)
                            Text(
                                text = "\"Excellence is not an act, but a daily habit. Complete today's lecture & DPP!\"",
                                style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // =====================================================================
        // 📢 OFFICIAL ANNOUNCEMENTS, NOTICE BOARD & arcstfug-app.netlify.app AI
        // =====================================================================
        item {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // 1. Official Announcements & Notice Board Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_announcements_notice_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
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
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(AmberStreak.copy(alpha = 0.18f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Campaign,
                                        contentDescription = "Official Announcements & Notice Board",
                                        tint = AmberStreak,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "📢 Announcements & Notice Board",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Live Updates • JEE, NEET & Class 9–12 Official Portal",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            FilledTonalButton(
                                onClick = { showNewNoticeForm = !showNewNoticeForm },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("toggle_new_notice_form_btn")
                            ) {
                                Icon(
                                    imageVector = if (showNewNoticeForm) Icons.Filled.Close else Icons.Filled.AddAlert,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (showNewNoticeForm) "Close" else "+ Notice",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (showNewNoticeForm) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.28f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("new_notice_composer_box")
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Publish New Official Announcement / Notice",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = IndigoPrimary
                                    )
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        val tags = listOf("OFFICIAL NOTICE", "URGENT ALERT", "PW BATCH UPDATE", "EXAM SCHEDULE", "ARCSTFUG AI")
                                        items(tags) { tagItem ->
                                            FilterChip(
                                                selected = noticeCategoryInput == tagItem,
                                                onClick = { noticeCategoryInput = tagItem },
                                                label = { Text(tagItem, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                                            )
                                        }
                                    }
                                    OutlinedTextField(
                                        value = noticeTitleInput,
                                        onValueChange = { noticeTitleInput = it },
                                        label = { Text("Announcement / Notice Title") },
                                        placeholder = { Text("e.g. 🚨 All-India JEE & NEET Mock Test Live Today at 6 PM") },
                                        singleLine = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("notice_title_input")
                                    )
                                    OutlinedTextField(
                                        value = noticeMessageInput,
                                        onValueChange = { noticeMessageInput = it },
                                        label = { Text("Notice Details / Message") },
                                        placeholder = { Text("Enter important instructions, schedule, or link details for students...") },
                                        minLines = 2,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("notice_message_input")
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                onPostCustomAnnouncement(
                                                    noticeTitleInput.ifBlank { "📢 Official ARCSTFUG Notice" },
                                                    noticeMessageInput.ifBlank {
                                                        "Important update posted on ARCSTFUG Official Portal. Check your batch schedule and daily DPPs."
                                                    },
                                                    noticeCategoryInput,
                                                    ExternalStudyLinks.ARCSTFUG_WEB_PORTAL_URL
                                                )
                                                noticeTitleInput = ""
                                                noticeMessageInput = ""
                                                showNewNoticeForm = false
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("publish_notice_submit_btn")
                                        ) {
                                            Icon(Icons.Filled.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Publish Notice", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        announcements.take(4).forEach { notice ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (notice.actionUrl.startsWith("http")) {
                                            uriHandler.openUri(notice.actionUrl)
                                        }
                                    }
                                    .testTag("notice_item_${notice.id}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = IndigoPrimary.copy(alpha = 0.14f)
                                        ) {
                                            Text(
                                                text = notice.categoryTag,
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                                color = IndigoPrimary,
                                                fontSize = 10.sp,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                        Text(
                                            text = notice.timestampLabel,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 10.sp
                                        )
                                    }
                                    Text(
                                        text = notice.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = notice.message,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "By ${notice.authorName}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = 10.sp
                                        )
                                        Text(
                                            text = "View Details ↗",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = IndigoPrimary,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. 🤖 arcstfug-app.netlify.app Official AI Assistant & Smart Notice Generator Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_arcstfug_netlify_ai_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        IndigoPrimary.copy(alpha = 0.12f),
                                        CyanContainer.copy(alpha = 0.30f)
                                    )
                                )
                            )
                            .padding(16.dp),
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
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(IndigoPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.AutoAwesome,
                                        contentDescription = "ARCSTFUG AI Assistant",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "🤖 ARCSTFUG Official AI Assistant",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "AI Doubt Mentor, Formula Guide & Smart Notice Generator",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            FilledTonalButton(
                                onClick = { uriHandler.openUri(ExternalStudyLinks.ARCSTFUG_WEB_PORTAL_URL) },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("arcstfug_ai_open_portal_btn")
                            ) {
                                Icon(Icons.Filled.Language, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Web Portal ↗", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        val aiQuickPrompts = listOf(
                            "📢 Generate Today's Official JEE & NEET Batch Notice",
                            "⚡ PW All Batches (9–12, JEE, NEET) Study Strategy",
                            "📘 High-Yield Physics & Chemistry Formula Revision",
                            "🌐 Complete ARCSTFUG Study Vault Guide"
                        )

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(aiQuickPrompts) { quickPrompt ->
                                OutlinedButton(
                                    onClick = {
                                        arcstfugAiQueryInput = quickPrompt
                                        onAskArcstfugPortalAi(quickPrompt, false)
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(quickPrompt, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }

                        OutlinedTextField(
                            value = arcstfugAiQueryInput,
                            onValueChange = { arcstfugAiQueryInput = it },
                            label = { Text("Ask ARCSTFUG AI or Enter Notice Topic") },
                            placeholder = { Text("e.g. Write an exam notice for Lakshya JEE & Yakeen NEET or explain Gauss Law...") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("arcstfug_ai_query_input")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    onAskArcstfugPortalAi(arcstfugAiQueryInput, false)
                                },
                                enabled = arcstfugPortalAiState !is AiUiState.Loading && arcstfugPortalAiState !is AiUiState.Streaming,
                                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("ask_arcstfug_ai_btn")
                            ) {
                                Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ask ARCSTFUG AI", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    onAskArcstfugPortalAi(
                                        arcstfugAiQueryInput.ifBlank {
                                            "Generate an official announcement notice for JEE, NEET & Class 9–12 students"
                                        },
                                        true
                                    )
                                },
                                enabled = arcstfugPortalAiState !is AiUiState.Loading && arcstfugPortalAiState !is AiUiState.Streaming,
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1.1f)
                                    .testTag("ai_post_notice_btn")
                            ) {
                                Icon(Icons.Filled.Campaign, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("AI Post as Notice", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        when (arcstfugPortalAiState) {
                            is AiUiState.Loading -> {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                    Text(
                                        text = "ARCSTFUG AI is thinking...",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = IndigoPrimary
                                    )
                                }
                            }
                            is AiUiState.Streaming -> {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = arcstfugPortalAiState.text,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                            is AiUiState.Success -> {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = EmeraldSuccess.copy(alpha = 0.12f),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("arcstfug_ai_response_box")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "✨ ARCSTFUG Official AI Response",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                            color = EmeraldSuccess
                                        )
                                        Text(
                                            text = arcstfugPortalAiState.text,
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
                                    Text(
                                        text = arcstfugPortalAiState.message,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = RoseError,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                            else -> {}
                        }
                    }
                }
            }
        }

        // Quick Navigation Hub
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Quick Study Desk",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickHubItem(
                        title = "Batches",
                        subtitle = "Courses & Syllabus",
                        icon = Icons.Filled.School,
                        color = IndigoPrimary,
                        modifier = Modifier.weight(1f),
                        testTag = "hub_batches",
                        onClick = { onNavigateTab(MainNavTab.BATCHES) }
                    )
                    QuickHubItem(
                        title = "Lectures",
                        subtitle = "Video Classes",
                        icon = Icons.Filled.PlayCircle,
                        color = CyanAccent,
                        modifier = Modifier.weight(1f),
                        testTag = "hub_lectures",
                        onClick = { onNavigateTab(MainNavTab.LECTURES) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickHubItem(
                        title = "DPP Bank",
                        subtitle = "Daily Practice",
                        icon = Icons.Filled.AssignmentTurnedIn,
                        color = AmberStreak,
                        modifier = Modifier.weight(1f),
                        testTag = "hub_dpp",
                        onClick = { onNavigateTab(MainNavTab.DPP) }
                    )
                    QuickHubItem(
                        title = "Mock Tests",
                        subtitle = "All-India Series",
                        icon = Icons.Filled.Assessment,
                        color = EmeraldSuccess,
                        modifier = Modifier.weight(1f),
                        testTag = "hub_tests",
                        onClick = { onNavigateTab(MainNavTab.PROFILE) }
                    )
                }

                // Teacher / Faculty Admin Panel Card (Upload Video, DPP & Study Material)
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenTeacherAdmin() }
                        .testTag("open_teacher_admin_home_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        IndigoPrimary.copy(alpha = 0.14f),
                                        AmberStreak.copy(alpha = 0.12f)
                                    )
                                )
                            )
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(IndigoPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CloudUpload,
                                contentDescription = "Teacher Admin Studio",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Teacher & Faculty Admin Panel",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Upload Video Lectures, Create Chapter DPPs & Share PDF Study Materials",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = onOpenTeacherAdmin,
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("open_teacher_admin_home_btn")
                        ) {
                            Text("Upload", fontSize = 12.sp, color = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = Color.White
                            )
                        }
                    }
                }

                // ⚡ PW (Physics Wallah) API Course Bridge Card on Home Screen
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_pw_api_bridge_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF1E1B4B)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Api,
                                    contentDescription = "PW API Bridge",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "⚡ PW All Courses API Bridge",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = EmeraldSuccess.copy(alpha = 0.16f)
                                    ) {
                                        Text(
                                            text = "🟢 CONNECTED",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = EmeraldSuccess,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "JEE (Lakshya, Arjuna, Prayas) • NEET (Yakeen, Lakshya, Arjuna) • Class 9–12",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        val homePwPresets = listOf(
                            Triple("🌐 All JEE + NEET + 9–12", "ALL_JEE_NEET_9_TO_12", PwCourseApiService.DEFAULT_PW_BATCHES_API_URL),
                            Triple("⚡ Lakshya JEE (12th)", "LAKSHYA_JEE_2026", "https://api.penpencil.co/v3/batches/all-batches?mode=1&filter=true&exam=JEE&class=12"),
                            Triple("🔥 Arjuna JEE (11th)", "ARJUNA_JEE_2026", "https://api.penpencil.co/v3/batches/all-batches?mode=1&filter=true&exam=JEE&class=11"),
                            Triple("🚀 Prayas JEE (Dropper)", "PRAYAS_JEE_2026", "https://api.penpencil.co/v3/batches/all-batches?mode=1&filter=true&exam=JEE&class=12+"),
                            Triple("🩺 Yakeen NEET (Dropper)", "YAKEEN_NEET_2026", "https://api.penpencil.co/v3/batches/all-batches?mode=1&filter=true&exam=NEET&class=12+"),
                            Triple("🧬 Lakshya NEET (12th)", "LAKSHYA_NEET_2026", "https://api.penpencil.co/v3/batches/all-batches?mode=1&filter=true&exam=NEET&class=12"),
                            Triple("🔬 Arjuna NEET (11th)", "ARJUNA_NEET_2026", "https://api.penpencil.co/v3/batches/all-batches?mode=1&filter=true&exam=NEET&class=11"),
                            Triple("🎓 Parishram Class 12", "PARISHRAM_CLASS12_2026", "https://api.penpencil.co/v3/batches/all-batches?mode=1&filter=true&exam=BOARD_EXAM&class=12"),
                            Triple("📘 Uday Class 11", "UDAY_CLASS11_2026", "https://api.penpencil.co/v3/batches/all-batches?mode=1&filter=true&exam=BOARD_EXAM&class=11"),
                            Triple("🎯 Udaan Class 10", "UDAAN_CLASS10_2026", "https://api.penpencil.co/v3/batches/all-batches?mode=1&filter=true&exam=BOARD_EXAM&class=10"),
                            Triple("🌱 Neev Class 9", "NEEV_CLASS9_2026", "https://api.penpencil.co/v3/batches/all-batches?mode=1&filter=true&exam=BOARD_EXAM&class=9")
                        )

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(homePwPresets) { (label, tag, endpoint) ->
                                OutlinedButton(
                                    onClick = {
                                        onSendPwApiRequest(endpoint, tag, false)
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("home_pw_api_${tag.lowercase()}_btn")
                                ) {
                                    Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    onSendPwApiRequest(
                                        PwCourseApiService.DEFAULT_PW_BATCHES_API_URL,
                                        "ALL_JEE_NEET_9_TO_12",
                                        false
                                    )
                                },
                                enabled = pwApiSyncState !is AiUiState.Loading,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1B4B)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1.35f)
                                    .testTag("home_send_pw_api_request_btn")
                            ) {
                                if (pwApiSyncState is AiUiState.Loading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Syncing All PW Courses...", fontSize = 11.sp, color = Color.White)
                                } else {
                                    Icon(
                                        imageVector = Icons.Filled.Send,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Send PW All Courses API Request",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    val existing = courses.find { it.id == lastSyncedPwBatchId }
                                    if (existing != null) {
                                        onOpenBatch(existing.id)
                                    } else {
                                        onSendPwApiRequest(
                                            PwCourseApiService.DEFAULT_PW_BATCHES_API_URL,
                                            "ALL_JEE_NEET_9_TO_12",
                                            true
                                        )
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(0.9f)
                                    .testTag("home_open_pw_course_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp),
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Run Course",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        if (pwApiSyncState is AiUiState.Success) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = EmeraldSuccess.copy(alpha = 0.12f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = pwApiSyncState.text,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    val pwSyncedBatches = courses.filter { it.id.startsWith("c_pw_") || it.id.startsWith("pw_") }
                                    if (pwSyncedBatches.isNotEmpty()) {
                                        Text(
                                            text = "Tap any Connected PW Course below to Open Lectures & DPPs:",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                            color = EmeraldSuccess
                                        )
                                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            items(pwSyncedBatches) { pwBatch ->
                                                FilledTonalButton(
                                                    onClick = { onOpenBatch(pwBatch.id) },
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                    modifier = Modifier.testTag("open_synced_${pwBatch.id}")
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.PlayCircleFilled,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = pwBatch.title.replace(" (API Connected)", "").replace(" API)", ")"),
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Button(
                                        onClick = { onOpenBatch(lastSyncedPwBatchId) },
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("home_open_synced_pw_batch_btn")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.PlayCircleFilled,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Open Primary PW Batch (Watch Lectures & Solve DPPs)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Teacher-Uploaded Study Materials (if any custom materials were uploaded)
        val customMaterials = studyMaterials.filter { it.isTeacherUpload }
        if (customMaterials.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Newly Uploaded Faculty Study Materials (${customMaterials.size})",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    customMaterials.forEach { mat ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = CardDefaults.outlinedCardBorder(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (mat.resourceUrl.startsWith("http")) {
                                        uriHandler.openUri(mat.resourceUrl)
                                    }
                                }
                                .testTag("home_teacher_material_${mat.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(EmeraldSuccess.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.PictureAsPdf,
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
                                        text = "${mat.category} • ${mat.subject.displayName} (${mat.chapterTitle}) • By ${mat.authorName}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = mat.description,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 2
                                    )
                                }
                                if (mat.resourceUrl.startsWith("http")) {
                                    Icon(
                                        imageVector = Icons.Filled.OpenInNew,
                                        contentDescription = "Open Material",
                                        tint = IndigoPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Direct Study Vault & Live Portals (ARCSTFUG Web App + JEE Physics Vault + EJEE Books + Allen + JEE Adv PYQs + AITS)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Official Study Vault, Books & Live Web Portals",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                // 0) ARCSTFUG Official Web App Portal (arcstfug-app.netlify.app)
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { uriHandler.openUri(ExternalStudyLinks.ARCSTFUG_WEB_PORTAL_URL) }
                        .testTag("home_arcstfug_web_portal_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        IndigoPrimary.copy(alpha = 0.14f),
                                        CyanAccent.copy(alpha = 0.12f)
                                    )
                                )
                            )
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(IndigoPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Language,
                                contentDescription = "ARCSTFUG Official Web App",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ARCSTFUG Official Web App Portal",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Official Cloud Study Platform • Live Classes & Notes",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Button(
                            onClick = { uriHandler.openUri(ExternalStudyLinks.ARCSTFUG_WEB_PORTAL_URL) },
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("open_arcstfug_web_btn")
                        ) {
                            Text("Open Web", fontSize = 12.sp, color = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Filled.OpenInNew,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = Color.White
                            )
                        }
                    }
                }

                // ⚡ JEE PHYSICS VAULT | DROP #01 (DC Pandey, HC Verma, Irodov)
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_jee_physics_vault_drop1_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = PhysicsColor.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "⚡ DROP #01",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                    color = PhysicsColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "⚡ JEE PHYSICS VAULT | DROP #01",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Shared by Abhishek Kumar • Complete Google Drive Folders",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { uriHandler.openUri(ExternalStudyLinks.DC_PANDEY_DRIVE_URL) },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("open_dc_pandey_btn")
                            ) {
                                Text("📘 DC Pandey", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = { uriHandler.openUri(ExternalStudyLinks.HC_VERMA_DRIVE_URL) },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("open_hc_verma_btn")
                            ) {
                                Text("📗 HC Verma", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = { uriHandler.openUri(ExternalStudyLinks.IRODOV_DRIVE_URL) },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("open_irodov_btn")
                            ) {
                                Text("📕 Irodov", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // 📚 EJEE Books PDF – Google Drive Links (All in One)
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_ejee_books_pdf_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "📚 EJEE Books PDF Vault (All in One)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Access and download all important EJEE preparation PDFs anytime 👇",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        val ejeeLinks = listOf(
                            Triple("📘 Inorganic Chemistry (IOC) – Om Pandey Sir", ExternalStudyLinks.IOC_OM_PANDEY_URL, "open_ioc_om_pandey_btn"),
                            Triple("📗 Organic Chemistry – Rohit Agarwal Sir", ExternalStudyLinks.OC_ROHIT_AGARWAL_URL, "open_oc_rohit_agarwal_btn"),
                            Triple("📙 Physical Chemistry – Complete Book PDF", ExternalStudyLinks.PHYSICAL_CHEM_URL, "open_physical_chem_btn"),
                            Triple("📕 Maths (Class 11) – Sachin Sir", ExternalStudyLinks.MATHS_SACHIN_SIR_URL, "open_maths_sachin_sir_btn"),
                            Triple("📓 SKC Class 11 Full Book – Dhirendra Kumar", ExternalStudyLinks.SKC_CLASS_11_BOOK_URL, "open_skc_book_btn")
                        )

                        ejeeLinks.forEach { (label, url, tag) ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { uriHandler.openUri(url) }
                                    .testTag(tag)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        imageVector = Icons.Filled.OpenInNew,
                                        contentDescription = "Open PDF",
                                        tint = IndigoPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Allen Modules & Allen Material + 📚 JEE Advanced PYQs (2010–2025)
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_allen_and_pyq_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "📂 Allen Study Material & 📚 JEE Advanced PYQs (2010–2025)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "✅ Year-wise Question Papers (2010–2025) • ✅ Allen Modules & Classroom Material",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilledTonalButton(
                                onClick = { uriHandler.openUri(ExternalStudyLinks.ALLEN_MODULES_URL) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("open_allen_modules_btn")
                            ) {
                                Text("📂 Allen Modules", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            FilledTonalButton(
                                onClick = { uriHandler.openUri(ExternalStudyLinks.ALLEN_MATERIAL_URL) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("open_allen_material_btn")
                            ) {
                                Text("📂 Allen Material", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = { uriHandler.openUri(ExternalStudyLinks.JEE_ADV_PYQ_2010_2025_URL) },
                            colors = ButtonDefaults.buttonColors(containerColor = AmberStreak),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("open_jee_adv_pyq_btn")
                        ) {
                            Text(
                                text = "📚 JEE Advanced PYQs (2010–2025) Drive",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Filled.OpenInNew,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp),
                                tint = Color.White
                            )
                        }
                    }
                }

                // 1) Physics Google Drive Vault Card
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { uriHandler.openUri(ExternalStudyLinks.PHYSICS_DRIVE_URL) }
                        .testTag("home_physics_drive_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(PhysicsColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.FolderSpecial,
                                contentDescription = "Physics Google Drive",
                                tint = PhysicsColor,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Physics Complete Study Material (Drive)",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Chapterwise Notes, Formula Sheets, PYQs & Lectures Folder",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = { uriHandler.openUri(ExternalStudyLinks.PHYSICS_DRIVE_URL) },
                            colors = ButtonDefaults.buttonColors(containerColor = PhysicsColor),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("open_physics_drive_btn")
                        ) {
                            Text("Open Drive", fontSize = 12.sp, color = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Filled.OpenInNew,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = Color.White
                            )
                        }
                    }
                }

                // 2) All India Test Series (Mission JEET Portal) Card
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { uriHandler.openUri(ExternalStudyLinks.ALL_INDIA_TEST_SERIES_URL) }
                        .testTag("home_aits_portal_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(EmeraldSuccess.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.EmojiEvents,
                                contentDescription = "All India Test Series",
                                tint = EmeraldSuccess,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "All-India Test Series (Mission JEET)",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Live JEE / NEET CBT Exam Portal • All-India Rank",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = { uriHandler.openUri(ExternalStudyLinks.ALL_INDIA_TEST_SERIES_URL) },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("open_aits_portal_btn")
                        ) {
                            Text("Start Test", fontSize = 12.sp, color = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Filled.OpenInNew,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Continue Watching / Active Lecture Section
        if (activeLecture != null) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Continue Watching",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        TextButton(onClick = { onNavigateTab(MainNavTab.LECTURES) }) {
                            Text("All Lectures", color = IndigoPrimary)
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenLecture(activeLecture.id) }
                            .testTag("continue_lecture_card")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Video Thumbnail Box
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        when (activeLecture.subject) {
                                            SubjectType.PHYSICS -> PhysicsColor
                                            SubjectType.CHEMISTRY -> ChemistryColor
                                            SubjectType.MATHEMATICS -> MathsColor
                                            SubjectType.BIOLOGY -> BiologyColor
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = "Play Lecture",
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = activeLecture.subject.displayName,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = activeLecture.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    maxLines = 1,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Instructor: ${activeLecture.instructor} • ${activeLecture.durationMinutes} min",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                val progressFrac = (activeLecture.progressSec.toFloat() / (activeLecture.durationMinutes * 60)).coerceIn(0f, 1f)
                                LinearProgressIndicator(
                                    progress = { progressFrac },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = CyanAccent,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Today's Daily Practice Problem (DPP) Challenge
        if (pendingDpp != null) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Today's Daily Practice Problem",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onStartDpp(pendingDpp.id) }
                            .testTag("today_dpp_card")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = AmberContainer
                                    ) {
                                        Text(
                                            text = "DPP #${pendingDpp.number}",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Color(0xFF78350F),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                    Text(
                                        text = pendingDpp.subject.displayName,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = "⏱ ${pendingDpp.timeLimitMinutes} mins",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Text(
                                text = pendingDpp.title,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${pendingDpp.questions.size} Questions • Immediate Solution Analysis",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Button(
                                    onClick = { onStartDpp(pendingDpp.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.testTag("solve_today_dpp_button")
                                ) {
                                    Text("Solve Now")
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Enrolled & Recommended Batches
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Batches & Courses",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = { onNavigateTab(MainNavTab.BATCHES) }) {
                        Text("View All", color = IndigoPrimary)
                    }
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(end = 8.dp)
                ) {
                    items(courses) { course ->
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = CardDefaults.outlinedCardBorder(),
                            modifier = Modifier
                                .width(295.dp)
                                .clickable { onOpenBatch(course.id) }
                                .testTag("home_course_card_${course.id}")
                        ) {
                            Column {
                                if (course.bannerDrawableRes != null) {
                                    Image(
                                        painter = painterResource(id = course.bannerDrawableRes),
                                        contentDescription = course.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(145.dp)
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(110.dp)
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(
                                                        Color(course.bannerColorHex),
                                                        IndigoDark
                                                    )
                                                )
                                            )
                                            .padding(14.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.align(Alignment.BottomStart)
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = Color(0x30FFFFFF)
                                            ) {
                                                Text(
                                                    text = course.category,
                                                    color = Color.White,
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = course.title,
                                                color = Color.White,
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }

                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (course.availablePlans.isNotEmpty() || !course.featureBadge.isNullOrBlank()) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (course.availablePlans.isNotEmpty()) {
                                                Text(
                                                    text = "👑 Plans: ${course.availablePlans.joinToString(", ")}",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                                    color = Color(0xFF78350F)
                                                )
                                            }
                                            if (!course.featureBadge.isNullOrBlank()) {
                                                Surface(
                                                    shape = RoundedCornerShape(12.dp),
                                                    color = Color(0xFFFDE68A),
                                                    modifier = Modifier.border(1.dp, Color(0xFF78350F), RoundedCornerShape(12.dp))
                                                ) {
                                                    Text(
                                                        text = course.featureBadge,
                                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                                        color = Color(0xFF1E1B4B),
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Text(
                                        text = course.title,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "⭐ ${course.rating} (${course.enrolledCount})",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = if (course.isEnrolled) "Enrolled" else course.price,
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (course.isEnrolled) EmeraldSuccess else IndigoPrimary
                                            )
                                        )
                                    }

                                    Text(
                                        text = course.instructorTeam,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )

                                    if (course.isEnrolled) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Syllabus Progress",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = "${course.syllabusCoveragePercent}%",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        LinearProgressIndicator(
                                            progress = { course.syllabusCoveragePercent / 100f },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(5.dp)
                                                .clip(RoundedCornerShape(3.dp)),
                                            color = IndigoPrimary,
                                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // About Us Section on Dashboard (Home Screen)
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
                        .testTag("home_about_us_card")
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.arcstfug_logo),
                                contentDescription = "ARCSTFUG Logo",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White)
                                    .border(1.dp, AmberStreak, RoundedCornerShape(10.dp))
                            )
                            Column {
                                Text(
                                    text = "About ARCSTFUG",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Premier JEE, NEET & Board Learning Ecosystem",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Text(
                            text = "ARCSTFUG is built to empower students across India with structured video lectures, daily practice problems (DPPs), live All-India Test Series, and Gemini AI-powered mentorship.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                        Text(
                            text = "Founders",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = IndigoPrimary.copy(alpha = 0.1f),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Verified,
                                        contentDescription = null,
                                        tint = IndigoPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "Abhishek Kumar",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Founder",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = IndigoPrimary
                                        )
                                    }
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = CyanAccent.copy(alpha = 0.12f),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Verified,
                                        contentDescription = null,
                                        tint = CyanAccent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "Anirudhh Mishra",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Founder",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = CyanAccent
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
fun QuickHubItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    testTag: String = "",
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder(),
        modifier = modifier
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
