package com.example.hakupivkirja.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.example.hakupivkirja.model.PistoState
import com.example.hakupivkirja.ui.theme.HakupäiväkirjaTheme
import kotlin.math.ceil

@Composable
fun UusiRata(pistojenMaara: Int) {
    println("Rata - pistojenMaara initial value: $pistojenMaara")
    var pistoStateList by remember(pistojenMaara) {
        mutableStateOf(List(pistojenMaara) { PistoState.Default })
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        reverseLayout = true,
        modifier = Modifier.fillMaxWidth()
    ) {
        //varmistetaan, että jakolaskun tulokseksi tulee kokonaisluku
        items(count = ceil(pistojenMaara / 2.0).toInt()) { rowIndex ->
            Row( //Rivi kahdelle vierekkäiselle pistolle
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val leftPistoIndex = rowIndex * 2+2 //vas. pistonro + apu, ettei pistoje tule liikaa
                val rightPistoIndex = leftPistoIndex -1 //oikean puoleinen pistonro

                if (leftPistoIndex <= pistojenMaara) { //renderöidään vain niin monta kuin pistoja
                    Row(
                        horizontalArrangement = Arrangement.End
                    ) { //vas piston sisältö
                        Text(leftPistoIndex.toString()) // pistonro
                        when (pistoStateList[leftPistoIndex -1]) {
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
                                        newList[leftPistoIndex -1] = PistoState.Tyhja
                                        pistoStateList = newList.toList()
                                    }) {
                                        Text(text = "Tyhjä")
                                    }
                                    TextButton(onClick = {
                                        val newList = pistoStateList.toMutableList()
                                        newList[leftPistoIndex-1] = PistoState.MM
                                        pistoStateList = newList.toList()
                                    }) {
                                        Text(text = "MM")
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
                if (rightPistoIndex <= pistojenMaara) {
                    Row { //oikeanpuoleisen piston sisältö
                        Text(text = rightPistoIndex.toString())
                        when (pistoStateList[rightPistoIndex-1]) {
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
                                        newList[rightPistoIndex-1] = PistoState.Tyhja
                                        pistoStateList = newList.toList()
                                    }) {
                                        Text(text = "Tyhjä")
                                    }
                                    TextButton(onClick = {
                                        val newList = pistoStateList.toMutableList()
                                        newList[rightPistoIndex-1] = PistoState.MM
                                        pistoStateList = newList.toList()
                                    }) {
                                        Text(text = "MM")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RataPreview() {
    HakupäiväkirjaTheme {
        UusiRata(pistojenMaara = 11
        )
    }
}