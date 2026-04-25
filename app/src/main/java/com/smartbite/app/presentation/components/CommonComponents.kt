package com.smartbite.app.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartbite.app.presentation.theme.*

@Composable
fun SmartBiteTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    errorMessage: String? = null,
    placeholder: String = ""
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, fontSize = 13.sp) },
            placeholder = { Text(placeholder, fontSize = 13.sp, color = TextGrey) },
            leadingIcon = { Icon(leadingIcon, contentDescription = null, tint = if (errorMessage != null) ErrorRed else TextGrey) },
            trailingIcon = {
                if (isPassword) {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = TextGrey
                        )
                    }
                } else if (value.isNotEmpty()) {
                    IconButton(onClick = { onValueChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear text", tint = TextGrey)
                    }
                }
            },
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            isError = errorMessage != null,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryOrange,
                unfocusedBorderColor = DividerColor,
                errorBorderColor = ErrorRed,
                focusedLabelColor = PrimaryOrange
            ),
            singleLine = true
        )
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = ErrorRed,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 16.dp, top = 2.dp)
            )
        }
    }
}

@Composable
fun OrangeFilledButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryOrange,
            contentColor = Color.White,
            disabledContainerColor = PrimaryOrange.copy(alpha = 0.5f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
        } else {
            Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun OrangeOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryOrange),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = PrimaryOrange
        )
    ) {
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}
