package eu.groeller.datastreamui

import WorkoutTrackingViewModel
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import eu.groeller.datastreamui.data.exercise.ExerciseRepository
import eu.groeller.datastreamui.data.model.WorkoutResponse
import eu.groeller.datastreamui.data.workout.WorkoutRepository
import eu.groeller.datastreamui.screens.DashScreen
import eu.groeller.datastreamui.screens.WorkoutScreen
import eu.groeller.datastreamui.screens.workout.SingleWorkoutView
import eu.groeller.datastreamui.screens.workout.WorkoutTrackingScreen
import eu.groeller.datastreamui.viewmodel.DashViewModel
import eu.groeller.datastreamui.viewmodel.WorkoutViewModel
import kotlinx.serialization.Serializable


public val Context.datastore: DataStore<Preferences> by preferencesDataStore("settings")
public const val DS_TAG = "DS"

class MainActivity : ComponentActivity() {

    private var token: String? = null
    private lateinit var injectionHolder: InjectionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        injectionHolder = InjectionManager(datastore)

        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
        }

        setContent {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = DashRoute
            ) {
                composable<DashRoute> {
                    val viewModel = viewModel<DashViewModel> {
                        DashViewModel(injectionHolder.userRepository)
                    }
                    DashScreen(viewModel,
                        onWorkoutButtonClicked = { user -> navController.navigate(WorkoutRoute(user.username, user.token)) } )
                }
                composable<WorkoutRoute> { backStackEntry ->
                    val args = backStackEntry.toRoute<WorkoutRoute>()
                    val viewModel = viewModel<WorkoutViewModel> {
                        val workoutRepository = WorkoutRepository(injectionHolder.httpConfig.v1HttpClient, User(args.username, "hihi", args.token))
                        WorkoutViewModel(workoutRepository)
                    }
                    WorkoutScreen(
                        viewModel = viewModel,
                        viewRecentWorkout = { workout ->
                            navController.currentBackStackEntry?.savedStateHandle?.set("workout", workout)
                            navController.navigate(SingleWorkoutRoute)
                        },
                        onTrackWorkoutClicked = {
                            navController.navigate(WorkoutTracking(args.username, args.token))
                        }
                    )
                }
                composable<SingleWorkoutRoute> { backStackEntry ->
                    val workout = navController.previousBackStackEntry?.savedStateHandle?.get<WorkoutResponse>("workout")
                    if (workout != null) {
                        SingleWorkoutView(
                            workout = workout,
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
                composable<WorkoutTracking> { backStackEntry ->
                    val args = backStackEntry.toRoute<WorkoutRoute>()
                    val viewModel = viewModel<WorkoutTrackingViewModel> {
                        val user = User(args.username, "hihi", args.token)
                        val exerciseRepository = ExerciseRepository(injectionHolder.httpConfig.v1HttpClient, user)
                        val workoutRepository = WorkoutRepository(injectionHolder.httpConfig.v1HttpClient, user)
                        WorkoutTrackingViewModel(exerciseRepository, workoutRepository)
                    }
                    WorkoutTrackingScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onWorkoutComplete = {
                            // TODO: Handle workout completion
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}


@Serializable object DashRoute
@Serializable data class WorkoutRoute(val username: String, val token: String)
@Serializable object SingleWorkoutRoute
@Serializable data class WorkoutTracking(val username: String, val token: String)
