package xyz.fartmap.ui.navigation

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import xyz.fartmap.data.Preferences
import xyz.fartmap.ui.screens.Login
import xyz.fartmap.ui.screens.Record
import xyz.fartmap.ui.screens.Signup
import xyz.fartmap.ui.screens.Welcome

@Composable
fun Navigator(controller: NavHostController, context: Context) {
    val prefs = Preferences(context)
    val token by prefs.token.collectAsState(null)

    var startDestination: String? by remember {
        mutableStateOf(null)
    }

    LaunchedEffect (token) {
        if (startDestination == null && token != null) {
            startDestination = if (token!!.isNotEmpty())
                Destinations.RECORD
            else Destinations.WELCOME
        }
    }

    if (startDestination == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
    else {
        NavHost(controller, startDestination = startDestination!!) {
            composable(Destinations.WELCOME) {
                Welcome(controller)
            }
            composable(Destinations.LOGIN) {
                Login(controller, prefs)
            }
            composable(Destinations.SIGNUP) {
                Signup(controller, prefs)
            }
            composable(Destinations.RECORD) {
                Record(controller, prefs)
            }
        }
    }
}