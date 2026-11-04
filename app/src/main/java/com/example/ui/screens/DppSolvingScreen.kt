package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Dpp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DppSolvingScreen(
    dpp: Dpp,
    currentQuestionIndex: Int,
    userAnswers: Map<Int, Int>,
    reviewFlags: Set<Int>,
    timerSec: Int,
    onSelectOption: (questionIndex: Int, optionIndex: Int) -> Unit,
    onToggleReviewFlag: (questionIndex: Int) -> Unit,
    onQuestionIndexChange: (Int) -> Unit,
    onSubmit: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSubmitDialog by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }
    var showHint by remember { mutableStateOf(false) }

    BackHandler {
        showExitDialog = true
    }

    val currentQuestion = dpp.questions.getOrNull(currentQuestionIndex)
    val totalQuestions = dpp.questions.size

    val minutes = timerSec / 60
    val seconds = timerSec % 60
    val timerText = String.format("%02d:%02d", minutes, seconds)
    val isTimerLow = timerSec < 120

    Scaffold(
        modifier = modifier.testTag("dpp_solving_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = dpp.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1
                        )
                        Text(
                            text = "${dpp.subject.displayName} • ${dpp.chapterTitle}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { showExitDialog = true },
                        modifier = Modifier.testTag("dpp_exit_btn")
                    ) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Exit DPP")
                    }
                },
                actions = {
                    // Timer Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isTimerLow) RoseContainer else IndigoContainer,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("dpp_timer_badge")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = "⏱", fontSize = 14.sp)
                            Text(
                                text = timerText,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isTimerLow) RoseError else IndigoDark
                            )
                        }
                    }

                    Button(
                        onClick = { showSubmitDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("dpp_top_submit_btn")
                    ) {
                        Text("Submit", color = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Review flag toggle
                    OutlinedButton(
                        onClick = { onToggleReviewFlag(currentQuestionIndex) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("dpp_flag_review_btn")
                    ) {
                        Icon(
                            imageVector = if (reviewFlags.contains(currentQuestionIndex)) Icons.Filled.Flag else Icons.Filled.OutlinedFlag,
                            contentDescription = null,
                            tint = if (reviewFlags.contains(currentQuestionIndex)) AmberStreak else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (reviewFlags.contains(currentQuestionIndex)) "Marked" else "Mark",
                            fontSize = 12.sp
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Previous button
                        OutlinedButton(
                            onClick = {
                                if (currentQuestionIndex > 0) onQuestionIndexChange(currentQuestionIndex - 1)
                            },
                            enabled = currentQuestionIndex > 0,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("dpp_prev_btn")
                        ) {
                            Text("Prev")
                        }

                        // Next or Final Submit
                        if (currentQuestionIndex < totalQuestions - 1) {
                            Button(
                                onClick = { onQuestionIndexChange(currentQuestionIndex + 1) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                modifier = Modifier.testTag("dpp_next_btn")
                            ) {
                                Text("Next")
                            }
                        } else {
                            Button(
                                onClick = { showSubmitDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                                modifier = Modifier.testTag("dpp_finish_btn")
                            ) {
                                Text("Finish & Review")
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Question Palette Tabs Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .padding(vertical = 10.dp, horizontal = 16.dp)
                    .testTag("dpp_palette_row"),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(dpp.questions) { index, _ ->
                    val isCurrent = index == currentQuestionIndex
                    val isAnswered = userAnswers.containsKey(index)
                    val isFlagged = reviewFlags.contains(index)

                    val bgColor = when {
                        isCurrent -> IndigoPrimary
                        isFlagged -> AmberStreak
                        isAnswered -> EmeraldSuccess
                        else -> MaterialTheme.colorScheme.surface
                    }

                    val textColor = when {
                        isCurrent || isFlagged || isAnswered -> Color.White
                        else -> MaterialTheme.colorScheme.onSurface
                    }

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(bgColor)
                            .border(
                                width = if (isCurrent) 2.dp else 1.dp,
                                color = if (isCurrent) IndigoLight else MaterialTheme.colorScheme.outline,
                                shape = CircleShape
                            )
                            .clickable { onQuestionIndexChange(index) }
                            .testTag("dpp_palette_item_$index"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = textColor
                        )
                    }
                }
            }

            // Question & Options Area
            if (currentQuestion != null) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "Question ${currentQuestionIndex + 1} of $totalQuestions",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = currentQuestion.topic,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = CardDefaults.outlinedCardBorder(),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = currentQuestion.questionText,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    lineHeight = 24.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(18.dp)
                            )
                        }
                    }

                    // Options List
                    itemsIndexed(currentQuestion.options) { optionIdx, optionText ->
                        val isSelected = userAnswers[currentQuestionIndex] == optionIdx
                        val optionLabel = ('A'.code + optionIdx).toChar()

                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.surface
                            ),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = if (isSelected) androidx.compose.ui.graphics.SolidColor(IndigoPrimary) else CardDefaults.outlinedCardBorder().brush
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectOption(currentQuestionIndex, optionIdx) }
                                .testTag("option_${currentQuestionIndex}_$optionIdx")
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
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$optionLabel",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Text(
                                    text = optionText,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // Hint Accordion
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showHint = !showHint }
                                .testTag("hint_accordion")
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text("💡", fontSize = 16.sp)
                                        Text(
                                            text = if (showHint) "Hide Problem Hint" else "Need a Hint?",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Icon(
                                        imageVector = if (showHint) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                        contentDescription = null
                                    )
                                }

                                if (showHint) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = currentQuestion.hint,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Submit Confirmation Dialog
    if (showSubmitDialog) {
        val answeredCount = userAnswers.size
        val unansweredCount = totalQuestions - answeredCount

        AlertDialog(
            onDismissRequest = { showSubmitDialog = false },
            title = { Text("Submit DPP Test?") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Are you ready to submit your answers?")
                    Text("• Answered: $answeredCount")
                    Text("• Unanswered: $unansweredCount")
                    Text("• Time Left: $timerText")
                    Text("Once submitted, complete step-by-step solutions and scoring will be unlocked.")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSubmitDialog = false
                        onSubmit()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                    modifier = Modifier.testTag("confirm_submit_btn")
                ) {
                    Text("Yes, Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitDialog = false }) {
                    Text("Keep Solving")
                }
            }
        )
    }

    // Exit Confirmation Dialog
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Leave DPP Practice?") },
            text = {
                Text("Your current test attempt is still running. If you leave now, your answers will not be recorded.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExitDialog = false
                        onClose()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoseError)
                ) {
                    Text("Exit DPP")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Continue Solving")
                }
            }
        )
    }
}
