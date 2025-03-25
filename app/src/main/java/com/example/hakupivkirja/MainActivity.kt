package com.example.hakupivkirja

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hakupivkirja.ui.components.AppTopBar
import com.example.hakupivkirja.ui.screens.HomeScreen
import com.example.hakupivkirja.ui.theme.HakupäiväkirjaTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HakupäiväkirjaTheme {
                Scaffold(
                    topBar = { AppTopBar() }
                ) { innerPadding ->
                    App(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun App(modifier: Modifier) {
    Column(modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HomeScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HakupäiväkirjaTheme {
        App(modifier = Modifier)
    }
}