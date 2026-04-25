package com.smartbite.app.presentation.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smartbite.app.AppState
import com.smartbite.app.data.model.Dish
import com.smartbite.app.data.model.DishCategory
import com.smartbite.app.data.model.FilterState
import com.smartbite.app.presentation.components.*
import com.smartbite.app.presentation.theme.*

class HomeViewModel : ViewModel() {
    var selectedCategory by mutableStateOf<DishCategory?>(null)
    var filterState by mutableStateOf(FilterState())
    var showFilterSheet by mutableStateOf(false)
    var showLocationDialog by mutableStateOf(false)

    val greeting: String get() {
        val name = AppState.userRepository.currentUser?.name
            ?: AppState.userRepository.guestName ?: "there"
        return "Hi, ${name.split(" ").first()} 👋"
    }
    val location: String get() = AppState.userRepository.currentUser?.state ?: "Malaysia"
    val isLoggedIn: Boolean get() = AppState.userRepository.isLoggedIn
    val allDishes: List<Dish> get() = AppState.dishRepository.allDishes
    val filteredDishes: List<Dish> get() {
        val base = filterState.copy(
            selectedCategories = if (selectedCategory != null) setOf(selectedCategory!!) else filterState.selectedCategories
        )
        return AppState.dishRepository.filterDishes(base)
    }
    val favourites: List<Dish> get() = AppState.favouritesRepository.getAll(allDishes)

    fun updateLocation(newState: String) {
        AppState.userRepository.updateState(newState)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(onDishClick: (Int) -> Unit, onSearchClick: () -> Unit) {
    val vm: HomeViewModel = viewModel()

    if (vm.showFilterSheet) {
        FilterBottomSheet(
            currentFilter = vm.filterState,
            onApply = { vm.filterState = it; vm.showFilterSheet = false },
            onDismiss = { vm.showFilterSheet = false }
        )
    }

    if (vm.showLocationDialog) {
        val states = listOf("Kuala Lumpur", "Selangor", "Penang", "Johor", "Melaka", "Sabah", "Sarawak")
        AlertDialog(
            onDismissRequest = { vm.showLocationDialog = false },
            title = { Text("Select Location", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    states.forEach { state ->
                        TextButton(
                            onClick = {
                                vm.updateLocation(state)
                                vm.showLocationDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(state, color = TextDark, textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5)),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.clickable { vm.showLocationDialog = true },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📍", fontSize = 16.sp)
                    Spacer(Modifier.width(3.dp))
                    Text(
                        vm.location,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextDark
                    )
                    Text(" ▾", fontSize = 11.sp, color = PrimaryOrange)
                }
                Text(vm.greeting, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextDark)
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(start = 16.dp, end = 16.dp, bottom = 14.dp, top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f).clickable { onSearchClick() },
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF0F0F0)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, null, tint = TextGrey, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Search for a dish...", color = TextGrey, fontSize = 14.sp)
                    }
                }
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(PrimaryOrange)
                        .clickable { vm.showFilterSheet = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Tune, null, tint = Color.White, modifier = Modifier.size(22.dp))
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CategoryPill("All", null, vm.selectedCategory) { vm.selectedCategory = null }
                    DishCategory.entries.forEach { cat ->
                        val emoji = when (cat) {
                            DishCategory.RICE -> "🍚"; DishCategory.NOODLES -> "🍜"
                            DishCategory.SNACKS -> "🥙"; DishCategory.DRINKS -> "🧋"
                            DishCategory.DESSERTS -> "🍮"
                        }
                        CategoryPill(
                            "$emoji ${cat.name.lowercase().replaceFirstChar { it.uppercase() }}",
                            cat, vm.selectedCategory
                        ) { vm.selectedCategory = cat }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        if (vm.filterState.budgetModeOn && vm.filterState.budgetAmount > 0) {
            item {
                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)) {
                    Surface(color = PrimaryOrange.copy(alpha = 0.12f), shape = RoundedCornerShape(20.dp)) {
                        Text(
                            "Budget: RM %.2f".format(vm.filterState.budgetAmount),
                            color = PrimaryOrange, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }

        if (vm.isLoggedIn) {
            item {
                Text(
                    "My Favourites ❤️",
                    fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                )
                if (vm.favourites.isEmpty()) {
                    Text(
                        "Tap ❤️ on a dish to save it here",
                        fontSize = 13.sp, color = TextGrey,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                    )
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(vm.favourites) { dish -> FavCard(dish) { onDishClick(dish.id) } }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        } else {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.horizontalGradient(listOf(PrimaryOrange, DarkOrange)))
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Text(
                        "Login to save your favourite dishes ❤️",
                        color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp
                    )
                }
            }
        }

        item {
            Text(
                "Browse All Dishes (${vm.filteredDishes.size})",
                fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 8.dp)
            )
        }

        if (vm.filteredDishes.isEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("😕", fontSize = 48.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("No dishes found", fontWeight = FontWeight.SemiBold, color = TextDark)
                    Text("Try adjusting your filters", color = TextGrey, fontSize = 13.sp)
                    Spacer(Modifier.height(12.dp))
                    TextButton(onClick = { vm.filterState = FilterState(); vm.selectedCategory = null }) {
                        Text("Clear Filters", color = PrimaryOrange)
                    }
                }
            }
        } else {
            items(vm.filteredDishes) { dish ->
                DishCard(
                    dish = dish,
                    onClick = { onDishClick(dish.id) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)
                )
            }
        }
    }
}

@Composable
private fun CategoryPill(label: String, cat: DishCategory?, selected: DishCategory?, onClick: () -> Unit) {
    val isSelected = cat == selected
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = if (isSelected) PrimaryOrange else Color.White,
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, DividerColor) else null
    ) {
        Text(
            label, fontSize = 13.sp,
            color = if (isSelected) Color.White else TextDark,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun FavCard(dish: Dish, onClick: () -> Unit) {
    val (g1, g2) = dishGradient(dish.category)
    Card(
        modifier = Modifier.width(110.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(10.dp))
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
                    Text(dish.emoji, fontSize = 26.sp)
                }
            }
            Spacer(Modifier.height(5.dp))
            Text(
                dish.name, fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
                color = TextDark, maxLines = 2, lineHeight = 13.sp, textAlign = TextAlign.Center
            )
        }
    }
}

fun dishGradient(cat: DishCategory) = when (cat) {
    DishCategory.RICE -> Pair(Color(0xFFFFCE88), Color(0xFFFF9800))
    DishCategory.NOODLES -> Pair(Color(0xFFFFAB91), Color(0xFFE64A19))
    DishCategory.SNACKS -> Pair(Color(0xFFA5D6A7), Color(0xFF388E3C))
    DishCategory.DRINKS -> Pair(Color(0xFF80DEEA), Color(0xFF0097A7))
    DishCategory.DESSERTS -> Pair(Color(0xFFCE93D8), Color(0xFF7B1FA2))
}
