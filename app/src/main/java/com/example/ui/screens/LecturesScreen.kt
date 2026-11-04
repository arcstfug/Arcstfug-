package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ExternalStudyLinks
import com.example.model.Lecture
import com.example.model.SubjectType
import com.example.ui.theme.*

@Composable
fun LecturesScreen(
    lectures: List<Lecture>,
    onOpenLecture: (String) -> Unit,
    onBookmarkToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf<SubjectType?>(null) }
    var filterTab by remember { mutableStateOf("All") } // "All", "In Progress", "Completed", "Saved"

    val filteredLectures = lectures.filter { lec ->
        val matchesSubject = selectedSubject == null || lec.subject == selectedSubject
        val matchesStatus = when (filterTab) {
            "All" -> true
            "In Progress" -> lec.progressSec > 0 && !lec.isCompleted
            "Completed" -> lec.isCompleted
            "Saved" -> lec.isBookmarked
            else -> true
        }
        val matchesSearch = lec.title.contains(searchQuery, ignoreCase = true) ||
                lec.chapterTitle.contains(searchQuery, ignoreCase = true) ||
                lec.instructor.contains(searchQuery, ignoreCase = true)

        matchesSubject && matchesStatus && matchesSearch
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("lectures_screen_list"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp)
    ) {
        // Search Input
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("lecture_search_input"),
                placeholder = { Text("Search by topic, formula, faculty...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Search, contentDescription = "Search")
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Filled.Close, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )
        }

        // Physics Google Drive Cloud Folder Banner
        if (selectedSubject == null || selectedSubject == SubjectType.PHYSICS) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = PhysicsColor.copy(alpha = 0.1f)
                    ),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { uriHandler.openUri(ExternalStudyLinks.PHYSICS_DRIVE_URL) }
                        .testTag("lectures_physics_drive_card")
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
                                .size(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(PhysicsColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CloudDownload,
                                contentDescription = "Physics Google Drive",
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
                                text = "Access full Physics Lectures, Notes & Modules on Google Drive",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = { uriHandler.openUri(ExternalStudyLinks.PHYSICS_DRIVE_URL) },
                            colors = ButtonDefaults.buttonColors(containerColor = PhysicsColor),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("lectures_open_physics_drive_btn")
                        ) {
                            Text("Open Drive", fontSize = 11.sp, color = Color.White)
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
                        modifier = Modifier.testTag("subject_chip_all")
                    )
                }
                items(SubjectType.values()) { subj ->
                    val isSelected = selectedSubject == subj
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedSubject = subj },
                        label = { Text(subj.displayName) },
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("subject_chip_${subj.name.lowercase()}")
                    )
                }
            }
        }

        // Status Tabs Row
        item {
            TabRow(
                selectedTabIndex = listOf("All", "In Progress", "Completed", "Saved").indexOf(filterTab),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = IndigoPrimary,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                listOf("All", "In Progress", "Completed", "Saved").forEach { tab ->
                    Tab(
                        selected = filterTab == tab,
                        onClick = { filterTab = tab },
                        text = {
                            Text(
                                text = tab,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (filterTab == tab) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    )
                }
            }
        }

        // Count Summary
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Video Lectures (${filteredLectures.size})",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "4K Ultra-HD Classes",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Lecture List
        if (filteredLectures.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.VideoLibrary,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "No lectures match your filter",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(filteredLectures) { lec ->
                LectureCardItem(
                    lecture = lec,
                    onOpen = { onOpenLecture(lec.id) },
                    onBookmarkToggle = { onBookmarkToggle(lec.id) }
                )
            }
        }
    }
}

@Composable
fun LectureCardItem(
    lecture: Lecture,
    onOpen: () -> Unit,
    onBookmarkToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder(),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onOpen() }
            .testTag("lecture_item_card_${lecture.id}")
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
                        color = when (lecture.subject) {
                            SubjectType.PHYSICS -> PhysicsColor.copy(alpha = 0.15f)
                            SubjectType.CHEMISTRY -> ChemistryColor.copy(alpha = 0.15f)
                            SubjectType.MATHEMATICS -> MathsColor.copy(alpha = 0.15f)
                            SubjectType.BIOLOGY -> BiologyColor.copy(alpha = 0.15f)
                        }
                    ) {
                        Text(
                            text = lecture.subject.displayName,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = when (lecture.subject) {
                                SubjectType.PHYSICS -> PhysicsColor
                                SubjectType.CHEMISTRY -> ChemistryColor
                                SubjectType.MATHEMATICS -> MathsColor
                                SubjectType.BIOLOGY -> BiologyColor
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Text(
                        text = lecture.chapterTitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }

                IconButton(
                    onClick = onBookmarkToggle,
                    modifier = Modifier.size(32.dp).testTag("bookmark_btn_${lecture.id}")
                ) {
                    Icon(
                        imageVector = if (lecture.isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (lecture.isBookmarked) AmberStreak else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = lecture.title,
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
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = lecture.instructor,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(text = "•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "⏱ ${lecture.durationMinutes} mins",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (lecture.isCompleted) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = EmeraldContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = EmeraldSuccess,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Completed",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = EmeraldSuccess
                            )
                        }
                    }
                } else {
                    FilledTonalButton(
                        onClick = onOpen,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(if (lecture.progressSec > 0) "Resume" else "Watch")
                    }
                }
            }

            if (lecture.progressSec > 0 && !lecture.isCompleted) {
                val fraction = (lecture.progressSec.toFloat() / (lecture.durationMinutes * 60)).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { fraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = CyanAccent,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}
