package com.example.ui.components

import android.annotation.SuppressLint
import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.MediaController
import android.widget.VideoView
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.R
import com.example.data.PwCourseApiService
import com.example.model.Lecture
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@SuppressLint("SetJavaScriptEnabled")
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
    // Player mode: "NATIVE_VIDEO" (Real PW MP4 VideoView), "WEB_VIDEO" (HTML5 Video Stream), or "SMART_BOARD" (Interactive Slides)
    var playerMode by remember(lecture.id) { mutableStateOf("NATIVE_VIDEO") }
    var isVideoBuffering by remember(lecture.id, playerMode) { mutableStateOf(true) }
    var showControls by remember { mutableStateOf(false) }
    var showSpeedMenu by remember { mutableStateOf(false) }
    var showQualityMenu by remember { mutableStateOf(false) }
    var manualSlideIndex by remember(lecture.id) { mutableStateOf<Int?>(null) }

    val directMp4Url = remember(lecture.id, lecture.videoUrl) {
        PwCourseApiService.resolvePwDirectMp4Url(lecture)
    }

    LaunchedEffect(showControls, isPlaying) {
        if (showControls && isPlaying && !showSpeedMenu && !showQualityMenu) {
            delay(3500)
            showControls = false
        }
    }

    val totalSec = (lecture.durationMinutes * 60).coerceAtLeast(60)
    val progressFraction = (progressSec.toFloat() / totalSec).coerceIn(0f, 1f)

    val slides = remember(lecture.id, lecture.notesSummary) {
        if (lecture.notesSummary.isNotEmpty()) {
            lecture.notesSummary
        } else {
            listOf(
                "Core concepts, formula derivations & numerical problem solving for ${lecture.title}.",
                "High-yield exam shortcuts and Previous Year Questions (PYQs) taught by ${lecture.instructor}."
            )
        }
    }

    val autoSlideIdx = remember(progressSec, slides.size) {
        if (slides.size <= 1) 0 else ((progressSec / 12) % slides.size)
    }
    val activeSlideIndex = (manualSlideIndex ?: autoSlideIdx).coerceIn(0, (slides.size - 1).coerceAtLeast(0))
    val activeSlideText = slides.getOrElse(activeSlideIndex) { lecture.title }

    val currentTimestampTitle = remember(progressSec, lecture.timestamps, activeSlideIndex) {
        lecture.timestamps.lastOrNull { it.timeSec <= progressSec }?.title
            ?: lecture.timestamps.getOrNull(activeSlideIndex)?.title
            ?: lecture.chapterTitle
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Top Header & Player Mode Switcher Bar (Always Accessible)
        Surface(
            color = Color(0xFF0F172A),
            shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("video_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = lecture.title,
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "👨‍🏫 ${lecture.instructor}",
                            color = CyanAccent,
                            fontSize = 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    IconButton(
                        onClick = onBookmarkToggle,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("video_bookmark_button")
                    ) {
                        Icon(
                            imageVector = if (lecture.isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (lecture.isBookmarked) AmberStreak else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Mode Switcher Pills: Real PW Video vs Web Stream vs Smart-Board
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val modes = listOf(
                        "NATIVE_VIDEO" to "🎬 PW Video Lecture",
                        "WEB_VIDEO" to "📺 Stream Player 2",
                        "SMART_BOARD" to "📝 Smart-Board Notes"
                    )
                    modes.forEach { (modeKey, label) ->
                        val selected = playerMode == modeKey
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selected) EmeraldSuccess else Color(0xFF1E293B),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { playerMode = modeKey }
                                .testTag("player_mode_${modeKey.lowercase()}")
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 5.dp, horizontal = 4.dp)
                            ) {
                                Text(
                                    text = label,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // Main 16:9 Video Frame
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF07191E), Color(0xFF0F172A), Color(0xFF1E1B4B))
                    )
                )
                .border(1.5.dp, CyanAccent.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
                .testTag("video_player_container")
        ) {
            when (playerMode) {
                "NATIVE_VIDEO" -> {
                    var videoViewRef by remember { mutableStateOf<VideoView?>(null) }

                    AndroidView(
                        factory = { ctx ->
                            FrameLayout(ctx).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                setBackgroundColor(android.graphics.Color.BLACK)

                                val vv = VideoView(ctx).apply {
                                    layoutParams = FrameLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        android.view.Gravity.CENTER
                                    )
                                    val mediaController = MediaController(ctx)
                                    mediaController.setAnchorView(this)
                                    setMediaController(mediaController)

                                    setOnPreparedListener { mp ->
                                        isVideoBuffering = false
                                        mp.isLooping = false
                                        if (isPlaying) {
                                            start()
                                        }
                                    }
                                    setOnInfoListener { _, what, _ ->
                                        if (what == android.media.MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START ||
                                            what == android.media.MediaPlayer.MEDIA_INFO_BUFFERING_END
                                        ) {
                                            isVideoBuffering = false
                                        }
                                        false
                                    }
                                    setOnErrorListener { _, _, _ ->
                                        isVideoBuffering = false
                                        playerMode = "WEB_VIDEO"
                                        true
                                    }
                                    setVideoURI(Uri.parse(directMp4Url))
                                    requestFocus()
                                }
                                videoViewRef = vv
                                addView(vv)
                            }
                        },
                        update = {
                            val vv = videoViewRef
                            if (vv != null && !isVideoBuffering) {
                                if (isPlaying && !vv.isPlaying) {
                                    runCatching { vv.start() }
                                } else if (!isPlaying && vv.isPlaying) {
                                    runCatching { vv.pause() }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("pw_native_video_view")
                    )

                    if (isVideoBuffering) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xCC0A1424)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.padding(16.dp)
                            ) {
                                CircularProgressIndicator(
                                    color = EmeraldSuccess,
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(34.dp)
                                )
                                Text(
                                    text = "⚡ Connecting PW HD Video Lecture Stream...",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "${lecture.title} • ${lecture.instructor}",
                                    color = CyanAccent,
                                    fontSize = 10.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    // Official ARCSTFUG Watermark Badge in corner
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                            .alpha(0.85f)
                            .testTag("video_player_logo_watermark"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.arcstfug_logo),
                            contentDescription = "ARCSTFUG Watermark Logo",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(20.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.White)
                        )
                        Text(
                            text = "PW LIVE",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                "WEB_VIDEO" -> {
                    AndroidView(
                        factory = { ctx ->
                            WebView(ctx).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                setBackgroundColor(android.graphics.Color.BLACK)
                                settings.apply {
                                    javaScriptEnabled = true
                                    domStorageEnabled = true
                                    mediaPlaybackRequiresUserGesture = false
                                    loadWithOverviewMode = true
                                    useWideViewPort = true
                                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                }
                                webChromeClient = WebChromeClient()
                                webViewClient = object : WebViewClient() {
                                    override fun onPageFinished(view: WebView?, url: String?) {
                                        isVideoBuffering = false
                                    }
                                }
                                val htmlContent = """
                                    <!DOCTYPE html>
                                    <html>
                                    <head>
                                        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                                        <style>
                                            * { margin: 0; padding: 0; box-sizing: border-box; background: #000; }
                                            html, body { width: 100%; height: 100%; overflow: hidden; display: flex; align-items: center; justify-content: center; }
                                            video { width: 100%; height: 100%; object-fit: contain; background: #000; }
                                        </style>
                                    </head>
                                    <body>
                                        <video controls autoplay playsinline preload="auto">
                                            <source src="$directMp4Url" type="video/mp4">
                                        </video>
                                    </body>
                                    </html>
                                """.trimIndent()
                                loadDataWithBaseURL("https://archive.org", htmlContent, "text/html", "UTF-8", null)
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("pw_web_video_view")
                    )

                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                            .alpha(0.85f)
                            .testTag("video_player_logo_watermark"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.arcstfug_logo),
                            contentDescription = "ARCSTFUG Watermark Logo",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(20.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.White)
                        )
                        Text(
                            text = "PW STREAM",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                else -> {
                    // SMART_BOARD Mode
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                showControls = !showControls
                            }
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            val gridStep = 38f
                            var x = 0f
                            while (x < w) {
                                drawLine(
                                    color = Color(0x1614B8A6),
                                    start = Offset(x, 0f),
                                    end = Offset(x, h),
                                    strokeWidth = 1f
                                )
                                x += gridStep
                            }
                            var y = 0f
                            while (y < h) {
                                drawLine(
                                    color = Color(0x1614B8A6),
                                    start = Offset(0f, y),
                                    end = Offset(w, y),
                                    strokeWidth = 1f
                                )
                                y += gridStep
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isPlaying) EmeraldSuccess.copy(alpha = 0.25f) else RoseError.copy(alpha = 0.25f),
                                        modifier = Modifier.border(
                                            1.dp,
                                            if (isPlaying) EmeraldSuccess else RoseError,
                                            RoundedCornerShape(6.dp)
                                        )
                                    ) {
                                        Text(
                                            text = if (isPlaying) "PW SMART-BOARD LIVE" else "PAUSED",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0x551E1B4B)
                                    ) {
                                        Text(
                                            text = "Slide ${activeSlideIndex + 1}/${slides.size}",
                                            color = Color(0xFFFDE68A),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier
                                        .alpha(0.92f)
                                        .testTag("video_player_logo_watermark"),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.arcstfug_logo),
                                        contentDescription = "ARCSTFUG Watermark Logo",
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color.White)
                                    )
                                    Text(
                                        text = "ARCSTFUG",
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0x880B132B),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(vertical = 6.dp)
                                    .border(1.dp, Color(0x4038BDF8), RoundedCornerShape(12.dp))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = "📌 $currentTimestampTitle",
                                            color = CyanAccent,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = activeSlideText,
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            lineHeight = 16.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 3,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "👨‍🏫 ${lecture.instructor}",
                                            color = Color(0xFFFDE68A),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )

                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(0x40FFFFFF),
                                                modifier = Modifier
                                                    .clickable {
                                                        manualSlideIndex = if (activeSlideIndex > 0) activeSlideIndex - 1 else slides.size - 1
                                                    }
                                                    .testTag("smartboard_prev_slide_btn")
                                            ) {
                                                Text(
                                                    text = "◀ Prev",
                                                    color = Color.White,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = IndigoPrimary,
                                                modifier = Modifier
                                                    .clickable {
                                                        manualSlideIndex = (activeSlideIndex + 1) % slides.size
                                                    }
                                                    .testTag("smartboard_next_slide_btn")
                                            ) {
                                                Text(
                                                    text = "Next Slide ▶",
                                                    color = Color.White,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = IndigoPrimary,
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clickable { onTogglePlayPause() }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                            contentDescription = if (isPlaying) "Pause" else "Play",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                LinearProgressIndicator(
                                    progress = { progressFraction },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp)),
                                    color = CyanAccent,
                                    trackColor = Color(0x40FFFFFF)
                                )

                                Text(
                                    text = "${formatTime(progressSec)} / ${formatTime(totalSec)}",
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Full Overlay Controls when tapped in Smart-Board mode
                        androidx.compose.animation.AnimatedVisibility(
                            visible = showControls,
                            enter = fadeIn(),
                            exit = fadeOut(),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xB3000000))
                                    .padding(12.dp)
                            ) {
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

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                ) {
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
            }
        }
    }
}

private fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return String.format("%02d:%02d", m, s)
}
