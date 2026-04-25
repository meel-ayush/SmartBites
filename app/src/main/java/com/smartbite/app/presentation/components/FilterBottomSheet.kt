package com.smartbite.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartbite.app.data.model.DietaryType
import com.smartbite.app.data.model.DishCategory
import com.smartbite.app.data.model.FilterState
import com.smartbite.app.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    currentFilter: FilterState,
    onApply: (FilterState) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedDietary by remember { mutableStateOf(currentFilter.selectedDietary) }
    var selectedCategories by remember { mutableStateOf(currentFilter.selectedCategories.toMutableSet()) }
    var selectedPlatforms by remember { mutableStateOf(currentFilter.selectedPlatforms.toMutableSet()) }
    var budgetOn by remember { mutableStateOf(currentFilter.budgetModeOn) }
    var budgetText by remember {
        mutableStateOf(if (currentFilter.budgetAmount > 0) "%.2f".format(currentFilter.budgetAmount) else "")
    }
    var budgetError by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Filters", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = TextDark)
                TextButton(onClick = {
                    selectedDietary = null
                    selectedCategories = DishCategory.entries.toMutableSet()
                    selectedPlatforms = mutableSetOf("GrabFood", "Foodpanda", "ShopeeFood")
                    budgetOn = false; budgetText = ""; budgetError = false
                }) {
                    Text("Reset All", color = PrimaryOrange, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }
            HorizontalDivider(color = DividerColor)
            Spacer(Modifier.height(16.dp))

            Text("Dietary Preference", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
            Spacer(Modifier.height(8.dp))
            listOf(
                null to "All",
                DietaryType.PURE_VEG to "Pure Veg 🌿",
                DietaryType.VEG_EGG to "Veg (may contain egg) 🥚",
                DietaryType.NON_VEG to "Non-Veg 🍗"
            ).forEach { (type, label) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(
                        selected = selectedDietary == type,
                        onClick = { selectedDietary = type },
                        colors = RadioButtonDefaults.colors(selectedColor = PrimaryOrange)
                    )
                    Text(label, fontSize = 14.sp, color = TextDark)
                }
            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = DividerColor)
            Spacer(Modifier.height(16.dp))

            Text("Category", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
            Spacer(Modifier.height(10.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                DishCategory.entries.forEach { cat ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = cat in selectedCategories,
                            onCheckedChange = { checked ->
                                val updated = selectedCategories.toMutableSet()
                                if (checked) updated.add(cat) else updated.remove(cat)
                                selectedCategories = updated
                            },
                            colors = CheckboxDefaults.colors(checkedColor = PrimaryOrange),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(cat.name.lowercase().replaceFirstChar { it.uppercase() }, fontSize = 14.sp, color = TextDark)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = DividerColor)
            Spacer(Modifier.height(16.dp))

            Text("Platform", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
            Spacer(Modifier.height(10.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf(
                    Triple("GrabFood", GrabGreen, "G"),
                    Triple("Foodpanda", FoodpandaPink, "P"),
                    Triple("ShopeeFood", ShopeeFoodOrange, "S")
                ).forEach { (name, color, letter) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = name in selectedPlatforms,
                            onCheckedChange = { checked ->
                                val updated = selectedPlatforms.toMutableSet()
                                if (checked) updated.add(name) else updated.remove(name)
                                selectedPlatforms = updated
                            },
                            colors = CheckboxDefaults.colors(checkedColor = PrimaryOrange),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(2.dp))
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .background(color, RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(letter, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(4.dp))
                        Text(name, fontSize = 14.sp, color = TextDark)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = DividerColor)
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Budget Mode", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
                    Text("Set a maximum budget per dish", fontSize = 12.sp, color = TextGrey)
                }
                Switch(
                    checked = budgetOn,
                    onCheckedChange = { budgetOn = it; if (!it) { budgetText = ""; budgetError = false } },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryOrange)
                )
            }

            if (budgetOn) {
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("10", "15", "20", "25").forEach { amt ->
                        val isSelected = budgetText == amt || budgetText == "$amt.00"
                        Button(
                            onClick = { budgetText = amt; budgetError = false },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) PrimaryOrange else PrimaryOrange.copy(alpha = 0.15f),
                                contentColor = if (isSelected) Color.White else PrimaryOrange
                            ),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            modifier = Modifier.weight(1f).height(40.dp)
                        ) {
                            Text("RM $amt", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = budgetText,
                    onValueChange = { budgetText = it; budgetError = false },
                    label = { Text("My Budget (RM)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = budgetError,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryOrange,
                        focusedLabelColor = PrimaryOrange
                    )
                )
                if (budgetError) {
                    Text("Please enter a valid budget amount", color = ErrorRed, fontSize = 11.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp))
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    val budget = budgetText.toDoubleOrNull()
                    if (budgetOn && (budget == null || budget <= 0)) {
                        budgetError = true
                    } else {
                        onApply(FilterState(
                            selectedDietary = selectedDietary,
                            selectedCategories = selectedCategories.toSet().ifEmpty { DishCategory.entries.toSet() },
                            selectedPlatforms = selectedPlatforms.toSet().ifEmpty { setOf("GrabFood", "Foodpanda", "ShopeeFood") },
                            budgetModeOn = budgetOn,
                            budgetAmount = if (budgetOn) budget ?: 0.0 else 0.0
                        ))
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
            ) {
                Text("Apply Filters", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(Modifier.height(28.dp))
        }
    }
}
