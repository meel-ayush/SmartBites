package com.smartbite.app.data.repository

import android.content.Context
import com.smartbite.app.data.model.User
import com.smartbite.app.domain.Constants

class UserRepository(context: Context) {
    private val prefs = context.getSharedPreferences("SmartBitePrefs", Context.MODE_PRIVATE)

    private val users = mutableListOf(
        User("Ayush Meel", "smartbite@demo.in", "demo123++", "Kuala Lumpur")
    )

    var currentUser: User? = null
        private set

    val isLoggedIn get() = currentUser != null

    var guestName: String?
        get() = prefs.getString(Constants.GUEST_NAME_KEY, null)
        set(value) { prefs.edit().putString(Constants.GUEST_NAME_KEY, value).apply() }

    fun login(email: String, password: String): Boolean {
        val match = users.find { it.email.equals(email, ignoreCase = true) && it.password == password }
        currentUser = match
        return match != null
    }

    fun register(name: String, email: String, password: String, state: String): Boolean {
        if (users.any { it.email.equals(email, ignoreCase = true) }) return false
        users.add(User(name, email, password, state))
        return true
    }

    fun isEmailTaken(email: String) = users.any { it.email.equals(email, ignoreCase = true) }

    fun logout() { currentUser = null }

    fun updateName(name: String) = updateUser { it.copy(name = name) }
    fun updatePassword(pwd: String) = updateUser { it.copy(password = pwd) }
    fun updateState(state: String) = updateUser { it.copy(state = state) }

    private fun updateUser(transform: (User) -> User) {
        currentUser?.let { u ->
            val updated = transform(u)
            val i = users.indexOfFirst { it.email == u.email }
            if (i >= 0) users[i] = updated
            currentUser = updated
        }
    }
}
