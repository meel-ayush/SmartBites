package com.smartbite.app.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import com.smartbite.app.AppState
import com.smartbite.app.domain.ValidationUtils
import com.smartbite.app.presentation.components.*
import com.smartbite.app.presentation.theme.*

class AuthViewModel : ViewModel() {
    var loginEmail by mutableStateOf("smartbite@demo.in")
    var loginPassword by mutableStateOf("demo123++")
    var loginEmailError by mutableStateOf<String?>(null)
    var loginPasswordError by mutableStateOf<String?>(null)
    var loginGeneralError by mutableStateOf<String?>(null)

    var regName by mutableStateOf("")
    var regEmail by mutableStateOf("")
    var regPassword by mutableStateOf("")
    var regConfirmPassword by mutableStateOf("")
    var regState by mutableStateOf("")
    var regNameError by mutableStateOf<String?>(null)
    var regEmailError by mutableStateOf<String?>(null)
    var regPasswordError by mutableStateOf<String?>(null)
    var regConfirmError by mutableStateOf<String?>(null)
    var regStateError by mutableStateOf<String?>(null)
    var regSuccess by mutableStateOf(false)

    fun attemptLogin(): Boolean {
        loginEmailError = null; loginPasswordError = null; loginGeneralError = null
        var valid = true
        if (!ValidationUtils.isValidEmail(loginEmail)) {
            loginEmailError = "Please enter a valid email address"; valid = false
        }
        if (loginPassword.isBlank()) {
            loginPasswordError = "Please enter your password"; valid = false
        }
        if (!valid) return false
        val ok = AppState.userRepository.login(loginEmail.trim(), loginPassword)
        if (!ok) loginGeneralError = "Invalid email or password."
        return ok
    }

    fun attemptRegister(): Boolean {
        regNameError = null; regEmailError = null; regPasswordError = null
        regConfirmError = null; regStateError = null
        var valid = true
        if (!ValidationUtils.isValidName(regName)) { regNameError = "Please enter your name (min 2 chars)"; valid = false }
        if (!ValidationUtils.isValidEmail(regEmail)) { regEmailError = "Please enter a valid email address"; valid = false }
        else if (AppState.userRepository.isEmailTaken(regEmail.trim())) { regEmailError = "Email already registered"; valid = false }
        if (!ValidationUtils.isValidPassword(regPassword)) { regPasswordError = "Password must be at least 6 characters"; valid = false }
        if (!ValidationUtils.doPasswordsMatch(regPassword, regConfirmPassword)) { regConfirmError = "Passwords do not match"; valid = false }
        if (regState.isBlank()) { regStateError = "Please select your state"; valid = false }
        if (!valid) return false
        return AppState.userRepository.register(regName.trim(), regEmail.trim(), regPassword, regState)
    }
}

@Composable
fun GuestNameScreen(onBack: () -> Unit, onSuccess: () -> Unit) {
    var name by remember { mutableStateOf(AppState.userRepository.guestName ?: "") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = onBack, modifier = Modifier.align(Alignment.Start)) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextDark)
        }
        Spacer(Modifier.height(32.dp))
        Text("👋", fontSize = 64.sp, textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        Text("What's your name?", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = TextDark, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text("We'll personalise your experience just for you", fontSize = 14.sp, color = TextGrey, textAlign = TextAlign.Center)
        Spacer(Modifier.height(32.dp))
        SmartBiteTextField(
            value = name,
            onValueChange = { name = it; error = null },
            label = "Your Name",
            leadingIcon = Icons.Default.Person,
            placeholder = "Enter your name...",
            errorMessage = error
        )
        Spacer(Modifier.height(24.dp))
        OrangeFilledButton(text = "Let's Go! →", onClick = {
            if (!ValidationUtils.isValidName(name)) {
                error = "Please enter your name (minimum 2 characters)"
            } else {
                AppState.userRepository.guestName = name.trim()
                onSuccess()
            }
        })
        Spacer(Modifier.height(12.dp))
        Text("You can always login later from your profile", fontSize = 12.sp, color = TextGrey, textAlign = TextAlign.Center)
    }
}

@Composable
fun LoginScreen(
    fromRegister: Boolean,
    onBack: () -> Unit,
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    onGuestClick: () -> Unit
) {
    val vm: AuthViewModel = viewModel()

    LaunchedEffect(fromRegister) {
        if (fromRegister) {
            vm.loginEmail = ""
            vm.loginPassword = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (fromRegister) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF4CAF50).copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "✅ Registration successful! Please login with your new account.",
                    color = Color(0xFF2E7D32),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(Modifier.height(12.dp))
        }
        IconButton(onClick = onBack, modifier = Modifier.align(Alignment.Start)) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextDark)
        }
        Spacer(Modifier.height(16.dp))
        Text("🍴", fontSize = 36.sp)
        Text("SmartBite", color = PrimaryOrange, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(Modifier.height(16.dp))
        Text("Welcome Back! 👋", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = TextDark)
        Text("Login to continue", fontSize = 14.sp, color = TextGrey)
        Spacer(Modifier.height(32.dp))
        SmartBiteTextField(
            value = vm.loginEmail,
            onValueChange = { vm.loginEmail = it; vm.loginEmailError = null; vm.loginGeneralError = null },
            label = "Email Address",
            leadingIcon = Icons.Default.Email,
            keyboardType = KeyboardType.Email,
            errorMessage = vm.loginEmailError
        )
        Spacer(Modifier.height(12.dp))
        SmartBiteTextField(
            value = vm.loginPassword,
            onValueChange = { vm.loginPassword = it; vm.loginPasswordError = null; vm.loginGeneralError = null },
            label = "Password",
            leadingIcon = Icons.Default.Lock,
            isPassword = true,
            errorMessage = vm.loginPasswordError
        )
        if (vm.loginGeneralError != null) {
            Spacer(Modifier.height(8.dp))
            Text(vm.loginGeneralError!!, color = ErrorRed, fontSize = 13.sp, textAlign = TextAlign.Center)
        }
        Spacer(Modifier.height(24.dp))
        OrangeFilledButton(text = "Login", onClick = {
            if (vm.attemptLogin()) onLoginSuccess()
        })
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = DividerColor)
            Text("  OR  ", fontSize = 12.sp, color = TextGrey)
            HorizontalDivider(modifier = Modifier.weight(1f), color = DividerColor)
        }
        Spacer(Modifier.height(12.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Don't have an account? ", fontSize = 14.sp, color = TextGrey)
            TextButton(onClick = onRegisterClick, contentPadding = PaddingValues(0.dp)) {
                Text("Register Now", color = PrimaryOrange, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }
        TextButton(onClick = onGuestClick) {
            Text("Continue as Guest →", color = PrimaryOrange, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
    }
}

val malaysianStates = listOf(
    "Johor", "Kedah", "Kelantan", "Melaka", "Negeri Sembilan", "Pahang",
    "Perak", "Perlis", "Pulau Pinang", "Sabah", "Sarawak", "Selangor",
    "Terengganu", "Kuala Lumpur", "Labuan", "Putrajaya"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onBack: () -> Unit, onSuccess: () -> Unit, onLoginClick: () -> Unit) {
    val vm: AuthViewModel = viewModel()
    var stateDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(vm.regSuccess) {
        if (vm.regSuccess) {
            delay(1500)
            onSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = onBack, modifier = Modifier.align(Alignment.Start)) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextDark)
        }
        Spacer(Modifier.height(8.dp))
        Text("Create Account", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = TextDark)
        Text("Join SmartBite today", fontSize = 14.sp, color = TextGrey)
        Spacer(Modifier.height(24.dp))

        if (vm.regSuccess) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF4CAF50).copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "✅ Account created successfully! Redirecting to login...",
                    color = Color(0xFF2E7D32),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(Modifier.height(12.dp))
        }

        SmartBiteTextField(vm.regName, { vm.regName = it; vm.regNameError = null }, "Full Name", Icons.Default.Person, errorMessage = vm.regNameError)
        Spacer(Modifier.height(12.dp))
        SmartBiteTextField(vm.regEmail, { vm.regEmail = it; vm.regEmailError = null }, "Email Address", Icons.Default.Email, keyboardType = KeyboardType.Email, errorMessage = vm.regEmailError)
        Spacer(Modifier.height(12.dp))
        SmartBiteTextField(vm.regPassword, { vm.regPassword = it; vm.regPasswordError = null }, "Password", Icons.Default.Lock, isPassword = true, errorMessage = vm.regPasswordError)
        Spacer(Modifier.height(12.dp))
        SmartBiteTextField(vm.regConfirmPassword, { vm.regConfirmPassword = it; vm.regConfirmError = null }, "Confirm Password", Icons.Default.Lock, isPassword = true, errorMessage = vm.regConfirmError)
        Spacer(Modifier.height(12.dp))

        ExposedDropdownMenuBox(
            expanded = stateDropdownExpanded,
            onExpandedChange = { stateDropdownExpanded = it }
        ) {
            OutlinedTextField(
                value = vm.regState,
                onValueChange = {},
                readOnly = true,
                label = { Text("State") },
                leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = TextGrey) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(stateDropdownExpanded) },
                isError = vm.regStateError != null,
                supportingText = vm.regStateError?.let { { Text(it, color = ErrorRed, fontSize = 11.sp) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryOrange)
            )
            ExposedDropdownMenu(expanded = stateDropdownExpanded, onDismissRequest = { stateDropdownExpanded = false }) {
                malaysianStates.forEach { state ->
                    DropdownMenuItem(
                        text = { Text(state) },
                        onClick = { vm.regState = state; vm.regStateError = null; stateDropdownExpanded = false }
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        OrangeFilledButton(text = "Create Account", onClick = {
            if (vm.attemptRegister()) vm.regSuccess = true
        })
        Spacer(Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Already have an account? ", fontSize = 14.sp, color = TextGrey)
            TextButton(onClick = onLoginClick, contentPadding = PaddingValues(0.dp)) {
                Text("Login", color = PrimaryOrange, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}
