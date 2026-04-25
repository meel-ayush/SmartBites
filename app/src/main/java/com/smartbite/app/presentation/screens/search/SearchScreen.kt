package com.smartbite.app.presentation.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smartbite.app.AppState
import com.smartbite.app.data.model.Dish
import com.smartbite.app.data.model.FilterState
import com.smartbite.app.presentation.components.*
import com.smartbite.app.presentation.theme.*

class SearchViewModel : ViewModel() {
    var searchQuery by mutableStateOf("")
    var filterState by mutableStateOf(FilterState())
    var showFilterSheet by mutableStateOf(false)

    val results: List<Dish> get() {
        val afterFilter = AppState.dishRepository.filterDishes(filterState)
        return if (searchQuery.isBlank()) afterFilter
        else afterFilter.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }
}

@Composable
fun SearchScreen(onBack: () -> Unit, onDishClick: (Int) -> Unit) {
    val vm: SearchViewModel = viewModel()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    if (vm.showFilterSheet) {
        FilterBottomSheet(
            currentFilter = vm.filterState,
            onApply = { vm.filterState = it; vm.showFilterSheet = false },
            onDismiss = { vm.showFilterSheet = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextDark)
            }
            OutlinedTextField(
                value = vm.searchQuery,
                onValueChange = { vm.searchQuery = it },
                placeholder = { Text("Search for a dish...", fontSize = 14.sp, color = TextGrey) },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryOrange,
                    unfocusedBorderColor = DividerColor
                ),
                trailingIcon = {
                    if (vm.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { vm.searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextGrey)
                        }
                    }
                },
                singleLine = true
            )
            Spacer(Modifier.width(4.dp))
            IconButton(onClick = { vm.showFilterSheet = true }) {
                Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = PrimaryOrange)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "${vm.results.size} results found",
                fontSize = 13.sp,
                color = TextGrey,
                modifier = Modifier.weight(1f)
            )
            if (vm.filterState.budgetModeOn && vm.filterState.budgetAmount > 0) {
                Surface(color = PrimaryOrange.copy(alpha = 0.12f), shape = RoundedCornerShape(20.dp)) {
                    Text(
                        "Budget: RM %.2f".format(vm.filterState.budgetAmount),
                        color = PrimaryOrange,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
            }
        }

        if (vm.results.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔍", fontSize = 48.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        if (vm.filterState.budgetModeOn) "No dishes within RM %.2f".format(vm.filterState.budgetAmount)
                        else "No dishes found for \"${vm.searchQuery}\"",
                        color = TextDark, fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = {
                        vm.searchQuery = ""
                        vm.filterState = FilterState()
                    }) {
                        Text(if (vm.filterState.budgetModeOn) "Adjust Budget" else "Clear Filters", color = PrimaryOrange)
                    }
                }
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
                items(vm.results) { dish ->
                    DishCard(
                        dish = dish,
                        onClick = { onDishClick(dish.id) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
