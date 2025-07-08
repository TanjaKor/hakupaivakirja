package com.example.hakupivkirja.ui.components

import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog

@Composable
fun SaveSuccessMessage() {
  Dialog(onDismissRequest = { /* Handle dismiss if needed */ }) {
    Card {
      Text("Treenisi on tallennettu!")
    }
  }
}