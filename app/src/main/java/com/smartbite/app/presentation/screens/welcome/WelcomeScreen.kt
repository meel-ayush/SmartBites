package com.smartbite.app.presentation.screens.welcome

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartbite.app.R
import com.smartbite.app.presentation.components.OrangeFilledButton
import com.smartbite.app.presentation.components.OrangeOutlinedButton
import com.smartbite.app.presentation.theme.*
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onGuestClick: () -> Unit
) {
    var showContent by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(100)
        showContent = true
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(
            brush = Brush.verticalGradient(
                colors = listOf(PrimaryOrange, DarkOrange)
            )
        )
    ) {

        // Watermark fork
        Image(
            painter = painterResource(R.drawable.ic_fork_watermark),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = (-20).dp, y = (-80).dp)
                .height(250.dp)
                .alpha(0.08f)
        )

        // Watermark spoon
        Image(
            painter = painterResource(R.drawable.ic_spoon_watermark),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 30.dp, y = 40.dp)
                .height(220.dp)
                .alpha(0.08f)
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.1f),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(animationSpec = tween(1200)) + scaleIn(animationSpec = tween(1200))
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            modifier = Modifier.size(125.dp), // Made even smaller as requested
                            shape = RoundedCornerShape(28.dp), // Squircle-like shape (flat top/bottom feel)
                            color = Color.White.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White.copy(alpha = 0.4f))
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(10.dp)) {
                                Image(
                                    painter = painterResource(R.drawable.applogo),
                                    contentDescription = "SmartBite",
                                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp)),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "SmartBite",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 32.sp,
                            letterSpacing = 2.sp
                        )
                        Text(
                            "order smarter. spend better.",
                            color = Color.White.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = showContent,
                enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(1000, easing = FastOutSlowInEasing))
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                    color = Color.White,
                    shadowElevation = 32.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp, vertical = 36.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "The Ultimate Price Scanner 🚀",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = TextDark,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Instantly compare prices across Grab, Foodpanda, and ShopeeFood to ensure you never overpay.",
                            fontSize = 15.sp,
                            color = TextGrey,
                            textAlign = TextAlign.Center,
                            lineHeight = 24.sp
                        )
                        Spacer(Modifier.height(32.dp))
                        OrangeFilledButton(text = "Login to Your Account", onClick = onLoginClick)
                        Spacer(Modifier.height(12.dp))
                        OrangeOutlinedButton(text = "Create an Account", onClick = onRegisterClick)
                        Spacer(Modifier.height(24.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            HorizontalDivider(modifier = Modifier.weight(1f), color = DividerColor.copy(alpha = 0.5f))
                            Text("  OR  ", fontSize = 12.sp, color = TextGrey.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
                            HorizontalDivider(modifier = Modifier.weight(1f), color = DividerColor.copy(alpha = 0.5f))
                        }
                        Spacer(Modifier.height(16.dp))
                        TextButton(onClick = onGuestClick) {
                            Text(
                                "Continue as Guest →",
                                color = PrimaryOrange,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(Modifier.height(20.dp))
                        Text(
                            "Join thousands of smart foodies saving daily!",
                            fontSize = 12.sp,
                            color = PrimaryOrange,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
