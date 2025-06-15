package com.example.hakupivkirja.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hakupivkirja.model.PistoUiState

@Composable
fun Pisto(pistoUiState: PistoUiState,
          onHaukutChange: (String) -> Unit,
          onAvutChange: (String) -> Unit,
          onPalkkaChange: (String) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.weight(1f).height(100.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Top
        ) {
            AvutDropdown(selectedText = pistoUiState.avut ?: "",
                onSelectedValueChange = { newValue ->
                    onAvutChange(newValue)
                }
            )
            PalkkaDropdown(
                selectedText = pistoUiState.palkka ?: "",
                onSelectedValueChange = { newValue ->
                    onPalkkaChange(newValue)
                }
            )
        }
        OutlinedTextField(
            value = pistoUiState.haukut ?: "",
            onValueChange = {newValue ->
                if (newValue.toIntOrNull() != null || newValue.isEmpty()) {
                    onHaukutChange(newValue)
                }
            },
            keyboardOptions = KeyboardOptions( // Keep only this one
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            label = { Text("Hau", fontSize = 11.sp) },
            modifier = Modifier.width(80.dp).height(56.dp),
        )
    }
}

