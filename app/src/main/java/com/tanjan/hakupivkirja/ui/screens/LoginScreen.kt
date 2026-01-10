package com.tanjan.hakupivkirja.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tanjan.hakupivkirja.ui.theme.primaryLight
import com.tanjan.hakupivkirja.ui.viewmodels.AuthViewModel

@Composable
fun LoginScreen(authViewModel: AuthViewModel) {
    val uiState by authViewModel.uiState.collectAsState()
    
    var email by remember { mutableStateOf("") }
    var salasana by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isRegisterMode by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isRegisterMode) "Luo tunnus" else "Kirjaudu sisään",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = primaryLight,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Sähköposti
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Sähköposti") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Salasana
        OutlinedTextField(
            value = salasana,
            onValueChange = { salasana = it },
            label = { Text("Salasana") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )

        // Virheilmoitus
        uiState.error?.let {
            Text(
                text = it,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Kirjautumispainike
        Button(
            onClick = {
                if (isRegisterMode) {
                    authViewModel.signUp(email, salasana)
                } else {
                    authViewModel.signIn(email, salasana)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryLight),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(if (isRegisterMode) "Rekisteröidy" else "Kirjaudu")
            }
        }

        // Vaihda moodia
        TextButton(
            onClick = { 
                isRegisterMode = !isRegisterMode
                authViewModel.clearError()
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(
                if (isRegisterMode) "Onko sinulla jo tunnus? Kirjaudu" 
                else "Eikö tunnusta? Luo uusi tästä",
                color = primaryLight
            )
        }
    }
}
