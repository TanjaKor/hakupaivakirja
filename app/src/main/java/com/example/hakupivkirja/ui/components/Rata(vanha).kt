package com.example.hakupivkirja.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hakupivkirja.model.PistoState
import com.example.hakupivkirja.ui.screens.HomeScreen
import com.example.hakupivkirja.ui.theme.HakupäiväkirjaTheme
import kotlin.math.ceil

@Composable
fun Rata(pistojenMaara: Int) {
    println("Rata - pistojenMaara initial value: $pistojenMaara")
//    var haukutList by remember { mutableStateOf(List(pistojenMaara) { "" }) }
    var pistoStateList by remember { mutableStateOf(List(pistojenMaara) { PistoState.Default }) }
    // Create a list of numbers in the desired order
    val numbers = (0 until pistojenMaara).toList()

//    LaunchedEffect(pistojenMaara) {
//        println("Rata - pistojenMaara changed to: $pistojenMaara")
//        println("Rata - old haukutList size: ${haukutList.size}")
//
//        val newHaukutList = MutableList(pistojenMaara) { index ->
//            haukutList.getOrElse(index) { "" }
//        }
//        haukutList = newHaukutList
//
//        println("Rata - new haukutList size: ${haukutList.size}")
//
//        val newPistoStateList = MutableList(pistojenMaara) { index ->
//            pistoStateList.getOrElse(index) { PistoState.Default }
//        }
//        pistoStateList = newPistoStateList
//    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        reverseLayout = true,
        modifier = Modifier.fillMaxWidth()
    ) {
        //varmistetaan, että jakolaskun tulokseksi tulee kokonaisluku
        items(count = ceil(pistojenMaara / 2.0).toInt()) { index ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Vasen Pisto
                Column {
                    if (index * 2 +1 < numbers.size) {
                        val pistoIndex = numbers[index * 2 +2] // Get the number
                        val listIndex = index + 1
                        Text(text = "$pistoIndex", fontSize = 14.sp)
                        when (pistoStateList[listIndex]) { //
                            PistoState.MM -> {
                                Text("MM")
                            }
                            PistoState.Tyhja -> {
                                Text(text = " ")
                            }
                            PistoState.Default -> {
                                Row {
                                    TextButton(onClick = {
                                        val newList = pistoStateList.toMutableList()
                                        newList[listIndex] = PistoState.Tyhja
                                        pistoStateList = newList.toList()
                                    }) {
                                        Text(text = "Tyhjä")
                                    }
                                    TextButton(onClick = {
                                        val newList = pistoStateList.toMutableList()
                                        newList[listIndex] = PistoState.MM
                                        pistoStateList = newList.toList()
                                    }) {
                                        Text(text = "MM")
                                    }
                                }
                            }
                        }
                    } else {
                        Text(text = " ")
                    }
                }
                // Oikea Pisto
                Column {
                    if (index * 2 < numbers.size) {
                        val pistoIndex = numbers[index * 2+1] // Get the number
                        Text(text = "$pistoIndex", fontSize = 14.sp)
                        when (pistoStateList[index]) { // Use the number as index
                            PistoState.MM -> {
                                Text("MM")
                            }
                            PistoState.Tyhja -> {
                                Text(text = "")
                            }
                            PistoState.Default -> {
                                Row {
                                    TextButton(onClick = {
                                        val newList = pistoStateList.toMutableList()
                                        newList[index] = PistoState.Tyhja
                                        pistoStateList = newList.toList()
                                    }) {
                                        Text(text = "Tyhjä")
                                    }
                                    TextButton(onClick = {
                                        val newList = pistoStateList.toMutableList()
                                        newList[index] = PistoState.MM
                                        pistoStateList = newList.toList()
                                    }) {
                                        Text(text = "MM")
                                    }
                                }
                            }
                        }
                    } else {
                        Text(text = "")
                    }
                }


            }
        }
    }
}


//                // Vasen Pisto
//                Column {
//                    val vasenIndex = index * 2 +2
//                    if (index * 2 < numbers.size ) {
//                        val pistoIndex = numbers[index * 2]
//                        Text(text = "$pistoIndex", fontSize = 14.sp)
//                        when (pistoStateList[pistoIndex]) {
//                            PistoState.MM -> {
//                                Text("MM")
//                            }
//                            PistoState.Tyhja -> {
//                                Text(text = " ")
//                            }
//                            PistoState.Default -> {
//                                Row {
//                                    TextButton(onClick = {
//                                        val newList = pistoStateList.toMutableList()
//                                        newList[pistoIndex] = PistoState.Tyhja
//                                        pistoStateList = newList.toList()
//                                    }) {
//                                        Text(text = "Tyhjä")
//                                    }
//                                    TextButton(onClick = {
//                                        val newList = pistoStateList.toMutableList()
//                                        newList[pistoIndex] = PistoState.MM
//                                        pistoStateList = newList.toList()
//                                    }) {
//                                        Text(text = "MM")
//                                    }
//                                }
//                            }
//                        }
//                    } else {
//                        Text(text = " ")
//                    }
//                }
//
//                // Oikea Pisto
//                Column {
//                    val oikeaIndex = index * 2 +1
//                    if (index * 2 + 1 < numbers.size) {
//                        val pistoIndex = numbers[index * 2 + 1]
//                        Text(text = "$pistoIndex", fontSize = 14.sp)
//                        when (pistoStateList[pistoIndex]) {
//                            PistoState.MM -> {
//                                Text("MM")
//                            }
//                            PistoState.Tyhja -> {
//                                Text(text = "")
//                            }
//                            PistoState.Default -> {
//                                Row {
//                                    TextButton(onClick = {
//                                        val newList = pistoStateList.toMutableList()
//                                        newList[pistoIndex] = PistoState.Tyhja
//                                        pistoStateList = newList.toList()
//                                    }) {
//                                        Text(text = "Tyhjä")
//                                    }
//                                    TextButton(onClick = {
//                                        val newList = pistoStateList.toMutableList()
//                                        newList[pistoIndex] = PistoState.MM
//                                        pistoStateList = newList.toList()
//                                    }) {
//                                        Text(text = "MM")
//                                    }
//                                }
//                            }
//                        }
//                    } else {
//                        Text(text = "")
//                    }
//                }
//            }
//        }
//    }
//}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HakupäiväkirjaTheme {
        HomeScreen()
    }
}