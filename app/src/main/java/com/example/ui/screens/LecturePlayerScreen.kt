package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Lecture
import com.example.ui.components.VideoPlayerView
import com.example.ui.theme.*
import com.example.ui.viewmodel.AiUiState

@Composable
fun LecturePlayerScreen(
    lecture: Lecture,
    isPlaying: Boolean,
    progressSec: Int,
    playbackSpeed: Float,
    videoQuality: String,
    lectureAiSummaryState: AiUiState,
    doubtAiState: AiUiState,
    onTogglePlayPause: () -> Unit,
    onSeek: (Int) -> Unit,
    onSkipBackward: () -> Unit,
    onSkipForward: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onQualityChange: (String) -> Unit,
    onBookmarkToggle: () -> Unit,
    onBack: () -> Unit,
    onStartDpp: (String) -> Unit,
    onSummarizeWithAi: () -> Unit,
    onPostDoubt: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val uriHandler = LocalUriHandler.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var doubtInput by remember { mutableStateOf("") }
    var notesDownloaded by remember { mutableStateOf(false) }

    val tabs = listOf("Notes & AI", "Timestamps", "Related DPP", "AI Doubt Tutor")

    Scaffold(
        modifier = modifier.testTag("lecture_player_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Interactive Video Player View
            VideoPlayerView(
                lecture = lecture,
                isPlaying = isPlaying,
                progressSec = progressSec,
                playbackSpeed = playbackSpeed,
                videoQuality = videoQuality,
                onTogglePlayPause = onTogglePlayPause,
                onSeek = onSeek,
                onSkipBackward = onSkipBackward,
                onSkipForward = onSkipForward,
                onSpeedChange = onSpeedChange,
                onQualityChange = onQualityChange,
                onBookmarkToggle = onBookmarkToggle,
                onBack = onBack,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (!lecture.videoUrl.isNullOrBlank()) {
                Surface(
                    color = IndigoPrimary.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Teacher Uploaded Video Stream Attached",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = IndigoPrimary
                            )
                            Text(
                                text = lecture.videoUrl,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                        if (lecture.videoUrl.startsWith("http")) {
                            FilledTonalButton(
                                onClick = { uriHandler.openUri(lecture.videoUrl) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("open_external_video_url_btn")
                            ) {
                                Icon(Icons.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Open Stream", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Tabs for Notes & AI, Timestamps, DPP, AI Doubt Tutor
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = IndigoPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("lecture_player_tab_row")
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    )
                }
            }

            // Tab Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        // Notes & Formulas
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = CardDefaults.outlinedCardBorder(),
                                modifier = Modifier.fillMaxWidth()
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
                                        Text(
                                            text = "Key Formulas & Lecture Notes",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        FilledTonalButton(
                                            onClick = { notesDownloaded = true },
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.testTag("download_notes_btn")
                                        ) {
                                            Icon(
                                                imageVector = if (notesDownloaded) Icons.Filled.Check else Icons.Filled.Download,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(if (notesDownloaded) "Saved PDF" else "Save PDF", fontSize = 12.sp)
                                        }
                                    }

                                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                                    lecture.notesSummary.forEach { note ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .padding(top = 6.dp)
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(CyanAccent)
                                            )
                                            Text(
                                                text = note,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Gemini AI Concept & Derivation Summarizer Card
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                                ),
                                border = CardDefaults.outlinedCardBorder(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("ai_lecture_summarizer_card")
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
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.AutoAwesome,
                                                contentDescription = "Gemini AI",
                                                tint = AmberStreak
                                            )
                                            Column {
                                                Text(
                                                    text = "Gemini AI Derivation & Exam Sheet",
                                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = "Step-by-step derivations + JEE/NEET shortcuts",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        Button(
                                            onClick = onSummarizeWithAi,
                                            enabled = lectureAiSummaryState !is AiUiState.Loading,
                                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                            modifier = Modifier.testTag("generate_ai_lecture_summary_btn")
                                        ) {
                                            Text(
                                                text = if (lectureAiSummaryState is AiUiState.Idle) "✨ Summarize" else "Regenerate",
                                                fontSize = 12.sp
                                            )
                                        }
                                    }

                                    when (lectureAiSummaryState) {
                                        is AiUiState.Idle -> {
                                            Text(
                                                text = "Tap '✨ Summarize' to generate a complete step-by-step derivation breakdown and high-yield exam tips for ${lecture.title}.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        is AiUiState.Loading -> {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                modifier = Modifier.padding(vertical = 8.dp)
                                            ) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(20.dp),
                                                    strokeWidth = 2.dp,
                                                    color = IndigoPrimary
                                                )
                                                Text(
                                                    text = "Gemini AI is deriving formulas and exam tips...",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurface
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
                                                        text = "⚡ Streaming Live from Gemini AI...",
                                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                        color = CyanAccent
                                                    )
                                                    Text(
                                                        text = lectureAiSummaryState.text,
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
                                                        text = "✨ Complete AI Derivation & Exam Sheet",
                                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                        color = EmeraldSuccess
                                                    )
                                                    Text(
                                                        text = lectureAiSummaryState.text,
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
                                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Text(
                                                        text = "AI Summary Error: ${lectureAiSummaryState.message}",
                                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                                        color = RoseError
                                                    )
                                                    TextButton(onClick = onSummarizeWithAi) {
                                                        Text("Retry", color = RoseError, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        // Timestamps / Chapter Moments
                        items(lecture.timestamps) { stamp ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = CardDefaults.outlinedCardBorder(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSeek(stamp.timeSec) }
                                    .testTag("timestamp_item_${stamp.timeSec}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Text(
                                            text = stamp.timeLabel,
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = stamp.title,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Tap to jump in lecture",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.Filled.PlayArrow,
                                        contentDescription = "Jump",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }

                    2 -> {
                        // Related DPP
                        item {
                            if (lecture.dppId != null) {
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = CardDefaults.outlinedCardBorder(),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(18.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = AmberContainer
                                        ) {
                                            Text(
                                                text = "ATTACHED DAILY PRACTICE SHEET",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = Color(0xFF78350F),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }

                                        Text(
                                            text = "Practice questions strictly based on this lecture (${lecture.chapterTitle})",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        Text(
                                            text = "Solving DPPs right after the lecture increases memory retention by over 80%. Complete the questions and check your instant score.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        Button(
                                            onClick = { onStartDpp(lecture.dppId) },
                                            colors = ButtonDefaults.buttonColors(containerColor = AmberStreak),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("start_lecture_dpp_btn")
                                        ) {
                                            Icon(imageVector = Icons.Filled.AssignmentTurnedIn, contentDescription = null)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Start Solving Attached DPP", color = Color.White)
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    text = "No DPP attached to this introductory lecture.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    3 -> {
                        // AI Doubt Solver Tutor inside Lecture Player
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = CardDefaults.outlinedCardBorder(),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.AutoAwesome,
                                            contentDescription = "Gemini AI Tutor",
                                            tint = AmberStreak
                                        )
                                        Column {
                                            Text(
                                                text = "Ask Gemini AI Subject Tutor",
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "Instant step-by-step derivations & exam tips for ${lecture.subject.displayName}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    OutlinedTextField(
                                        value = doubtInput,
                                        onValueChange = { doubtInput = it },
                                        placeholder = { Text("Ask any doubt or derivation from ${lecture.title}...") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("doubt_input_field"),
                                        shape = RoundedCornerShape(12.dp),
                                        minLines = 3
                                    )

                                    Button(
                                        onClick = {
                                            if (doubtInput.isNotBlank()) {
                                                onPostDoubt(doubtInput)
                                                doubtInput = ""
                                            }
                                        },
                                        enabled = doubtAiState !is AiUiState.Loading,
                                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .align(Alignment.End)
                                            .testTag("submit_doubt_btn")
                                    ) {
                                        Icon(imageVector = Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Solve with Gemini AI")
                                    }

                                    when (doubtAiState) {
                                        is AiUiState.Idle -> {}
                                        is AiUiState.Loading -> {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                                Text(
                                                    text = "Gemini AI Tutor is preparing step-by-step derivation...",
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        }
                                        is AiUiState.Streaming -> {
                                            Surface(
                                                shape = RoundedCornerShape(12.dp),
                                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(12.dp),
                                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Text(
                                                        text = "⚡ Gemini AI Tutor (Streaming):",
                                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                        color = IndigoPrimary
                                                    )
                                                    Text(
                                                        text = doubtAiState.text,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                }
                                            }
                                        }
                                        is AiUiState.Success -> {
                                            Surface(
                                                shape = RoundedCornerShape(12.dp),
                                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(12.dp),
                                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Text(
                                                        text = "✨ Gemini AI Tutor Solution:",
                                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                        color = EmeraldSuccess
                                                    )
                                                    Text(
                                                        text = doubtAiState.text,
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
                                                    text = "AI Tutor Error: ${doubtAiState.message}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = RoseError,
                                                    modifier = Modifier.padding(12.dp)
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
    }
}
