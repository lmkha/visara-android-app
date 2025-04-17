package com.example.visara.ui.screens.mail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun MailScreen(
   modifier: Modifier = Modifier,
   bottomNavBar: @Composable () -> Unit,
) {
   Scaffold(
      modifier = Modifier.fillMaxSize(),
      bottomBar = { bottomNavBar() },
   ) {innerPadding->
      Column(
         modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)
         ,
         verticalArrangement = Arrangement.Center,
         horizontalAlignment = Alignment.CenterHorizontally,
      ) {
         FilledTonalButton(
            onClick = {},
            colors = ButtonDefaults.filledTonalButtonColors(
               containerColor = MaterialTheme.colorScheme.primary
            )
         ) {
            Text("Check Var")
         }
      }
   }
}
