package eu.groeller.datastreamui

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import eu.groeller.datastreamui.service.AuthService
import eu.groeller.datastreamui.ui.theme.DatastreamUITheme
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

val Context.datastore: DataStore<Preferences> by preferencesDataStore("settings")

class MainActivity : ComponentActivity() {

    private var token: String? = null
    private lateinit var authService: AuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        authService = AuthService(this, datastore)

        lifecycleScope.launch {
            token = authService.readToken()
        }

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            DatastreamUITheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = if (token == null) LoginMenu else MainMenu
                ) {
                    composable<LoginMenu> {
                        LoginMenuView()
                    }
                    composable<MainMenu> {
                        MainMenuView(token!!)
                    }
                }
            }
        }
    }
}

@Serializable
object LoginMenu

@Serializable
object MainMenu

@Composable
fun MainMenuView(token: String) {
    Column {
        Text("Logged in with token: $token")
    }
}

@Composable
fun LoginMenuView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }

        )
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") }
        )
    }
}


