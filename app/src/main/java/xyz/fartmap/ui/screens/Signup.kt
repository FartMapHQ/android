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
fun Signup (nav: NavHostController, prefs: Preferences) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column {
        Text("Inscrivez vous !")

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
                error = ""

                scope.launch {
                    try {
                        prefs.signup(username, password)
                        nav.navigate(Destinations.LOGIN)
                    }
                    catch (e: Exception) {
                        error = e.message ?: "Erreur inconnue"
                    }
                    finally {
                        loading = false
                    }
                }
            },
            enabled = !loading
        ) {
            Text("S'inscrire")
        }

        if (error.isNotEmpty()) {
            Text(error)
        }

        Text("Vous avez déjà un compte ?")
        OutlinedButton (
            onClick = {
                nav.navigate(Destinations.LOGIN)
            },
            enabled = !loading
        ) {
            Text("Se connecter")
        }
    }

}