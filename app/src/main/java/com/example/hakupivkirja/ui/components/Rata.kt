package com.example.hakupivkirja.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
    val pistoStateList = remember(pistojenMaara) {
        mutableStateListOf<PistoState>().apply {
            repeat(pistojenMaara) { add(PistoState.Default) }
        }
    }
    var haukutList by remember { mutableStateOf(List(pistojenMaara) { "" }) }

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
                val leftPistoNumber = rowIndex * 2+2 //vas. pistonro + apu, ettei pistoja tule liikaa
                val leftPistoIndex = leftPistoNumber - 1 //vas puoleisen piston index

                val rightPistoNumber = leftPistoNumber -1 //oikean puoleinen pistonro
                val rightPistoIndex = rightPistoNumber - 1 //oikean puoleisen piston index

                if (leftPistoIndex >= 0 && leftPistoIndex < pistojenMaara) { //renderöidään vain niin monta kuin pistoja
                    Row(
                        horizontalArrangement = Arrangement.End
                    ) { //vas piston sisältö
                        Text(leftPistoNumber.toString()) // pistonro
                        when (pistoStateList[leftPistoIndex]) {
                            PistoState.MM -> {
                                Pisto(haukutList,leftPistoIndex) { }
                            }

                            PistoState.Tyhja -> {
                                Text(text = "Tyhjä")
                            }

                            PistoState.Default -> {
                                Row {
                                    TextButton(onClick = {
                                        pistoStateList[leftPistoIndex] = PistoState.Tyhja
                                        Log.d("UusiRata", "pistoStateList: $pistoStateList")
                                        Log.d("UusiRata", "leftPistoIndex: $leftPistoIndex")
                                        Log.d("UusiRata", "rowindex: $rowIndex")
                                    }) {
                                        Text(text = "Tyhjä")
                                    }
                                    TextButton(onClick = {
                                        pistoStateList[leftPistoIndex] = PistoState.MM
                                        Log.d("UusiRata", "pistoStateList: $pistoStateList")
                                        Log.d("UusiRata", "leftPistoIndex: $leftPistoIndex")
                                        Log.d("UusiRata", "rowindex: $rowIndex")
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
                if (rightPistoIndex >= 0 && rightPistoIndex < pistojenMaara) {
                    Row { //oikeanpuoleisen piston sisältö
                        Text(text = rightPistoNumber.toString())
                        when (pistoStateList[rightPistoIndex]) {
                            PistoState.MM -> {
                                Pisto(haukutList,leftPistoIndex) { }
                            }

                            PistoState.Tyhja -> {
                                Text(text = "Tyhjä")
                            }

                            PistoState.Default -> {
                                Row {
                                    TextButton(onClick = {
                                        pistoStateList[rightPistoIndex] = PistoState.Tyhja
                                        Log.d("UusiRata", "pistoStateList: $pistoStateList")
                                        Log.d("UusiRata", "rightPistoIndex: $rightPistoIndex")
                                        Log.d("UusiRata", "rowindex: $rowIndex")
                                    }) {
                                        Text(text = "Tyhjä")
                                    }
                                    TextButton(onClick = {
                                        pistoStateList[rightPistoIndex] = PistoState.MM
                                        Log.d("UusiRata", "pistoStateList: $pistoStateList")
                                        Log.d("UusiRata", "rightPistoIndex: $rightPistoIndex")
                                        Log.d("UusiRata", "rowindex: $rowIndex")
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
        UusiRata(pistojenMaara = 4
        )
    }
}