package com.smartbite.app.presentation.screens.welcome

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartbite.app.presentation.theme.DarkOrange
import com.smartbite.app.presentation.theme.PrimaryOrange
import kotlinx.coroutines.delay

@Composable
fun SuccessSplashScreen(userName: String, onFinished: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    
    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1.1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(1000)
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2500)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(PrimaryOrange, DarkOrange)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                "🎊",
                fontSize = 80.sp,
                modifier = Modifier
                    .scale(scaleAnim)
                    .alpha(alphaAnim)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                "Welcome, $userName!",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(alphaAnim)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                "Ready to find some delicious deals?",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(alphaAnim)
            )
        }
    }
}
