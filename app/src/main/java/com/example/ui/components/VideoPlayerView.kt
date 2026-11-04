package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Lecture
import com.example.ui.theme.*

@Composable
fun VideoPlayerView(
    lecture: Lecture,
    isPlaying: Boolean,
    progressSec: Int,
    playbackSpeed: Float,
    videoQuality: String,
    onTogglePlayPause: () -> Unit,
    onSeek: (Int) -> Unit,
    onSkipBackward: () -> Unit,
    onSkipForward: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onQualityChange: (String) -> Unit,
    onBookmarkToggle: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showControls by remember { mutableStateOf(true) }
    var showSpeedMenu by remember { mutableStateOf(false) }
    var showQualityMenu by remember { mutableStateOf(false) }

    val totalSec = lecture.durationMinutes * 60
    val progressFraction = if (totalSec > 0) progressSec.toFloat() / totalSec else 0f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0A1424))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                showControls = !showControls
            }
            .testTag("video_player_container")
    ) {
        // Digital Chalkboard / Smart Lecture Simulation Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Background subtle grid
            val gridStep = 40f
            var x = 0f
            while (x < w) {
                drawLine(
                    color = Color(0x1814B8A6),
                    start = Offset(x, 0f),
                    end = Offset(x, h),
                    strokeWidth = 1f
                )
                x += gridStep
            }
            var y = 0f
            while (y < h) {
                drawLine(
                    color = Color(0x1814B8A6),
                    start = Offset(0f, y),
                    end = Offset(w, y),
                    strokeWidth = 1f
                )
                y += gridStep
            }

            // Simulated wave / spectrum activity when playing
            if (isPlaying) {
                val waveCount = 20
                val barW = w / (waveCount * 2)
                for (i in 0 until waveCount) {
                    val barH = (Math.sin((progressSec * 2 + i).toDouble()) * 24 + 32).toFloat()
                    drawRect(
                        brush = Brush.verticalGradient(
                            listOf(Color(0x8014B8A6), Color(0x25D97706))
                        ),
                        topLeft = Offset(w - (i * barW * 1.8f) - 40f, h - barH - 40f),
                        size = androidx.compose.ui.geometry.Size(barW, barH)
                    )
                }
            }
        }

        // Official ARCSTFUG Square Logo Watermark Overlay in the Video Player
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 38.dp)
                .alpha(0.88f)
                .testTag("video_player_logo_watermark"),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.arcstfug_logo),
                contentDescription = "ARCSTFUG Watermark Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White)
                    .border(1.dp, AmberStreak.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
            )
            Text(
                text = "ARCSTFUG",
                color = Color.White.copy(alpha = 0.85f),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.8.sp
                )
            )
        }

        // Live Subject Watermark & Lecture Title overlay
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 24.dp, top = 20.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0x40000000)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isPlaying) EmeraldSuccess else RoseError)
                    )
                    Text(
                        text = if (isPlaying) "SMART LECTURE LIVE" else "PAUSED",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = lecture.chapterTitle,
                color = CyanAccent,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
                text = "Faculty: ${lecture.instructor}",
                color = Color(0xFFCBD5E1),
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Interactive Overlay Controls
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x80000000))
                    .padding(12.dp)
            ) {
                // Top Action Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("video_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = lecture.title,
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    )

                    // Bookmark Button
                    IconButton(
                        onClick = onBookmarkToggle,
                        modifier = Modifier.testTag("video_bookmark_button")
                    ) {
                        Icon(
                            imageVector = if (lecture.isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (lecture.isBookmarked) AmberStreak else Color.White
                        )
                    }

                    // Speed Selector Menu
                    Box {
                        TextButton(
                            onClick = { showSpeedMenu = true },
                            modifier = Modifier.testTag("speed_selector_button")
                        ) {
                            Text(
                                text = "${playbackSpeed}x",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        DropdownMenu(
                            expanded = showSpeedMenu,
                            onDismissRequest = { showSpeedMenu = false }
                        ) {
                            listOf(0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                                DropdownMenuItem(
                                    text = { Text("${speed}x") },
                                    onClick = {
                                        onSpeedChange(speed)
                                        showSpeedMenu = false
                                    }
                                )
                            }
                        }
                    }

                    // Quality Selector Menu
                    Box {
                        IconButton(
                            onClick = { showQualityMenu = true },
                            modifier = Modifier.testTag("quality_selector_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Quality",
                                tint = Color.White
                            )
                        }
                        DropdownMenu(
                            expanded = showQualityMenu,
                            onDismissRequest = { showQualityMenu = false }
                        ) {
                            listOf("1080p Full HD", "720p HD", "480p", "360p Data Saver").forEach { q ->
                                DropdownMenuItem(
                                    text = { Text(q) },
                                    onClick = {
                                        onQualityChange(q)
                                        showQualityMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Center Action Controls (Rewind 10s, Play/Pause, Forward 10s)
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(28.dp)
                ) {
                    IconButton(
                        onClick = onSkipBackward,
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("skip_backward_10s")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Replay10,
                            contentDescription = "Rewind 10 seconds",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = IndigoPrimary,
                        modifier = Modifier
                            .size(56.dp)
                            .clickable { onTogglePlayPause() }
                            .testTag("play_pause_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onSkipForward,
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("skip_forward_10s")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Forward10,
                            contentDescription = "Forward 10 seconds",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                // Bottom Timeline & Scrubber Bar
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatTime(progressSec),
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            text = "${videoQuality} • ${formatTime(totalSec)}",
                            color = Color(0xFFCBD5E1),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    Slider(
                        value = progressFraction,
                        onValueChange = { frac ->
                            onSeek((frac * totalSec).toInt())
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = CyanAccent,
                            activeTrackColor = CyanAccent,
                            inactiveTrackColor = Color(0x60FFFFFF)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .testTag("video_scrubber_slider")
                    )
                }
            }
        }
    }
}

private fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return String.format("%02d:%02d", m, s)
}
