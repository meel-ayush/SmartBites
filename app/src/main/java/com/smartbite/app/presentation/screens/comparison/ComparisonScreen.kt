package com.smartbite.app.presentation.screens.comparison

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartbite.app.AppState
import com.smartbite.app.data.model.DietaryType
import com.smartbite.app.data.model.PlatformPrice
import com.smartbite.app.data.model.NutritionInfo
import com.smartbite.app.presentation.screens.home.dishGradient
import com.smartbite.app.presentation.theme.*

@Composable
fun ComparisonScreen(dishId: Int, onBack: () -> Unit, onLoginRequired: () -> Unit) {
    val dish = remember { AppState.dishRepository.getDishById(dishId) }
    val prices = remember { dish?.let { AppState.dishRepository.getPricesForDish(it) } ?: emptyList() }
    var isFavourite by remember { mutableStateOf(AppState.favouritesRepository.contains(dishId)) }
    var showLoginDialog by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current

    if (dish == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Dish not found", color = TextGrey)
        }
        return
    }

    if (showLoginDialog) {
        AlertDialog(
            onDismissRequest = { showLoginDialog = false },
            title = { Text("Login Required", fontWeight = FontWeight.Bold) },
            text = { Text("Please login to save your favourite dishes.") },
            confirmButton = {
                TextButton(onClick = { showLoginDialog = false; onLoginRequired() }) {
                    Text("Login", color = PrimaryOrange)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLoginDialog = false }) {
                    Text("Not Now", color = TextGrey)
                }
            }
        )
    }

    val (heroG1, heroG2) = dishGradient(dish.category)

    Scaffold(
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                OutlinedButton(
                    onClick = {
                        if (AppState.userRepository.isLoggedIn) {
                            isFavourite = !isFavourite
                            if (isFavourite) AppState.favouritesRepository.add(dishId)
                            else AppState.favouritesRepository.remove(dishId)
                        } else {
                            showLoginDialog = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp,
                        if (isFavourite) ErrorRed else DividerColor)
                ) {
                    Icon(
                        if (isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isFavourite) ErrorRed else TextGrey,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (isFavourite) "Saved to Favourites ❤️" else "Save to Favourites ♡",
                        color = if (isFavourite) ErrorRed else TextDark,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .background(Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(Brush.verticalGradient(listOf(heroG1, heroG2)))
            ) {
                if (dish.imageRes != 0) {
                    Image(
                        painter = painterResource(dish.imageRes),
                        contentDescription = dish.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        dish.emoji,
                        fontSize = 90.sp,
                        modifier = Modifier.align(Alignment.Center).offset(y = (-16).dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.7f)))
                        )
                )
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                }
                Column(
                    modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
                ) {
                    Text(dish.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    Spacer(Modifier.height(6.dp))
                    DietaryBadge(dish.dietary)
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                NutritionRow(dish.nutritionInfo)
                Spacer(Modifier.height(14.dp))

                Text(
                    "\"${dish.description}\"",
                    fontSize = 13.sp,
                    color = TextGrey,
                    fontStyle = FontStyle.Italic,
                    lineHeight = 20.sp
                )

                Spacer(Modifier.height(22.dp))
                Text("Compare Prices", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextDark)
                Spacer(Modifier.height(14.dp))

                prices.forEach { price ->
                    PlatformCard(price = price, onOrder = { uriHandler.openUri(price.websiteUrl) })
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun DietaryBadge(dietary: DietaryType) {
    val (color, text) = when (dietary) {
        DietaryType.PURE_VEG -> Color(0xFF4CAF50) to "PURE VEG 🌿"
        DietaryType.VEG_EGG -> Color(0xFFFF9800) to "VEG EGG 🥚"
        DietaryType.NON_VEG -> Color(0xFFF44336) to "NON VEG 🍗"
    }
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun NutritionRow(info: NutritionInfo) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        NutritionItem("Calories", "${info.calories} kcal")
        NutritionItem("Protein", info.protein)
        NutritionItem("Carbs", info.carbs)
        NutritionItem("Fat", info.fat)
    }
}

@Composable
private fun NutritionItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
        Text(label, fontSize = 11.sp, color = TextGrey)
    }
}

@Composable
private fun PlatformCard(price: PlatformPrice, onOrder: () -> Unit) {
    val (bgColor, _, logoContent) = when (price.platformName) {
        "GrabFood" -> Triple(GrabGreen, Color.White, @Composable {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Grab", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, lineHeight = 14.sp)
                Text("Food", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, lineHeight = 14.sp)
            }
        })
        "Foodpanda" -> Triple(FoodpandaPink, Color.White, @Composable {
            Text("🐼", fontSize = 24.sp)
        })
        else -> Triple(ShopeeFoodOrange, Color.White, @Composable {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🍽️", fontSize = 18.sp)
                Text("ShopeeFood", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 7.sp, lineHeight = 9.sp)
            }
        })
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(if (price.isBestDeal) 6.dp else 2.dp),
        border = if (price.isBestDeal) androidx.compose.foundation.BorderStroke(2.dp, PrimaryOrange) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(bgColor),
                    contentAlignment = Alignment.Center
                ) { logoContent() }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(price.platformName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                        Surface(
                            color = when {
                                price.isBestDeal -> Color(0xFFFFF3CD)
                                price.badgeText.contains("FAST") -> GrabGreen.copy(0.1f)
                                else -> FoodpandaPink.copy(0.1f)
                            },
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                if (price.isBestDeal) "BEST DEAL ⭐" else price.badgeText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (price.isBestDeal) Color(0xFF856404) else
                                    if (price.badgeText.contains("FAST")) GrabGreen else FoodpandaPink,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    PriceRow("Food Price", "RM %.2f".format(price.foodPrice))
                    PriceRow("Delivery Fee", "RM %.2f".format(price.deliveryFee))
                    PriceRow("Service Tax 6%", "RM %.2f".format(price.serviceTax))
                }
            }

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = DividerColor)
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Grand Total", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
                Text(
                    "RM %.2f".format(price.grandTotal),
                    fontWeight = FontWeight.ExtraBold, fontSize = 17.sp,
                    color = if (price.isBestDeal) SuccessGreen else TextDark
                )
            }

            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🕐 ~${price.estimatedTime}", fontSize = 12.sp, color = TextGrey)
                if (price.promoText.isNotEmpty()) {
                    Surface(color = PrimaryOrange.copy(alpha = 0.10f), shape = RoundedCornerShape(20.dp)) {
                        Text(price.promoText, color = PrimaryOrange, fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            Button(
                onClick = onOrder,
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = bgColor)
            ) {
                Text("Order on ${price.platformName} →", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun PriceRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 13.sp, color = TextGrey)
        Text(value, fontSize = 13.sp, color = TextDark)
    }
}
