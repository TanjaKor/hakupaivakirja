package com.example.hakupivkirja.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.example.hakupivkirja.ui.components.Rata
import com.example.hakupivkirja.ui.components.Valintarivi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    var pistojenMaara by remember { mutableIntStateOf(1)  } // State managed at the parent level

    Column {
        Valintarivi(selectedPistot = pistojenMaara,
            onSelectedPistotChange = { pistojenMaara = it }  )
        HorizontalDivider(thickness = 2.dp)
        Rata(pistojenMaara)
    }
}
