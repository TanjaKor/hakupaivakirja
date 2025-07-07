package com.example.hakupivkirja.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.hakupivkirja.model.TrainingSessionUiState

@Composable
fun IlmaisunValinta(
  uiState: TrainingSessionUiState,
  onAlarmTypeChange: (String) -> Unit,
  onDismissRequest: () -> Unit
) {
  Dialog(onDismissRequest = { onDismissRequest() }) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .padding(16.dp),
      shape = RoundedCornerShape(16.dp),
    ) {
      Column(modifier = Modifier.padding(16.dp).fillMaxSize().wrapContentSize(Alignment.Center)) {
        Text(
          text = "Valitse ilmaisu koirallesi",
          textAlign = TextAlign.Center,
        )
        TextButton(onClick = {
          onAlarmTypeChange("rulla")
          Log.d("IlmaisunValinta", "Ilmaisu on ${uiState.alarmType}")
        }) {
          Text(text = "Rulla")
        }
        TextButton(onClick = {
          uiState.alarmType?.let { onAlarmTypeChange(it) } ?: onAlarmTypeChange("haukku")
          Log.d("IlmaisunValinta", "Ilmaisu on ${uiState.alarmType}")
        }) {
          Text(text = "Haukku")
        }
      }

    }
  }
}