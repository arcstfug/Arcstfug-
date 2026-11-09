package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchDetailScreen(
    course: CourseBatch,
    onBack: () -> Unit,
    onOpenLecture: (String) -> Unit,
    onStartDpp: (String) -> Unit,
    onEnrollToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val uriHandler = LocalUriHandler.current
    val subjects = course.subjects
    var selectedSubjectIndex by remember { mutableIntStateOf(0) }
    val currentSubject = subjects.getOrNull(selectedSubjectIndex)

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
                                if (!course.storageBucketUrl.isNullOrBlank()) {
                                    Text(
                                        text = "☁️ Cloud Storage Vault: Connected ✓",
                                        color = Color(0xFFBAE6FD),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
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

                // Chapters & Lectures / DPPs
                if (currentSubject != null && currentSubject.chapters.isNotEmpty()) {
                    items(currentSubject.chapters) { chapter ->
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
                                            text = "${chapter.lectures.size} Lectures • ${chapter.dpps.size} DPP Sheets",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                                // Chapter Lectures
                                Text(
                                    text = "Lectures",
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
                                        text = "Daily Practice Problems (DPP)",
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
                                text = "Lectures & DPPs for this subject are updating soon!",
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
