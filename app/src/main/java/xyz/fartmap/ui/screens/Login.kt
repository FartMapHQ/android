package xyz.fartmap.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import xyz.fartmap.data.Preferences
import xyz.fartmap.ui.navigation.Destinations

@Composable
fun Login (nav: NavHostController, prefs: Preferences) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column {
        Text("Connectez-vous")

        if (loading) {
            CircularProgressIndicator()
        }

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nom d'utilisateur") },
            enabled = !loading
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mot de passe") },
            visualTransformation = PasswordVisualTransformation(),
            enabled = !loading
        )

        Button (
            onClick = {
                loading = true
                scope.launch {
                    prefs.login(username, password)
                    loading = false
                    nav.navigate(Destinations.RECORD)
                }
            },
            enabled = !loading
        ) {
            Text("Se connecter")
        }

        Text("Pas encore de compte ?")
        OutlinedButton (
            onClick = {
                nav.navigate(Destinations.SIGNUP)
            },
            enabled = !loading
        ) {
            Text("S'inscrire")
        }
    }

}