package com.example.hakupivkirja.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
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
          onPalkkaChange: (String) -> Unit,
          onComeToMiddleChange: (Boolean) -> Unit) {
    Column(

        verticalArrangement = Arrangement.Top,
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth(),
//                .height(130.dp),
            verticalAlignment = Alignment.Top
        ) {
            AvutDropdown(selectedText = pistoUiState.avut ?: "",
                onSelectedValueChange = { newValue ->
                    onAvutChange(newValue)
                },
                modifier = Modifier
                    .weight(0.3f)
                    .widthIn(min = 65.dp, max = 100.dp)
            )
            PalkkaDropdown(
                selectedText = pistoUiState.palkka ?: "",
                onSelectedValueChange = { newValue ->
                    onPalkkaChange(newValue)
                },
                modifier = Modifier
                    .weight(0.3f)
                    .widthIn(min = 76.dp, max = 100.dp)
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp)
        ) {
            OutlinedTextField(
                value = pistoUiState.haukut ?: "",
                onValueChange = { newValue ->
                    if (newValue.toIntOrNull() != null || newValue.isEmpty()) {
                        onHaukutChange(newValue)
                    }
                },
                keyboardOptions = KeyboardOptions( // Keep only this one
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                label = { Text("Hau", fontSize = 11.sp) },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 56.dp)
                    .padding(1.dp),
            )
            Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                Text("Sisääntulo", fontSize = 11.sp)
                Checkbox(
                    checked = pistoUiState.comeToMiddle,
                    onCheckedChange = onComeToMiddleChange
                )
            }

        }

    }
}

