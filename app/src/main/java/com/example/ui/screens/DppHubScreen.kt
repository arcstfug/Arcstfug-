package com.example.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AiUiState

@Composable
fun DppHubScreen(
    dpps: List<Dpp>,
    userProfile: UserProfile,
    dppAiState: AiUiState,
    onStartDpp: (String) -> Unit,
    onGenerateAiDpp: (SubjectType, String, String, Boolean) -> Unit,
    onDismissAiState: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSubject by remember { mutableStateOf<SubjectType?>(null) }
    var selectedStatus by remember { mutableStateOf("ALL") } // "ALL", "PENDING", "COMPLETED"

    var showAiGeneratorDialog by remember { mutableStateOf(false) }
    var aiTopicInput by remember { mutableStateOf("") }
    var aiSelectedSubject by remember { mutableStateOf(SubjectType.PHYSICS) }
    var aiDifficulty by remember { mutableStateOf("JEE Main / NEET") }

    val filteredDpps = dpps.filter { dpp ->
        val matchesSubject = selectedSubject == null || dpp.subject == selectedSubject
        val matchesStatus = when (selectedStatus) {
            "ALL" -> true
            "PENDING" -> dpp.status == DppStatus.PENDING
            "COMPLETED" -> dpp.status == DppStatus.COMPLETED
            else -> true
        }
        matchesSubject && matchesStatus
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("dpp_hub_screen_list"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp)
    ) {
        // Stats Summary Banner
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dpp_stats_card")
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Daily Practice Problems (DPP)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Consistent daily problem solving unlocks top ranks",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text("🎯", fontSize = 28.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DppStatBox(
                            title = "Solved",
                            value = "${userProfile.totalDppsSolved}",
                            color = IndigoPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        DppStatBox(
                            title = "Accuracy",
                            value = "${userProfile.overallAccuracy}%",
                            color = EmeraldSuccess,
                            modifier = Modifier.weight(1f)
                        )
                        DppStatBox(
                            title = "Earned XP",
                            value = "${userProfile.xp}",
                            color = AmberStreak,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Gemini AI Custom DPP Generator Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                ),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_dpp_generator_card")
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
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = "Gemini AI DPP",
                                tint = AmberStreak
                            )
                            Column {
                                Text(
                                    text = "Gemini AI Custom DPP Generator",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Create instant timed practice sets on any weak topic with step-by-step derivations",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Button(
                            onClick = { showAiGeneratorDialog = true },
                            enabled = dppAiState !is AiUiState.Loading,
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("open_ai_dpp_dialog_btn")
                        ) {
                            Text("✨ Generate")
                        }
                    }

                    when (dppAiState) {
                        is AiUiState.Idle -> {}
                        is AiUiState.Loading -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = IndigoPrimary
                                )
                                Text(
                                    text = "Gemini AI is crafting custom MCQs, hints & step-by-step derivations...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        is AiUiState.Streaming -> {}
                        is AiUiState.Success -> {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = EmeraldContainer,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "✨ ${dppAiState.text}",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = EmeraldSuccess,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(onClick = onDismissAiState, modifier = Modifier.size(24.dp)) {
                                        Icon(Icons.Filled.Close, contentDescription = "Dismiss", tint = EmeraldSuccess)
                                    }
                                }
                            }
                        }
                        is AiUiState.Error -> {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = RoseContainer,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "AI DPP Error: ${dppAiState.message}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = RoseError
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        TextButton(onClick = { showAiGeneratorDialog = true }) {
                                            Text("Try Again", color = RoseError, fontWeight = FontWeight.Bold)
                                        }
                                        TextButton(onClick = onDismissAiState) {
                                            Text("Dismiss", color = RoseError)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Subject Filter Row
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(end = 8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedSubject == null,
                        onClick = { selectedSubject = null },
                        label = { Text("All Subjects") },
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("dpp_chip_all_subjects")
                    )
                }
                items(SubjectType.values()) { subj ->
                    val isSelected = selectedSubject == subj
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedSubject = subj },
                        label = { Text(subj.displayName) },
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("dpp_chip_${subj.name.lowercase()}")
                    )
                }
            }
        }

        // Status Segmented Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("ALL" to "All Sheets", "PENDING" to "Pending", "COMPLETED" to "Completed").forEach { (key, label) ->
                    val isSelected = selectedStatus == key
                    OutlinedButton(
                        onClick = { selectedStatus = key },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                        ),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }
        }

        // DPP List
        if (filteredDpps.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No practice sheets found for this selection.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(filteredDpps) { dpp ->
                DppCardItem(
                    dpp = dpp,
                    onClick = { onStartDpp(dpp.id) }
                )
            }
        }
    }

    // AI Custom DPP Generator Dialog
    if (showAiGeneratorDialog) {
        AlertDialog(
            onDismissRequest = { showAiGeneratorDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = AmberStreak)
                    Text("Generate Custom AI DPP")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Select Subject:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SubjectType.values().forEach { subj ->
                            FilterChip(
                                selected = aiSelectedSubject == subj,
                                onClick = { aiSelectedSubject = subj },
                                label = { Text(subj.displayName.take(4), fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Text(
                        text = "Difficulty Level:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Board / Foundation", "JEE Main / NEET", "JEE Advanced").forEach { diff ->
                            FilterChip(
                                selected = aiDifficulty == diff,
                                onClick = { aiDifficulty = diff },
                                label = { Text(diff.take(10), fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = aiTopicInput,
                        onValueChange = { aiTopicInput = it },
                        label = { Text("Chapter or Topic") },
                        placeholder = { Text("e.g. Rotational Motion, Electrochemistry, Optics") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_dpp_topic_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAiGeneratorDialog = false
                        onGenerateAiDpp(aiSelectedSubject, aiTopicInput, aiDifficulty, true)
                        aiTopicInput = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                    modifier = Modifier.testTag("confirm_generate_ai_dpp_btn")
                ) {
                    Text("Generate & Solve")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAiGeneratorDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DppStatBox(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.12f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DppCardItem(
    dpp: Dpp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder(),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("dpp_item_card_${dpp.id}")
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
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (dpp.subject) {
                            SubjectType.PHYSICS -> PhysicsColor.copy(alpha = 0.15f)
                            SubjectType.CHEMISTRY -> ChemistryColor.copy(alpha = 0.15f)
                            SubjectType.MATHEMATICS -> MathsColor.copy(alpha = 0.15f)
                            SubjectType.BIOLOGY -> BiologyColor.copy(alpha = 0.15f)
                        }
                    ) {
                        Text(
                            text = dpp.subject.displayName,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = when (dpp.subject) {
                                SubjectType.PHYSICS -> PhysicsColor
                                SubjectType.CHEMISTRY -> ChemistryColor
                                SubjectType.MATHEMATICS -> MathsColor
                                SubjectType.BIOLOGY -> BiologyColor
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Text(
                        text = dpp.chapterTitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (dpp.status == DppStatus.COMPLETED) EmeraldContainer else AmberContainer
                ) {
                    Text(
                        text = if (dpp.status == DppStatus.COMPLETED) "Score: ${dpp.score}/${dpp.questions.size}" else "Pending",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (dpp.status == DppStatus.COMPLETED) EmeraldSuccess else Color(0xFF78350F),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Text(
                text = dpp.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "⏱ ${dpp.timeLimitMinutes} mins",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(text = "•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "${dpp.questions.size} Questions",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (dpp.status == DppStatus.COMPLETED) EmeraldSuccess else IndigoPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("dpp_action_btn_${dpp.id}")
                ) {
                    Text(
                        text = if (dpp.status == DppStatus.COMPLETED) "Review Solutions" else "Start Practice",
                        color = Color.White
                    )
                }
            }
        }
    }
}
