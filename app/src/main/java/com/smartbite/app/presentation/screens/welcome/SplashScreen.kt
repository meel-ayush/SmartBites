package com.smartbite.app.presentation.screens.welcome

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartbite.app.R
import com.smartbite.app.presentation.theme.DarkOrange
import com.smartbite.app.presentation.theme.PrimaryOrange
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onAnimationFinished: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    
    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
    )
    
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000)
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2500)
        onAnimationFinished()
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
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.applogo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(160.dp)
                    .scale(scaleAnim)
                    .alpha(alphaAnim)
                    .clip(CircleShape),
                contentScale = ContentScale.Fit
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "SmartBite",
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.alpha(alphaAnim)
            )
            
            Text(
                text = "order smarter. spend better.",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.alpha(alphaAnim)
            )
        }
    }
}
