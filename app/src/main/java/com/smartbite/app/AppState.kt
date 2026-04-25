package com.smartbite.app

import android.content.Context
import com.smartbite.app.data.repository.DishRepository
import com.smartbite.app.data.repository.FavouritesRepository
import com.smartbite.app.data.repository.UserRepository

object AppState {
    lateinit var userRepository: UserRepository
    val dishRepository = DishRepository()
    val favouritesRepository = FavouritesRepository()

    fun init(context: Context) {
        userRepository = UserRepository(context)
    }
}
