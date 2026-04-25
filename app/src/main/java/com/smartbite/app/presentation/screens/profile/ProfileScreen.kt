package com.smartbite.app.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartbite.app.AppState
import com.smartbite.app.domain.ValidationUtils
import com.smartbite.app.presentation.components.OrangeFilledButton
import com.smartbite.app.presentation.components.OrangeOutlinedButton
import com.smartbite.app.presentation.components.SmartBiteTextField
import com.smartbite.app.presentation.screens.auth.malaysianStates
import com.smartbite.app.presentation.theme.*

@Composable
fun ProfileScreen(onLoginClick: () -> Unit, onRegisterClick: () -> Unit, onLogout: () -> Unit) {
    val isLoggedIn = AppState.userRepository.isLoggedIn
    var refresh by remember { mutableStateOf(0) }

    if (isLoggedIn) {
        LoggedInProfile(onLogout = onLogout, refresh = refresh, onRefresh = { refresh++ })
    } else {
        GuestProfile(onLoginClick = onLoginClick, onRegisterClick = onRegisterClick, refresh = refresh, onRefresh = { refresh++ })
    }
}

@Composable
fun LoggedInProfile(onLogout: () -> Unit, refresh: Int, onRefresh: () -> Unit) {
    val userState = remember(refresh) { mutableStateOf(AppState.userRepository.currentUser) }
    val user = userState.value ?: return
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showEditName by remember { mutableStateOf(false) }
    var showChangePwd by remember { mutableStateOf(false) }
    var showChangeState by remember { mutableStateOf(false) }

    val initials = user.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("")

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(onClick = { AppState.userRepository.logout(); onLogout() }) {
                    Text("Logout", color = ErrorRed)
                }
            },
            dismissButton = { TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") } }
        )
    }
    if (showEditName) EditNameSheet({ showEditName = false; onRefresh() })
    if (showChangePwd) ChangePasswordSheet { showChangePwd = false }
    if (showChangeState) ChangeStateSheet({ showChangeState = false; onRefresh() })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Brush.verticalGradient(listOf(PrimaryOrange, DarkOrange))),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(initials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 26.sp)
                }
                Spacer(Modifier.height(8.dp))
                Text(user.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(user.email, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                InfoRow(Icons.Default.LocationOn, "Location", user.state)
                InfoRow(Icons.Default.DateRange, "Member Since", user.joinDate)
                InfoRow(Icons.Default.Favorite, "Saved Dishes", "${AppState.favouritesRepository.ids.size} dishes")

                Spacer(Modifier.height(20.dp))
                Text("Account Settings", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                Spacer(Modifier.height(8.dp))
                SettingsItem(Icons.Default.Edit, "Edit Name") { showEditName = true }
                SettingsItem(Icons.Default.Lock, "Change Password") { showChangePwd = true }
                SettingsItem(Icons.Default.LocationOn, "Change Location") { showChangeState = true }

                Spacer(Modifier.height(24.dp))
                OutlinedButton(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, ErrorRed),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed)
                ) {
                    Text("Logout", fontWeight = FontWeight.SemiBold)
                }

                Spacer(Modifier.height(16.dp))
                Text("SmartBite v1.0", fontSize = 11.sp, color = TextGrey, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
fun GuestProfile(onLoginClick: () -> Unit, onRegisterClick: () -> Unit, refresh: Int, onRefresh: () -> Unit) {
    val guestName = remember(refresh) { AppState.userRepository.guestName ?: "Guest" }
    val initials = guestName.take(1).uppercase()
    var showEditName by remember { mutableStateOf(false) }

    if (showEditName) {
        GuestEditNameSheet({ showEditName = false; onRefresh() })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Brush.verticalGradient(listOf(Color(0xFF9E9E9E), Color(0xFF757575)))),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(72.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) { Text(initials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 28.sp) }
                Spacer(Modifier.height(8.dp))
                Text(guestName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(Modifier.height(4.dp))
                Surface(color = PrimaryOrange, shape = RoundedCornerShape(20.dp)) {
                    Text("Guest Mode", color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "You're browsing as a guest. Login to save favourites, view order history, and more.",
                    fontSize = 13.sp, color = TextGrey, textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(20.dp))
                OrangeFilledButton(text = "Login to Your Account", onClick = onLoginClick)
                Spacer(Modifier.height(10.dp))
                OrangeOutlinedButton(text = "Create an Account", onClick = onRegisterClick)
                Spacer(Modifier.height(24.dp))
                Text("Guest Settings", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                Spacer(Modifier.height(8.dp))
                SettingsItem(Icons.Default.Edit, "Edit Name") { showEditName = true }
                Spacer(Modifier.height(16.dp))
                Text("SmartBite v1.0", fontSize = 11.sp, color = TextGrey, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text(label, fontSize = 14.sp, color = TextGrey, modifier = Modifier.weight(1f))
        Text(value, fontSize = 14.sp, color = TextDark, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SettingsItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Surface(onClick = onClick, color = Color.Transparent, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Text(label, fontSize = 14.sp, color = TextDark, modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextGrey)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditNameSheet(onDone: () -> Unit) {
    var name by remember { mutableStateOf(AppState.userRepository.currentUser?.name ?: "") }
    var error by remember { mutableStateOf<String?>(null) }
    ModalBottomSheet(onDismissRequest = onDone, containerColor = Color.White) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Edit Name", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(16.dp))
            SmartBiteTextField(name, { name = it; error = null }, "Full Name", Icons.Default.Person, errorMessage = error)
            Spacer(Modifier.height(16.dp))
            Button(onClick = {
                if (!ValidationUtils.isValidName(name)) error = "Name must be at least 2 characters"
                else { AppState.userRepository.updateName(name.trim()); onDone() }
            }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange), shape = RoundedCornerShape(12.dp)) {
                Text("Save", fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChangePasswordSheet(onDone: () -> Unit) {
    var current by remember { mutableStateOf("") }
    var newPwd by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    ModalBottomSheet(onDismissRequest = onDone, containerColor = Color.White) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Change Password", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(16.dp))
            SmartBiteTextField(current, { current = it; error = null }, "Current Password", Icons.Default.Lock, isPassword = true)
            Spacer(Modifier.height(8.dp))
            SmartBiteTextField(newPwd, { newPwd = it; error = null }, "New Password", Icons.Default.Lock, isPassword = true)
            Spacer(Modifier.height(8.dp))
            SmartBiteTextField(confirm, { confirm = it; error = null }, "Confirm Password", Icons.Default.Lock, isPassword = true, errorMessage = error)
            Spacer(Modifier.height(16.dp))
            Button(onClick = {
                val user = AppState.userRepository.currentUser
                when {
                    user?.password != current -> error = "Current password is incorrect"
                    !ValidationUtils.isValidPassword(newPwd) -> error = "New password must be at least 6 characters"
                    !ValidationUtils.doPasswordsMatch(newPwd, confirm) -> error = "Passwords do not match"
                    else -> { AppState.userRepository.updatePassword(newPwd); onDone() }
                }
            }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange), shape = RoundedCornerShape(12.dp)) {
                Text("Update Password", fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChangeStateSheet(onDone: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(AppState.userRepository.currentUser?.state ?: "") }
    ModalBottomSheet(onDismissRequest = onDone, containerColor = Color.White) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Change Location", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(16.dp))
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = selected,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("State") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryOrange)
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    malaysianStates.forEach { state ->
                        DropdownMenuItem(text = { Text(state) }, onClick = { selected = state; expanded = false })
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Button(onClick = { if (selected.isNotBlank()) { AppState.userRepository.updateState(selected); onDone() } },
                modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange), shape = RoundedCornerShape(12.dp)) {
                Text("Save Location", fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GuestEditNameSheet(onDone: () -> Unit) {
    var name by remember { mutableStateOf(AppState.userRepository.guestName ?: "") }
    var error by remember { mutableStateOf<String?>(null) }
    ModalBottomSheet(onDismissRequest = onDone, containerColor = Color.White) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Edit Name", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(16.dp))
            SmartBiteTextField(name, { name = it; error = null }, "Your Name", Icons.Default.Person, errorMessage = error)
            Spacer(Modifier.height(16.dp))
            Button(onClick = {
                if (!ValidationUtils.isValidName(name)) error = "Name must be at least 2 characters"
                else { AppState.userRepository.guestName = name.trim(); onDone() }
            }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange), shape = RoundedCornerShape(12.dp)) {
                Text("Save", fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
