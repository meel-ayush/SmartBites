package com.smartbite.app.data.model

data class NutritionInfo(val calories: Int, val protein: String, val carbs: String, val fat: String)

enum class DietaryType { PURE_VEG, VEG_EGG, NON_VEG }
enum class DishCategory { RICE, NOODLES, SNACKS, DRINKS, DESSERTS }

data class Dish(
    val id: Int,
    val name: String,
    val category: DishCategory,
    val dietary: DietaryType,
    val basePrice: Double,
    val description: String,
    val nutritionInfo: NutritionInfo,
    val emoji: String = "🍽️",
    val imageRes: Int = 0
)

data class PlatformPrice(
    val platformName: String,
    val foodPrice: Double,
    val deliveryFee: Double,
    val serviceTax: Double,
    val grandTotal: Double,
    val estimatedTime: String,
    val promoText: String,
    val isBestDeal: Boolean,
    val websiteUrl: String,
    val badgeText: String
)

data class User(
    val name: String,
    val email: String,
    val password: String,
    val state: String,
    val joinDate: String = "January 2026"
)

data class FilterState(
    val selectedDietary: DietaryType? = null,
    val selectedCategories: Set<DishCategory> = DishCategory.values().toSet(),
    val selectedPlatforms: Set<String> = setOf("GrabFood", "Foodpanda", "ShopeeFood"),
    val budgetModeOn: Boolean = false,
    val budgetAmount: Double = 0.0
)
