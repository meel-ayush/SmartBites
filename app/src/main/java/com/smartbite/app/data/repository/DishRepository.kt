package com.smartbite.app.data.repository

import com.smartbite.app.data.local.DishDataSource
import com.smartbite.app.data.model.*
import com.smartbite.app.domain.PriceCalculator

class DishRepository {
    val allDishes: List<Dish> = DishDataSource.allDishes

    fun searchDishes(query: String) =
        allDishes.filter { it.name.contains(query, ignoreCase = true) }

    fun getDishById(id: Int) = allDishes.find { it.id == id }

    fun filterDishes(filter: FilterState): List<Dish> {
        var result = allDishes
        filter.selectedDietary?.let { result = result.filter { d -> d.dietary == it } }
        result = result.filter { it.category in filter.selectedCategories }
        if (filter.budgetModeOn && filter.budgetAmount > 0) {
            val platforms = filter.selectedPlatforms
            result = result.filter { dish ->
                PriceCalculator.getAllPrices(dish.basePrice)
                    .filter { it.platformName in platforms }
                    .any { it.grandTotal <= filter.budgetAmount }
            }
        }
        return result
    }

    fun getPricesForDish(dish: Dish) = PriceCalculator.getAllPrices(dish.basePrice)
}

class FavouritesRepository {
    private val saved = mutableSetOf<Int>()
    fun add(id: Int) { saved.add(id) }
    fun remove(id: Int) { saved.remove(id) }
    fun contains(id: Int) = id in saved
    fun getAll(dishes: List<Dish>) = dishes.filter { it.id in saved }
    val ids: Set<Int> get() = saved.toSet()
}
