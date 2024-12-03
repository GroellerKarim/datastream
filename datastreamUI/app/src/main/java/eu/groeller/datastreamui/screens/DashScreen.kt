package eu.groeller.datastreamui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import eu.groeller.datastreamui.data.user.UserState
import eu.groeller.datastreamui.viewmodel.DashViewModel

@Composable
fun DashScreen(
    viewModel: DashViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DashScreen(uiState, viewModel::login)
}

@Composable
fun DashScreen(
    uiState: UserState,
    onLoginClicked: (String, String) -> Unit
) {
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when(uiState) {
            is UserState.Loading -> Text("Loading...")
            is UserState.LocalMissing -> LoginView(onLoginClicked) ;
            is UserState.NetworkFailed -> Text("Error: ${uiState.err.message}")
            is UserState.Success -> Text("Yay got a user ${uiState.user.username}")
        }
   }
}

@Composable
fun LoginView(
    onLoginClicked: (String, String) -> Unit
) {
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
        Button(onClick = { onLoginClicked(email, password) }) {
            Text("Login")
        }
    }
}
