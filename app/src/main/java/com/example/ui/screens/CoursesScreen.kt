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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CourseBatch
import com.example.ui.theme.*

@Composable
fun CoursesScreen(
    courses: List<CourseBatch>,
    onOpenBatch: (String) -> Unit,
    onEnrollToggle: (String) -> Unit,
    onDonateAndSaveBatch: (String) -> Unit = {},
    isAutoSaveOnOpenEnabled: Boolean = true,
    onToggleAutoSaveOnOpen: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

    val categories = listOf(
        "All",
        "🌊 StudyOcean PW",
        "🔥 PW 2027 Batches",
        "🎁 Donated / Saved",
        "Enrolled",
        "HACKER",
        "JEE Main & Adv",
        "NEET-UG",
        "Class 10 Board"
    )

    val filteredCourses = courses.filter { course ->
        val matchesCategory = when (selectedCategory) {
            "All" -> true
            "🌊 StudyOcean PW" -> course.id.startsWith("c_pw_") || course.title.contains("StudyOcean", ignoreCase = true)
            "🔥 PW 2027 Batches" -> course.title.contains("2027", ignoreCase = true) || course.category.contains("2027", ignoreCase = true)
            "🎁 Donated / Saved" -> course.isDirectSaved || course.isDonatedBatch
            "Enrolled" -> course.isEnrolled
            else -> course.category.contains(selectedCategory, ignoreCase = true) ||
                    course.tag.contains(selectedCategory, ignoreCase = true)
        }
        val matchesSearch = course.title.contains(searchQuery, ignoreCase = true) ||
                course.description.contains(searchQuery, ignoreCase = true) ||
                course.instructorTeam.contains(searchQuery, ignoreCase = true) ||
                course.availablePlans.any { it.contains(searchQuery, ignoreCase = true) }
        matchesCategory && matchesSearch
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("courses_screen_list"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp)
    ) {
        // Search Input
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("course_search_input"),
                placeholder = { Text("Search batches, teachers, topics...") },
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

        // Category Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(end = 8.dp)
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("course_chip_${category.lowercase().replace(" ", "_")}")
                    )
                }
            }
        }

        // 🎁 Batch Donate & Auto-Save Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = EmeraldSuccess.copy(alpha = 0.10f)
                ),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("courses_batch_donate_banner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CardGiftcard,
                            contentDescription = "Batch Donate",
                            tint = EmeraldSuccess,
                            modifier = Modifier.size(22.dp)
                        )
                        Column {
                            Text(
                                text = "🎁 Batch Donate • Direct Save on Open",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isAutoSaveOnOpenEnabled) {
                                    "Koi bhi Premium Batch open hote hi direct save ho jayega ✓"
                                } else {
                                    "Tap '🎁 Batch Donate' on any batch to save directly"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = EmeraldSuccess
                            )
                        }
                    }
                    Switch(
                        checked = isAutoSaveOnOpenEnabled,
                        onCheckedChange = onToggleAutoSaveOnOpen,
                        modifier = Modifier.testTag("courses_autosave_switch")
                    )
                }
            }
        }

        // Batch Count Summary
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Explore Batches (${filteredCourses.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Live + Recorded",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Course List Items
        items(filteredCourses) { course ->
            CourseCardItem(
                course = course,
                onClick = { onOpenBatch(course.id) },
                onEnrollToggle = { onEnrollToggle(course.id) },
                onDonateAndSave = { onDonateAndSaveBatch(course.id) }
            )
        }
    }
}

@Composable
fun CourseCardItem(
    course: CourseBatch,
    onClick: () -> Unit,
    onEnrollToggle: () -> Unit,
    onDonateAndSave: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder(),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("course_card_${course.id}")
    ) {
        Column {
            // Header Banner
            if (course.bannerDrawableRes != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                ) {
                    Image(
                        painter = painterResource(id = course.bannerDrawableRes),
                        contentDescription = course.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    if (course.isEnrolled) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldSuccess,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "ENROLLED",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(course.bannerColorHex), Color(0xFF1E1B4B))
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.align(Alignment.BottomStart)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0x35FFFFFF)
                            ) {
                                Text(
                                    text = course.tag,
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            if (course.isEnrolled) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EmeraldSuccess
                                ) {
                                    Text(
                                        text = "ENROLLED",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = course.title,
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            maxLines = 2
                        )
                    }
                }
            }

            // Body Details
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (course.availablePlans.isNotEmpty() || !course.featureBadge.isNullOrBlank()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (course.availablePlans.isNotEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = AmberContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("👑", fontSize = 13.sp)
                                    Text(
                                        text = "Multiple plans inside: ${course.availablePlans.joinToString(", ")}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                        color = Color(0xFF78350F)
                                    )
                                }
                            }
                        }
                        if (!course.featureBadge.isNullOrBlank()) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color(0xFFFDE68A),
                                modifier = Modifier.border(1.5.dp, Color(0xFF78350F), RoundedCornerShape(20.dp))
                            ) {
                                Text(
                                    text = course.featureBadge,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                    color = Color(0xFF1E1B4B),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "⭐ ${course.rating}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                        Text(text = "• ${course.enrolledCount}", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = course.originalPrice,
                            style = MaterialTheme.typography.bodySmall.copy(
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = course.price,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = IndigoPrimary
                        )
                    }
                }

                Text(
                    text = course.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.People,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = course.instructorTeam,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                }

                if (course.isEnrolled) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Syllabus Completed",
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
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = IndigoPrimary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onClick() },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("course_explore_btn_${course.id}"),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        Text("Open & Save", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onDonateAndSave,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (course.isDirectSaved) EmeraldSuccess else Color(0xFFD97706)
                        ),
                        modifier = Modifier
                            .weight(1.15f)
                            .testTag("course_donate_btn_${course.id}"),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = if (course.isDirectSaved) Icons.Filled.CloudDone else Icons.Filled.CardGiftcard,
                            contentDescription = "Batch Donate",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (course.isDirectSaved) "🎁 Saved ✓" else "🎁 Batch Donate",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Button(
                        onClick = onEnrollToggle,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (course.isEnrolled) MaterialTheme.colorScheme.surfaceVariant else IndigoPrimary
                        ),
                        modifier = Modifier
                            .weight(0.95f)
                            .testTag("course_enroll_btn_${course.id}"),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = if (course.isEnrolled) "Enrolled ✓" else "Enroll",
                            color = if (course.isEnrolled) MaterialTheme.colorScheme.onSurfaceVariant else Color.White,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
