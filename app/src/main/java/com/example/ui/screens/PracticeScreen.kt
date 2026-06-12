package com.example.ui.screens

import android.graphics.PointF
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LessonCharacter
import com.example.ui.components.rememberNihongoSpeaker
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// Representation of user drawn lines
data class UserStroke(val points: List<Offset>)

// Simulated Sparkle particles for celebration explosion
data class SparkleParticle(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val color: Color,
    val r: Float = 6f,
    val alpha: Float = 1f,
    val scale: Float = 1f,
    val isStar: Boolean = false
)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PracticeScreen(
    character: LessonCharacter,
    onBack: () -> Unit,
    onComplete: () -> Unit
) {
    val speaker = rememberNihongoSpeaker()
    val density = LocalDensity.current

    // Canvas drawing states
    val userStrokes = remember { mutableStateListOf<UserStroke>() }
    val currentPoints = remember { mutableStateListOf<Offset>() }
    
    // Mode Select: 0: Guided Mode (मार्गदर्शन), 1: Challenge Mode (चुनौती)
    var activeMode by remember { mutableStateOf(0) }
    var peekGuidelines by remember { mutableStateOf(false) }

    // Demo sequence state
    var isDemoPlaying by remember { mutableStateOf(false) }
    var demoStrokeIndex by remember { mutableStateOf(0) }
    var demoProgress by remember { mutableStateOf(0f) }

    // Canvas actual measured size
    var canvasWidth by remember { mutableStateOf(400f) }
    var canvasHeight by remember { mutableStateOf(400f) }

    // Sparkles and confetti particles list
    var sparkles by remember { mutableStateOf<List<SparkleParticle>>(emptyList()) }

    // Scoring and feedback dialog
    var showMatchDialog by remember { mutableStateOf(false) }
    var tracingScore by remember { mutableStateOf(0) }
    var isSuccess by remember { mutableStateOf(false) }

    // Temporary guideline peek timer (Challenge Mode)
    LaunchedEffect(peekGuidelines) {
        if (peekGuidelines) {
            kotlinx.coroutines.delay(2000)
            peekGuidelines = false
        }
    }

    // Demo Auto playing sequence
    LaunchedEffect(isDemoPlaying) {
        if (isDemoPlaying) {
            val totalStrokes = character.strokes.size
            for (i in 0 until totalStrokes) {
                demoStrokeIndex = i
                demoProgress = 0f
                val steps = 30
                val delayMs = 25L // 750ms total per stroke sweep
                for (step in 0..steps) {
                    demoProgress = step.toFloat() / steps
                    kotlinx.coroutines.delay(delayMs)
                }
                kotlinx.coroutines.delay(180) // Short pause before next stroke
            }
            isDemoPlaying = false
        }
    }

    // Sparkles update physical loop
    LaunchedEffect(sparkles.isNotEmpty()) {
        if (sparkles.isNotEmpty()) {
            while (sparkles.isNotEmpty()) {
                kotlinx.coroutines.delay(16) // ~60 FPS
                sparkles = sparkles.map { p ->
                    p.copy(
                        x = p.x + p.vx,
                        y = p.y + p.vy,
                        vy = p.vy + 0.35f, // Gravity effect
                        alpha = p.alpha - 0.02f,
                        scale = p.scale - 0.02f
                    )
                }.filter { p -> p.alpha > 0f && p.scale > 0f }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "लेखन अभ्यास (Practice Writing)", 
                        fontWeight = FontWeight.ExtraBold, 
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 20.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("practice_back")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            
            // 1. Character Beautiful Specifications Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left: Elegant Master Japanese Letter Card
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(72.dp)
                            .shadow(3.dp, RoundedCornerShape(20.dp))
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                androidx.compose.ui.graphics.Brush.linearGradient(
                                    colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                                )
                            )
                    ) {
                        Text(
                            text = character.char,
                            fontSize = 38.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Middle: Nepali Spec details / Example
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Romaji: ${character.romaji}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFD1FAE5))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "उच्चारण: ${character.nepaliPronunciation}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF047857)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        if (character.meaning.isNotEmpty()) {
                            Text(
                                text = "अर्थ: ${character.meaning} (${character.nepaliMeaning})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Cute Pedagogical Example word
                        if (character.exampleWord.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "उदाहरण: ${character.exampleWord} ➔ ${character.exampleWordNepali} (${character.exampleWordRomaji})",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }

                    // Right: Audio Speaker Trigger Button with native ripple feedback
                    FilledIconButton(
                        onClick = { speaker.speak(character.char) },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .size(46.dp)
                            .testTag("practice_audio_btn")
                    ) {
                        Icon(
                            Icons.Default.VolumeUp,
                            contentDescription = "Pronounce",
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // 2. Beautiful Mode Switcher Pill Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f))
                    .padding(4.dp)
            ) {
                val modes = listOf("मार्गदर्शन (Guided)", "चुनौती (Challenge)")
                modes.forEachIndexed { index, title ->
                    val selected = activeMode == index
                    val animWeight by animateFloatAsState(targetValue = if (selected) 1.05f else 1f)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .scale(animWeight)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable {
                                activeMode = index
                                userStrokes.clear()
                                currentPoints.clear()
                            }
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            // Sub-status guidance line with watch demo trigger
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            ) {
                Text(
                    text = if (isDemoPlaying) {
                        "🦉 साथीले कोर्दै हुनुहुन्छ..."
                    } else if (activeMode == 1) {
                        "चुनौती! गाइडबिनै सम्झेर कोर्नुहोस्।"
                    } else {
                        "गाइडलाइन र दिशा पछ्याउँदै कोर्नुहोस्।"
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (activeMode == 1) Color(0xFFD81B60) else MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Watch Demo button
                TextButton(
                    onClick = {
                        if (!isDemoPlaying) {
                            userStrokes.clear()
                            currentPoints.clear()
                            isDemoPlaying = true
                        }
                    },
                    enabled = !isDemoPlaying,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Icon(
                        if (isDemoPlaying) Icons.Default.HourglassBottom else Icons.Default.PlayCircle,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("प्रदर्शन (Watch Demo)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // 3. Immersive Parchment Writing Canvas Card
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(28.dp))
                    .shadow(3.dp, RoundedCornerShape(28.dp))
                    .background(Color(0xFFFAF6EE)) // Cream traditional Washi paper feel
                    .border(BorderStroke(1.5.dp, MaterialTheme.colorScheme.primaryContainer), RoundedCornerShape(28.dp))
                    .onSizeChanged { size ->
                        canvasWidth = size.width.toFloat()
                        canvasHeight = size.height.toFloat()
                    }
                    .pointerInput(isDemoPlaying) {
                        if (!isDemoPlaying) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    currentPoints.clear()
                                    currentPoints.add(offset)
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                    currentPoints.add(change.position)
                                },
                                onDragEnd = {
                                    if (currentPoints.isNotEmpty()) {
                                        userStrokes.add(UserStroke(currentPoints.toList()))
                                        currentPoints.clear()
                                    }
                                }
                            )
                        }
                    }
            ) {
                // Guidelines visibility checks
                val showGuidelines = activeMode == 0 || peekGuidelines
                
                val guidelineColor = MaterialTheme.colorScheme.primary
                val systemAccentColor = MaterialTheme.colorScheme.secondary

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    // A. Draw calligraphic reference quadrant grid dotted lines
                    val dashEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.4f),
                        start = Offset(0f, height / 2),
                        end = Offset(width, height / 2),
                        strokeWidth = 2.5f,
                        pathEffect = dashEffect
                    )
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.4f),
                        start = Offset(width / 2, 0f),
                        end = Offset(width / 2, height),
                        strokeWidth = 2.5f,
                        pathEffect = dashEffect
                    )

                    // B. Draw Watermark indication in challenge mode if guidelines hidden
                    if (!showGuidelines) {
                        // Drawing subtle background watermark
                        drawCircle(
                            color = Color.LightGray.copy(alpha = 0.05f),
                            radius = width * 0.35f,
                            center = Offset(width / 2, height / 2)
                        )
                    }

                    // C. Draw Guidelines template if allowed
                    if (showGuidelines) {
                        character.strokes.forEach { strokePath ->
                            if (strokePath.points.isNotEmpty()) {
                                val path = Path().apply {
                                    val first = strokePath.points.first()
                                    moveTo(first.x * width, first.y * height)
                                    for (i in 1 until strokePath.points.size) {
                                        val pt = strokePath.points[i]
                                        lineTo(pt.x * width, pt.y * height)
                                    }
                                }
                                // Beautiful double layered guidance templates
                                drawPath(
                                    path = path,
                                    color = guidelineColor.copy(alpha = 0.14f),
                                    style = Stroke(width = 38f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                )
                                drawPath(
                                    path = path,
                                    color = Color.White.copy(alpha = 0.6f),
                                    style = Stroke(width = 24f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                )
                            }
                        }
                    }

                    // D. Draw Real-time Demo progression sweep animation
                    if (isDemoPlaying && demoStrokeIndex in character.strokes.indices) {
                        val currentStrokePath = character.strokes[demoStrokeIndex]
                        val pts = currentStrokePath.points
                        if (pts.isNotEmpty()) {
                            val visiblePointsCount = maxOf(1, (pts.size * demoProgress).toInt())
                            val path = Path().apply {
                                val first = pts.first()
                                moveTo(first.x * width, first.y * height)
                                for (i in 1 until visiblePointsCount) {
                                    val pt = pts[i]
                                    lineTo(pt.x * width, pt.y * height)
                                }
                            }
                            // Gold brush tracer
                            drawPath(
                                path = path,
                                color = Color(0xFFFFC107).copy(alpha = 0.3f),
                                style = Stroke(width = 32f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                            )
                            drawPath(
                                path = path,
                                color = Color(0xFFD81B60),
                                style = Stroke(width = 16f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                            )
                            // Glowing golden cursor brush tracker head
                            val leadPointIdx = minOf(pts.size - 1, visiblePointsCount - 1)
                            val leadPt = pts[leadPointIdx]
                            drawCircle(
                                color = Color(0xFFFFD600),
                                radius = 18f,
                                center = Offset(leadPt.x * width, leadPt.y * height)
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 8f,
                                center = Offset(leadPt.x * width, leadPt.y * height)
                            )
                        }
                    }

                    // E. Beautiful calligraphic user ink brush drawing
                    userStrokes.forEach { stroke ->
                        if (stroke.points.size > 1) {
                            val path = Path().apply {
                                val first = stroke.points.first()
                                moveTo(first.x, first.y)
                                for (i in 1 until stroke.points.size) {
                                    val pt = stroke.points[i]
                                    lineTo(pt.x, pt.y)
                                }
                            }
                            // Calligraphic layer 1: ink bleed aura
                            drawPath(
                                path = path,
                                color = Color(0xFF1E3A8A).copy(alpha = 0.18f), // Deep indigo smudge
                                style = Stroke(width = 24f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                            )
                            // Calligraphic layer 2: high pigment rich core ink
                            drawPath(
                                path = path,
                                color = Color(0xFF0F172A), // Slate 900 carbon ink
                                style = Stroke(width = 12f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                            )
                        }
                    }

                    // F. Live drag active ink feedback
                    if (currentPoints.size > 1) {
                        val path = Path().apply {
                            val first = currentPoints.first()
                            moveTo(first.x, first.y)
                            for (i in 1 until currentPoints.size) {
                                val pt = currentPoints[i]
                                lineTo(pt.x, pt.y)
                            }
                        }
                        // Live aura
                        drawPath(
                            path = path,
                            color = Color(0xFF00E676).copy(alpha = 0.25f), // Neon green wash
                            style = Stroke(width = 28f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                        // Live core
                        drawPath(
                            path = path,
                            color = Color(0xFF00C853), // Highly visible feedback green
                            style = Stroke(width = 14f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }

                    // G. Physics Particle Star fireworks overlay rendering
                    sparkles.forEach { p ->
                        val pColor = p.color.copy(alpha = p.alpha)
                        val sizeRadius = p.r * p.scale
                        if (p.isStar) {
                            // Draw 4-point sparkle stars
                            val starPath = Path().apply {
                                moveTo(p.x, p.y - sizeRadius * 1.5f)
                                lineTo(p.x + sizeRadius * 0.5f, p.y - sizeRadius * 0.5f)
                                lineTo(p.x + sizeRadius * 1.5f, p.y)
                                lineTo(p.x + sizeRadius * 0.5f, p.y + sizeRadius * 0.5f)
                                lineTo(p.x, p.y + sizeRadius * 1.5f)
                                lineTo(p.x - sizeRadius * 0.5f, p.y + sizeRadius * 0.5f)
                                lineTo(p.x - sizeRadius * 1.5f, p.y)
                                lineTo(p.x - sizeRadius * 0.5f, p.y - sizeRadius * 0.5f)
                                close()
                            }
                            drawPath(path = starPath, color = pColor)
                        } else {
                            drawCircle(color = pColor, radius = sizeRadius, center = Offset(p.x, p.y))
                        }
                    }
                }

                // H. Sequence anchor numbered markers (Only in guided mode)
                if (showGuidelines) {
                    character.strokes.forEachIndexed { idx, path ->
                        if (path.points.isNotEmpty()) {
                            val first = path.points.first()
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .offset(
                                        x = with(density) { (first.x * (canvasWidth / density.density)).dp - 10.dp },
                                        y = with(density) { (first.y * (canvasHeight / density.density)).dp - 10.dp }
                                    )
                                    .size(20.dp)
                                    .shadow(2.dp, CircleShape)
                                    .background(
                                        if (idx == demoStrokeIndex && isDemoPlaying) Color(0xFFFFC107) else MaterialTheme.colorScheme.primary,
                                        shape = CircleShape
                                    )
                            ) {
                                Text(
                                    text = "${idx + 1}",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }

                // I. Cute Traditional Chop stamp in bottom right to make it look authentic!
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(14.dp)
                        .border(1.5.dp, Color(0xFFD81B60), RoundedCornerShape(4.dp))
                        .background(Color(0xFFFFF2F5))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "साथी 🦉",
                        color = Color(0xFFD81B60),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )
                }

                // J. Floating "Peek Guidelines Hint" in Challenge mode if they get stuck
                if (activeMode == 1) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                    ) {
                        SmallFloatingActionButton(
                            onClick = { peekGuidelines = true },
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Visibility, contentDescription = "Hint", modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // 4. Polished Action control buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Clear button
                OutlinedButton(
                    onClick = {
                        userStrokes.clear()
                        currentPoints.clear()
                    },
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("practice_clear")
                ) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("मेट्नुहोस्", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                // Submit Validate Button
                Button(
                    onClick = {
                        if (userStrokes.isEmpty()) {
                            tracingScore = 0
                            isSuccess = false
                            showMatchDialog = true
                        } else {
                            val accuracy = matchWritingAccuracy(character, userStrokes)
                            tracingScore = accuracy
                            isSuccess = accuracy >= 55 // generous baseline trigger

                            if (isSuccess) {
                                // Spawn a beautiful sparkling particle volcano from coordinates
                                val colorsList = listOf(
                                    Color(0xFFFFD600), Color(0xFFFF4081), Color(0xFF00E5FF), 
                                    Color(0xFF69F0AE), Color(0xFFE040FB), Color(0xFFFAFFC1)
                                )
                                val particleLaunchers = mutableListOf<SparkleParticle>()
                                repeat(45) {
                                    val angle = Math.random() * 2 * Math.PI
                                    val speed = 6f + Math.random() * 15f
                                    particleLaunchers.add(
                                        SparkleParticle(
                                            x = canvasWidth / 2,
                                            y = canvasHeight * 0.7f,
                                            vx = (cos(angle) * speed).toFloat(),
                                            vy = (sin(angle) * speed - 6f).toFloat(), // shoot upwards
                                            color = colorsList.random(),
                                            r = 5f + (Math.random() * 10f).toFloat(),
                                            isStar = Math.random() > 0.4
                                        )
                                    )
                                }
                                sparkles = particleLaunchers
                            }
                            showMatchDialog = true
                        }
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("practice_check")
                ) {
                    Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("जाँच गर्नुहोस्", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }

    // Success or Retry Match Dialog
    if (showMatchDialog) {
        AlertDialog(
            onDismissRequest = { showMatchDialog = false },
            title = {
                Text(
                    text = if (isSuccess) "उत्कृष्ट! (Subarashii!) 🎉" else "अझैं प्रयास गर्नुहोस्! (Ganbatte!) 💪",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    modifier = Modifier.fillMaxWidth(),
                    color = if (isSuccess) Color(0xFF10B981) else Color(0xFFF59E0B)
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isSuccess) {
                            if (activeMode == 1) "चुनौती मोडमा सफलता! तपाईंको लेखन अदभूत छ!" else "सफलता प्राप्त भयो! तपाईंको लेखन मिलाउनुभयो।"
                        } else "भएन, अझैं अलिक सच्याएर पूरा रेखा कोर्नुहोस् र रीत पुर्याउनुहोस्।",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    
                    // Score meter layout
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(100.dp)
                            .border(BorderStroke(4.dp, if (isSuccess) Color(0xFF10B981) else Color(0xFFF59E0B)), CircleShape)
                            .padding(12.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$tracingScore%",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isSuccess) Color(0xFF10B981) else Color(0xFFF59E0B)
                            )
                            Text(
                                text = "शुद्धता",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(18.dp))
                    
                    // Star progression with bonus if completed in challenge mode
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val starsCount = if (isSuccess) (if (activeMode == 1) 3 else 3) else 1
                        repeat(starsCount) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = if (isSuccess) Color(0xFFFFD600) else Color.LightGray,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }
                    if (isSuccess) {
                        val earnedStars = if (activeMode == 1) 25 else 15
                        Text(
                            text = if (activeMode == 1) {
                                "+$earnedStars ताराहरू प्राप्त! (Challenge Bonus!) 🌟"
                            } else {
                                "+$earnedStars ताराहरू प्राप्त! 🌟"
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showMatchDialog = false
                        if (isSuccess) {
                            onComplete()
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("practice_confirm_ok")
                ) {
                    Text(
                        text = if (isSuccess) "अर्को अक्षर सिक्नुहोस्" else "फेरि प्रयास गर्ने", 
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                }
            }
        )
    }
}

// Coordinate based fuzzy checkpoint visitor matching algorithm
private fun matchWritingAccuracy(character: LessonCharacter, strokes: List<UserStroke>): Int {
    val userPoints = strokes.flatMap { it.points }
    if (userPoints.isEmpty() || character.strokes.isEmpty()) return 0

    // Measure bounding boxes of drawn outline relative boundaries to normalize size
    var minX = Float.MAX_VALUE
    var maxX = Float.MIN_VALUE
    var minY = Float.MAX_VALUE
    var maxY = Float.MIN_VALUE

    for (p in userPoints) {
        if (p.x < minX) minX = p.x
        if (p.x > maxX) maxX = p.x
        if (p.y < minY) minY = p.y
        if (p.y > maxY) maxY = p.y
    }

    val width = maxX - minX
    val height = maxY - minY
    if (width < 25f || height < 25f) return 15 // tiny dots are likely accidents

    // Map raw coordinate point arrays to matching relative coordinate bounds [0.0f - 1.0f]
    val normalizedUserPoints = userPoints.map { p ->
        PointF(
            (p.x - minX) / width,
            (p.y - minY) / height
        )
    }

    val templateCheckpoints = character.strokes.flatMap { it.points }
    var hits = 0
    val distanceThreshold = 0.22f // matching radius tolerance 

    for (checkPt in templateCheckpoints) {
        var foundCheck = false
        for (userPt in normalizedUserPoints) {
            val dx = userPt.x - checkPt.x
            val dy = userPt.y - checkPt.y
            val dist = sqrt(dx * dx + dy * dy)
            if (dist <= distanceThreshold) {
                foundCheck = true
                break
            }
        }
        if (foundCheck) {
            hits++
        }
    }

    val checkpointMatchRatio = if (templateCheckpoints.isNotEmpty()) {
        hits.toFloat() / templateCheckpoints.size
    } else 0f

    val scorePercentage = (checkpointMatchRatio * 100).toInt()
    return minOf(100, maxOf(10, scorePercentage))
}
