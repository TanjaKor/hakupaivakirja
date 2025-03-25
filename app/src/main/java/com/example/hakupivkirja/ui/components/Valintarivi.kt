package com.example.hakupivkirja.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Valintarivi(selectedPistot: Int, onSelectedPistotChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var pistotMax by remember { mutableStateOf(3) }
    var text by remember { mutableStateOf("Treenisuunnitelma lyhyesti") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            DatePickerFieldToModal()
            RadanPituusDropdown(onMaxPistotChange = { pistotMax = it })
            PistojenMaaraDropdown(maxPistot = pistotMax,  // Pass maxPistot to PistojenMaaraDropdown
                onSelectedPistotChange = { onSelectedPistotChange(it) })
            Button(
                onClick = { /*TODO*/ },
            ) {
                Text(text = "Kirjaa")
            }

        }
        OutlinedTextField(
            value = text,
            onValueChange = {text = it},
            label = { Text("Suunnitelma") },
            modifier = Modifier.fillMaxWidth().padding(4.dp)
        )
    }
}