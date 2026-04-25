package com.smartbite.app.domain

object ValidationUtils {
    fun isValidEmail(email: String) =
        email.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun isValidPassword(password: String) = password.length >= Constants.MIN_PASSWORD_LENGTH

    fun isValidName(name: String) = name.trim().length >= Constants.MIN_NAME_LENGTH

    fun isValidBudget(budget: String) = budget.toDoubleOrNull()?.let { it > 0 } ?: false

    fun doPasswordsMatch(p1: String, p2: String) = p1 == p2
}
