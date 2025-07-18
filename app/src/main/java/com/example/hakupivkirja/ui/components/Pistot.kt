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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hakupivkirja.model.PistoUiState

@Composable
fun Pisto(
  pistoUiState: PistoUiState,
  onHaukutChange: (String) -> Unit,
  onAvutChange: (String) -> Unit,
  onPalkkaChange: (String) -> Unit,
  onComeToMiddleChange: (Boolean) -> Unit,
  onIsClosedChange: (Boolean) -> Unit,
  onSuoraPalkkaChange: (Boolean) -> Unit,
  onKiintoRullaChange: (Boolean) -> Unit,
  onIrtorullanSijaintiChange: (String) -> Unit,
  sessionAlarmType: String?
) {
  Column(
    verticalArrangement = Arrangement.Bottom,
    modifier = Modifier.padding(top = 4.dp)
  ) {

    Row(
      modifier = Modifier
        .fillMaxWidth(),
      verticalAlignment = Alignment.Top
    ) {
      AvutDropdown(
        selectedText = pistoUiState.avut ?: "",
        onSelectedValueChange = { newValue ->
          onAvutChange(newValue)
        },
        modifier = Modifier
            .weight(0.3f)
            .widthIn(min = 60.dp, max = 90.dp)
      )
      PalkkaDropdown(
        selectedText = pistoUiState.palkka ?: "",
        onSelectedValueChange = { newValue ->
          onPalkkaChange(newValue)
        },
        modifier = Modifier
            .weight(0.3f)
            .widthIn(min = 65.dp, max = 100.dp)
      )
    }
    if (sessionAlarmType == "haukku") {
        OutlinedTextField(
          value = pistoUiState.haukut ?: "",
          onValueChange = { newValue ->
            if (newValue.toIntOrNull() != null || newValue.isEmpty()) {
              onHaukutChange(newValue)
            }
          },
          keyboardOptions = KeyboardOptions( // Keep only this one
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
          ),
          label = { Text("Haukut", fontSize = 11.sp) },
          colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
          ),
          textStyle = TextStyle(color = MaterialTheme.colorScheme.onSecondaryContainer),
          modifier = Modifier
              .heightIn(min = 55.dp)
              .padding(1.dp),
        )
    } else {
        OutlinedTextField(
            value = pistoUiState.irtorullanSijainti ?: "",
            onValueChange = { newValue ->
                    onIrtorullanSijaintiChange(newValue)
            },
            keyboardOptions = KeyboardOptions( // Keep only this one
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            label = { Text("Irtorullan sijainti", fontSize = 11.sp) },
            colors = TextFieldDefaults.colors(
              unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
              unfocusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
              focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSecondaryContainer),
            modifier = Modifier
                .heightIn(min = 55.dp)
                .padding(1.dp),
        )
    }
      Row(
          modifier = Modifier
              .fillMaxWidth()
              .heightIn(min = 64.dp)
      ) {
        CheckBoxWithLabel(
            label = "Sisääntulo",
            checked = pistoUiState.comeToMiddle,
            onCheckedChange = onComeToMiddleChange,
            modifier = Modifier.weight(0.5f)
        )
        CheckBoxWithLabel(
            label = "Umpipiilo",
            checked = pistoUiState.isClosed,
            onCheckedChange = onIsClosedChange,
            modifier = Modifier.weight(0.5f)
        )
          if (sessionAlarmType != "haukku") {
              CheckBoxWithLabel(
                  label = "Suorapalkka",
                  checked = pistoUiState.suoraPalkka,
                  onCheckedChange = onSuoraPalkkaChange,
                  modifier = Modifier.weight(0.5f)
              )
              pistoUiState.kiintoRulla?.let {
                  CheckBoxWithLabel(
                      label = "Kiintorulla",
                      checked = it,
                      onCheckedChange = onKiintoRullaChange,
                      modifier = Modifier.weight(0.5f)
                  )
              }
          }
      }
  }
}

@Composable
private fun CheckBoxWithLabel(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start
) {
    Column(
        horizontalAlignment = horizontalAlignment,
        modifier = modifier
            .padding(horizontal = 2.dp)
            .padding(top = 3.dp)
    ) {
        Text(label, fontSize = 11.sp, lineHeight = 9.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

