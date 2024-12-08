package eu.groeller.datastreamui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import eu.groeller.datastreamui.User
import eu.groeller.datastreamui.data.user.UserState
import eu.groeller.datastreamui.viewmodel.DashViewModel

@Composable
fun DashScreen(
    viewModel: DashViewModel = viewModel(),
    onWorkoutButtonClicked: (User) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DashScreen(uiState, viewModel::login, onWorkoutButtonClicked)
}

@Composable
fun DashScreen(
    uiState: UserState,
    onLoginClicked: (String, String) -> Unit,
    onWorkoutButtonClicked: (User) -> Unit
) {
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when(uiState) {
            is UserState.Loading -> Text("Loading...")
            is UserState.LocalMissing -> LoginScreen(onLoginClicked, null)
            is UserState.NetworkFailed -> LoginScreen(onLoginClicked, uiState.err)
            is UserState.Success -> Button(onClick = { onWorkoutButtonClicked(uiState.user) }) { Text("Workouts") }
        }
   }
}

