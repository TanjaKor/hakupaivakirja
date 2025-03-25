package com.example.hakupivkirja.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hakupivkirja.ui.screens.HomeScreen
import com.example.hakupivkirja.ui.theme.HakupäiväkirjaTheme

@Composable
fun Rata(pistojenMaara: Int) {
    val pistoIndex = List(pistojenMaara) { "${it + 1}" }
    var haukutList by remember { mutableStateOf(List(pistojenMaara *2 ) { "" }) }
    var

    LaunchedEffect(pistojenMaara) {
        haukutList = List(pistojenMaara * 2) { "" }
    }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
    //mietittävä vielä miten pistot numeroidaan -> oisko järkevämpi tehdä yksi lazycolumn
    //johon rivissä kaksi pistoa vierekkäin?
        //vasemmat pistot
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
                    Row {
                        TextButton(onClick = { /*TODO*/ }) {
                            Text(text = "Tyhjä")
                        }
                        TextButton(onClick = { /*TODO*/ }) {
                            Text(text = "MM")
                        }
                    }
//                    PistoVasen(
//                        haukutList = haukutList,
//                        index = index,
//                        pistojenMaara = pistojenMaara,
//                        onHaukutListChange = { newList -> haukutList = newList}
//                    )
                }
            }
        }
        //oikeat pistot
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
                    PistoOikea(
                        haukutList = haukutList,
                        index = index,
                        pistojenMaara = pistojenMaara,
                        onHaukutListChange = { newList -> haukutList = newList}
                    )
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