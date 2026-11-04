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
    var selectedBookFilter by remember { mutableStateOf("ALL") } // "ALL", "HCV", "CENGAGE"
    var selectedHcvChapter by remember { mutableStateOf("All Chapters") }
    var selectedCengageChapter by remember { mutableStateOf("All Chapters") }

    val hcvChapterOptions = remember {
        listOf(
            "All Chapters",
            "Ch 1: Intro to Physics",
            "Ch 2: Physics & Math",
            "Ch 3: Kinematics",
            "Ch 4: The Forces",
            "Ch 5: Newton's Laws",
            "Ch 6: Friction",
            "Ch 7: Circular Motion",
            "Ch 8: Work & Energy",
            "Ch 9: Centre of Mass",
            "Ch 10: Rotational Mechanics",
            "Ch 11: Gravitation",
            "Ch 12: SHM",
            "Ch 13: Fluid Mechanics",
            "Ch 14: Properties of Matter",
            "Ch 15: Waves on a String",
            "Ch 16: Sound Waves",
            "Ch 17: Light Waves",
            "Ch 18: Geometrical Optics",
            "Ch 19: Optical Instruments",
            "Ch 20: Dispersion & Photometry"
        )
    }

    val cengageChapterOptions = remember {
        listOf(
            "All Chapters",
            "Chapter 1: Number System",
            "Chapter 2: Complex Numbers",
            "Chapter 3: Progression & Series",
            "Chapter 4: Inequalities (Means)",
            "Chapter 5: Permutation & Combination",
            "Chapter 6: Binomial Theorem",
            "Chapter 7: Determinants",
            "Chapter 8: Matrices",
            "Chapter 9: Probability"
        )
    }

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
        val matchesBook = when (selectedBookFilter) {
            "ALL" -> true
            "HCV" -> dpp.sourceBook?.contains("H.C. Verma", ignoreCase = true) == true
            "CENGAGE" -> dpp.sourceBook?.contains("Cengage", ignoreCase = true) == true
            else -> true
        }
        val matchesChapter = when (selectedBookFilter) {
            "HCV" -> {
                if (selectedHcvChapter == "All Chapters") true
                else {
                    val chPrefix = selectedHcvChapter.substringBefore(":")
                    dpp.chapterTitle.contains(chPrefix, ignoreCase = true) ||
                    dpp.chapterTitle.contains(chPrefix.replace("Ch ", "Chapter "), ignoreCase = true)
                }
            }
            "CENGAGE" -> {
                if (selectedCengageChapter == "All Chapters") true
                else {
                    val chPrefix = selectedCengageChapter.substringBefore(":")
                    dpp.chapterTitle.contains(chPrefix, ignoreCase = true)
                }
            }
            else -> true
        }

        matchesSubject && matchesStatus && matchesBook && matchesChapter
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

        // Specialized Book Practice Series Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Book Filter Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "ALL" to "📚 All Sheets",
                        "HCV" to "⚡ HC Verma Physics",
                        "CENGAGE" to "📖 Cengage Algebra"
                    ).forEach { (bookKey, bookLabel) ->
                        val isSelected = selectedBookFilter == bookKey
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedBookFilter = bookKey
                                when (bookKey) {
                                    "HCV" -> selectedSubject = SubjectType.PHYSICS
                                    "CENGAGE" -> selectedSubject = SubjectType.MATHEMATICS
                                    else -> {}
                                }
                            },
                            label = { Text(bookLabel, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).testTag("filter_book_$bookKey")
                        )
                    }
                }

                // Concepts of Physics - H.C. Verma (Vol 1) Card
                if (selectedBookFilter == "ALL" || selectedBookFilter == "HCV") {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedBookFilter == "HCV") PhysicsColor.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                        ),
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("hc_verma_physics_hub_card")
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
                                        shape = RoundedCornerShape(8.dp),
                                        color = PhysicsColor.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "⚡ CONCEPTS OF PHYSICS",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = PhysicsColor,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                    Text(
                                        text = "Dr. H. C. Verma • Vol 1",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Switch(
                                    checked = selectedBookFilter == "HCV",
                                    onCheckedChange = { isChecked ->
                                        selectedBookFilter = if (isChecked) "HCV" else "ALL"
                                        if (isChecked) {
                                            selectedSubject = SubjectType.PHYSICS
                                        }
                                    },
                                    modifier = Modifier.testTag("toggle_hcv_filter")
                                )
                            }

                            Text(
                                text = "Distinct Chapter-wise DPPs (All 20 Chapters)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = "Authentic conceptual questions, Objective I & II, worked examples, and step-by-step physical derivations from H.C. Verma (Part 01).",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (selectedBookFilter == "HCV") {
                                Text(
                                    text = "Select Independent Chapter:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(vertical = 4.dp)
                                ) {
                                    items(hcvChapterOptions) { chOption ->
                                        val isSelected = selectedHcvChapter == chOption
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { selectedHcvChapter = chOption },
                                            label = { Text(chOption, fontSize = 12.sp) },
                                            shape = RoundedCornerShape(16.dp),
                                            modifier = Modifier.testTag("chip_hcv_${chOption.take(10).replace(" ", "_")}")
                                        )
                                    }
                                }
                            } else {
                                Button(
                                    onClick = {
                                        selectedBookFilter = "HCV"
                                        selectedSubject = SubjectType.PHYSICS
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PhysicsColor),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                    modifier = Modifier.testTag("btn_explore_hcv_chapters")
                                ) {
                                    Icon(Icons.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Open H.C. Verma 20-Chapter DPP Series", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Cengage Algebra (G. Tewani) Card
                if (selectedBookFilter == "ALL" || selectedBookFilter == "CENGAGE") {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedBookFilter == "CENGAGE") MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                        ),
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("cengage_algebra_hub_card")
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
                                        shape = RoundedCornerShape(8.dp),
                                        color = MathsColor.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "📖 CENGAGE ALGEBRA",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MathsColor,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                    Text(
                                        text = "G. Tewani Edition",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Switch(
                                    checked = selectedBookFilter == "CENGAGE",
                                    onCheckedChange = { isChecked ->
                                        selectedBookFilter = if (isChecked) "CENGAGE" else "ALL"
                                        if (isChecked) {
                                            selectedSubject = SubjectType.MATHEMATICS
                                        }
                                    },
                                    modifier = Modifier.testTag("toggle_cengage_filter")
                                )
                            }

                            Text(
                                text = "Chapter-wise Daily Practice Problems (9 Chapters)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = "Authentic objective problems, concept application exercises, step-by-step mathematical proofs & hints from Cengage Algebra.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (selectedBookFilter == "CENGAGE") {
                                Text(
                                    text = "Select Independent Chapter:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(vertical = 4.dp)
                                ) {
                                    items(cengageChapterOptions) { chOption ->
                                        val isSelected = selectedCengageChapter == chOption
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { selectedCengageChapter = chOption },
                                            label = { Text(chOption, fontSize = 12.sp) },
                                            shape = RoundedCornerShape(16.dp),
                                            modifier = Modifier.testTag("chip_cengage_${chOption.take(10).replace(" ", "_")}")
                                        )
                                    }
                                }
                            } else {
                                Button(
                                    onClick = {
                                        selectedBookFilter = "CENGAGE"
                                        selectedSubject = SubjectType.MATHEMATICS
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MathsColor),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                    modifier = Modifier.testTag("btn_explore_cengage_chapters")
                                ) {
                                    Icon(Icons.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Open Cengage 9-Chapter DPP Series", fontWeight = FontWeight.Bold)
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

            dpp.sourceBook?.let { bookSource ->
                val isHcv = bookSource.contains("H.C. Verma", ignoreCase = true)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isHcv) PhysicsColor.copy(alpha = 0.12f) else AmberContainer.copy(alpha = 0.7f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(if (isHcv) "⚡" else "📖", fontSize = 11.sp)
                        Text(
                            text = bookSource,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isHcv) PhysicsColor else Color(0xFF78350F)
                        )
                    }
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
