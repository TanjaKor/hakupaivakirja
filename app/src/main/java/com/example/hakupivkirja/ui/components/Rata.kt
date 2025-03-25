package com.example.hakupivkirja.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hakupivkirja.ui.screens.HomeScreen
import com.example.hakupivkirja.ui.theme.HakupäiväkirjaTheme

@Composable
fun Rata(pistojenMaara: Int) {
    val pistoIndex = List(pistojenMaara) { "${it + 1}" }
    var haukutList by remember { mutableStateOf(List(pistojenMaara *2 ) { "" }) }

    LaunchedEffect(pistojenMaara) {
        haukutList = List(pistojenMaara * 2) { "" }
    }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            itemsIndexed(pistoIndex) { index, _ ->
                val actualPistoNumber = pistojenMaara * 2 - index
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = "$actualPistoNumber", fontSize = 14.sp)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            AvutDropdown()
                            PalkkaDropdown()
                        }
                        OutlinedTextField(
                            value = haukutList.getOrElse(index + pistojenMaara) { "" },
                            onValueChange = { newValue ->
                                if ((index+pistojenMaara) < haukutList.size) {
                                    val newList = haukutList.toMutableList()
                                    newList[index + pistojenMaara] = newValue
                                    haukutList = newList.toList()
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
            }
        }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            itemsIndexed(pistoIndex) { index, _ ->
                val actualPistoNumber = pistojenMaara - index
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = "$actualPistoNumber", fontSize = 14.sp)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            AvutDropdown()
                            PalkkaDropdown()
                        }
                        OutlinedTextField(
                            value = haukutList.getOrElse(pistojenMaara - 1 - index) { "" },
                            onValueChange = { newValue ->
                                if ((pistojenMaara - 1 - index) < haukutList.size) {
                                    val newList = haukutList.toMutableList()
                                    newList[pistojenMaara - 1 - index] = newValue
                                    haukutList = newList.toList()
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
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HakupäiväkirjaTheme {
        HomeScreen()
    }
}