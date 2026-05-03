package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlin.random.Random

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MathQuizApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

enum class AnimalState {
    NEUTRAL, HAPPY, SAD
}

data class Question(
    val text: String,
    val options: List<Int>,
    val correctAnswer: Int
)

fun generateQuestion(): Question {
    val a = Random.nextInt(1, 21)
    val b = Random.nextInt(1, 21)
    val operation = listOf("+", "-", "*").random()
    val correctAnswer = when (operation) {
        "+" -> a + b
        "-" -> a - b
        else -> a * b
    }

    val options = mutableSetOf<Int>()
    options.add(correctAnswer)
    while (options.size < 5) {
        val wrongAnswer = correctAnswer + Random.nextInt(-10, 11)
        if (wrongAnswer != correctAnswer) {
            options.add(wrongAnswer)
        }
    }

    return Question(
        text = "$a $operation $b = ?",
        options = options.toList().shuffled(),
        correctAnswer = correctAnswer
    )
}

@Composable
fun CuteAnimal(state: AnimalState, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "animal_animation")
    
    val earTwitch by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(150, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ear_twitch"
    )

    val blink by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(200, delayMillis = 2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blink"
    )

    val bounce by animateFloatAsState(
        targetValue = if (state == AnimalState.HAPPY) -30f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "bounce"
    )

    val shake by animateFloatAsState(
        targetValue = if (state == AnimalState.SAD) 10f else 0f,
        animationSpec = repeatable(
            iterations = 3,
            animation = tween(100),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shake"
    )

    Canvas(
        modifier = modifier
            .size(150.dp)
            .graphicsLayer(translationY = bounce, translationX = shake)
    ) {
        val center = Offset(size.width / 2, size.height / 2)
        val headRadius = size.width * 0.35f

        // Ears with gradient
        val earColor = Color(0xFFFFCC80)
        
        val leftEarPath = Path().apply {
            moveTo(center.x - headRadius * 0.8f, center.y - headRadius * 0.5f)
            lineTo(center.x - headRadius * 1.3f, center.y - headRadius * 1.3f + earTwitch)
            lineTo(center.x - headRadius * 0.1f, center.y - headRadius * 1.0f)
            close()
        }
        val rightEarPath = Path().apply {
            moveTo(center.x + headRadius * 0.8f, center.y - headRadius * 0.5f)
            lineTo(center.x + headRadius * 1.3f, center.y - headRadius * 1.3f)
            lineTo(center.x + headRadius * 0.1f, center.y - headRadius * 1.0f)
            close()
        }
        
        drawPath(leftEarPath, earColor)
        drawPath(rightEarPath, earColor)

        // Head with radial gradient for "3D" look
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFF3E0), Color(0xFFFFE0B2), Color(0xFFFFCC80)),
                center = center - Offset(headRadius * 0.3f, headRadius * 0.3f),
                radius = headRadius * 1.5f
            ),
            radius = headRadius,
            center = center
        )

        // Eyes
        val eyeWidth = headRadius * 0.25f
        val eyeHeight = if (state == AnimalState.HAPPY) headRadius * 0.1f else headRadius * 0.35f * blink
        
        val leftEyeCenter = Offset(center.x - headRadius * 0.45f, center.y - headRadius * 0.1f)
        val rightEyeCenter = Offset(center.x + headRadius * 0.45f, center.y - headRadius * 0.1f)

        if (state == AnimalState.SAD) {
            // Sad eyes (downward arcs)
            drawArc(
                color = Color.Black,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(leftEyeCenter.x - eyeWidth / 2, leftEyeCenter.y - eyeHeight / 2),
                size = Size(eyeWidth, eyeHeight),
                style = Stroke(width = 4f)
            )
            drawArc(
                color = Color.Black,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(rightEyeCenter.x - eyeWidth / 2, rightEyeCenter.y - eyeHeight / 2),
                size = Size(eyeWidth, eyeHeight),
                style = Stroke(width = 4f)
            )
        } else {
            drawOval(
                color = Color.Black,
                topLeft = Offset(leftEyeCenter.x - eyeWidth / 2, leftEyeCenter.y - eyeHeight / 2),
                size = Size(eyeWidth, eyeHeight)
            )
            drawOval(
                color = Color.Black,
                topLeft = Offset(rightEyeCenter.x + headRadius * 0.05f - eyeWidth / 2, rightEyeCenter.y - eyeHeight / 2),
                size = Size(eyeWidth, eyeHeight)
            )
            
            // Eye highlights
            if (eyeHeight > 5f) {
                drawCircle(
                    color = Color.White,
                    radius = eyeWidth * 0.2f,
                    center = leftEyeCenter - Offset(eyeWidth * 0.15f, eyeHeight * 0.15f)
                )
                drawCircle(
                    color = Color.White,
                    radius = eyeWidth * 0.2f,
                    center = rightEyeCenter - Offset(eyeWidth * 0.15f, eyeHeight * 0.15f)
                )
            }
        }

        // Nose
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFADAD), Color(0xFFFF8A80)),
                center = Offset(center.x, center.y + headRadius * 0.15f),
                radius = headRadius * 0.2f
            ),
            radius = headRadius * 0.12f,
            center = Offset(center.x, center.y + headRadius * 0.15f)
        )

        // Mouth
        if (state == AnimalState.HAPPY) {
            drawArc(
                color = Color(0xFFD32F2F),
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(center.x - headRadius * 0.2f, center.y + headRadius * 0.2f),
                size = Size(headRadius * 0.4f, headRadius * 0.3f)
            )
        } else {
            val mouthPath = Path().apply {
                moveTo(center.x - headRadius * 0.2f, center.y + headRadius * 0.3f)
                quadraticTo(
                    center.x, center.y + headRadius * (if (state == AnimalState.SAD) 0.2f else 0.4f),
                    center.x + headRadius * 0.2f, center.y + headRadius * 0.3f
                )
            }
            drawPath(mouthPath, Color.Black, style = Stroke(width = 3f))
        }

        // Whiskers
        val whiskerLength = headRadius * 0.7f
        val whiskerY = center.y + headRadius * 0.2f
        
        drawLine(Color.Gray, Offset(center.x - headRadius * 0.6f, whiskerY), Offset(center.x - headRadius * 0.6f - whiskerLength, whiskerY - 10f), 2f)
        drawLine(Color.Gray, Offset(center.x - headRadius * 0.6f, whiskerY + 10f), Offset(center.x - headRadius * 0.6f - whiskerLength, whiskerY + 20f), 2f)
        
        drawLine(Color.Gray, Offset(center.x + headRadius * 0.6f, whiskerY), Offset(center.x + headRadius * 0.6f + whiskerLength, whiskerY - 10f), 2f)
        drawLine(Color.Gray, Offset(center.x + headRadius * 0.6f, whiskerY + 10f), Offset(center.x + headRadius * 0.6f + whiskerLength, whiskerY + 20f), 2f)
    }
}

@Composable
fun MathQuizApp(modifier: Modifier = Modifier) {
    var question by remember { mutableStateOf(generateQuestion()) }
    var score by remember { mutableStateOf(0) }
    var failedQuestions by remember { mutableStateOf(setOf<Question>()) }
    var animalState by remember { mutableStateOf(AnimalState.NEUTRAL) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CuteAnimal(state = animalState)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Score: $score",
            style = MaterialTheme.typography.headlineSmall
        )
        if (failedQuestions.contains(question)) {
            Text(
                text = "Review Question",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelLarge
            )
        } else {
            Spacer(modifier = Modifier.height(20.dp)) // Maintain layout consistency
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = question.text,
            style = MaterialTheme.typography.displayMedium,
            fontSize = 48.sp
        )
        Spacer(modifier = Modifier.height(32.dp))
        question.options.forEach { option ->
            Button(
                onClick = {
                    val isCorrect = option == question.correctAnswer
                    if (isCorrect) {
                        score++
                        failedQuestions = failedQuestions - question
                        animalState = AnimalState.HAPPY
                    } else {
                        failedQuestions = failedQuestions + question
                        animalState = AnimalState.SAD
                    }

                    scope.launch {
                        delay(1000)
                        animalState = AnimalState.NEUTRAL
                    }

                    // 30% chance to resurface a failed question if any exist
                    question = if (failedQuestions.isNotEmpty() && Random.nextFloat() < 0.3f) {
                        failedQuestions.random()
                    } else {
                        generateQuestion()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(text = option.toString(), fontSize = 20.sp)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Failed questions to review: ${failedQuestions.size}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MathQuizAppPreview() {
    MyApplicationTheme {
        MathQuizApp()
    }
}
