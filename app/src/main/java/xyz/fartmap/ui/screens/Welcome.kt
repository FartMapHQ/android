package xyz.fartmap.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import xyz.fartmap.ui.navigation.Destinations

@Composable
fun Welcome (nav: NavHostController) {
    Column {
        Text("Bienvenue sur FartMap, êtes vous prêt à péter ?")
        Button(
            onClick = {
                nav.navigate(Destinations.LOGIN)
            }
        ) {
            Text("Se connecter")
        }
        OutlinedButton(
            onClick = {
                nav.navigate(Destinations.SIGNUP)
            }
        ) {
            Text("S'inscrire")
        }

        Button(
            onClick = {
                nav.navigate(Destinations.RECORD)
            }
        ) {
            Text("oe tkt")
        }
    }
}