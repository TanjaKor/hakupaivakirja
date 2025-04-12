package com.example.hakupivkirja.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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

@Composable
fun Pisto(
    haukutList: List<String>,
    index: Int,
    onHaukutListChange: (List<String>) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            AvutDropdown()
            PalkkaDropdown()
        }
        OutlinedTextField(
            value = haukutList.getOrElse(index) { "" },
            onValueChange = { newValue ->
                if (index < haukutList.size) {
                    val newList = haukutList.toMutableList()
                    newList[index] = newValue
                    onHaukutListChange(newList.toList())
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            label = { Text("Haukut", fontSize = 11.sp) },
            modifier = Modifier.width(75.dp)
        )
    }
}

@Composable
fun PistoOikea(
    haukutList: List<String>,
    index: Int,
    pistojenMaara: Int,
    onHaukutListChange: (List<String>) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            AvutDropdown()
            PalkkaDropdown()
        }
        OutlinedTextField(
            value = haukutList.getOrElse(index) { "" },
            onValueChange = { newValue ->
                if (index < haukutList.size) {
                    val newList = haukutList.toMutableList()
                    newList[index] = newValue
                    onHaukutListChange(newList.toList())
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            label = { Text("Haukut", fontSize = 11.sp) },
            modifier = Modifier.width(75.dp),
        )
    }
}

