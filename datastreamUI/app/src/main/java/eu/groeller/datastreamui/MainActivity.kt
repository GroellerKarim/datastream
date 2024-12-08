package eu.groeller.datastreamui

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import eu.groeller.datastreamui.data.workout.WorkoutRepository
import eu.groeller.datastreamui.screens.DashScreen
import eu.groeller.datastreamui.screens.LoginScreen
import eu.groeller.datastreamui.screens.WorkoutScreen
import eu.groeller.datastreamui.viewmodel.DashViewModel
import eu.groeller.datastreamui.viewmodel.WorkoutViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable


public val Context.datastore: DataStore<Preferences> by preferencesDataStore("settings")
class MainActivity : ComponentActivity() {

    private var token: String? = null
    private lateinit var injectionHolder: InjectionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        injectionHolder = InjectionManager(datastore)
        lifecycleScope.launch {
            token = injectionHolder.localDataSource.readToken()
        }

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = DashRoute
            ) {
                composable<DashRoute> {
                    DashScreen(DashViewModel(injectionHolder.userRepository),
                        onWorkoutButtonClicked = { user -> navController.navigate(WorkoutRoute(user.username, user.token)) } )
                }
                composable<WorkoutRoute> { backStackEntry ->
                    val args = backStackEntry.toRoute<WorkoutRoute>()
                    val workoutRepository = WorkoutRepository(injectionHolder.httpConfig.v1HttpClient, User(args.username, "hihi", args.token))
                    WorkoutScreen(WorkoutViewModel(workoutRepository))
                }
            }
        }
    }
}


@Serializable object DashRoute
@Serializable data class WorkoutRoute(val username: String, val token: String)
