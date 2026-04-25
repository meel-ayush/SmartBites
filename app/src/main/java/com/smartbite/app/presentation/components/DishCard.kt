package com.smartbite.app.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartbite.app.data.model.Dish
import com.smartbite.app.data.model.DishCategory
import com.smartbite.app.domain.PriceCalculator
import com.smartbite.app.presentation.screens.home.dishGradient
import com.smartbite.app.presentation.theme.*

@Composable
fun DishCard(dish: Dish, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val prices = PriceCalculator.getAllPrices(dish.basePrice)
    val grab = prices[0]; val panda = prices[1]; val shopee = prices[2]
    val minTotal = prices.minOf { it.grandTotal }
    val uriHandler = LocalUriHandler.current
    val (g1, g2) = dishGradient(dish.category)

    val catAccent = when (dish.category) {
        DishCategory.RICE -> Color(0xFFE65100)
        DishCategory.NOODLES -> Color(0xFFBF360C)
        DishCategory.SNACKS -> Color(0xFF2E7D32)
        DishCategory.DRINKS -> Color(0xFF006064)
        DishCategory.DESSERTS -> Color(0xFF6A1B9A)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp), ambientColor = Color.Black.copy(0.08f))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.verticalGradient(listOf(g1, g2))),
                contentAlignment = Alignment.Center
            ) {
                if (dish.imageRes != 0) {
                    Image(
                        painter = painterResource(dish.imageRes),
                        contentDescription = dish.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(dish.emoji, fontSize = 44.sp)
                }
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    dish.name,
                    fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark,
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                
                Spacer(Modifier.height(4.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🔥 ${dish.nutritionInfo.calories} kcal", fontSize = 11.sp, color = TextGrey)
                    Spacer(Modifier.width(8.dp))
                    Surface(color = catAccent.copy(0.08f), shape = RoundedCornerShape(8.dp)) {
                        Text(
                            dish.category.name.lowercase().replaceFirstChar { it.uppercase() },
                            fontSize = 9.sp, color = catAccent, fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PriceBadge(
                        "Grab", GrabGreen, 
                        "RM %.2f".format(grab.grandTotal), 
                        grab.grandTotal == minTotal
                    ) { uriHandler.openUri(grab.websiteUrl) }
                    
                    PriceBadge(
                        "Panda", FoodpandaPink, 
                        "RM %.2f".format(panda.grandTotal), 
                        panda.grandTotal == minTotal
                    ) { uriHandler.openUri(panda.websiteUrl) }
                    
                    PriceBadge(
                        "Shopee", ShopeeFoodOrange, 
                        "RM %.2f".format(shopee.grandTotal), 
                        shopee.grandTotal == minTotal
                    ) { uriHandler.openUri(shopee.websiteUrl) }
                }
            }
        }
    }
}

@Composable
private fun PriceBadge(
    label: String, 
    color: Color, 
    price: String, 
    isBest: Boolean, 
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (isBest) color.copy(alpha = 0.1f) else Color(0xFFF5F5F5),
        shape = RoundedCornerShape(10.dp),
        border = if (isBest) androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f)) else null,
        modifier = Modifier.height(34.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(color))
            Spacer(Modifier.width(5.dp))
            Text(
                price,
                fontSize = 11.sp,
                fontWeight = if (isBest) FontWeight.ExtraBold else FontWeight.Medium,
                color = if (isBest) color else TextDark
            )
        }
    }
}
