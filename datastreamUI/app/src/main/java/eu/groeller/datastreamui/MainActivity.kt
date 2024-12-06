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
import eu.groeller.datastreamui.screens.DashScreen
import eu.groeller.datastreamui.screens.LoginScreen
import eu.groeller.datastreamui.viewmodel.DashViewModel
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
                    DashScreen(DashViewModel(injectionHolder.userRepository), )
                }
                composable<WorkoutRoute> {  }
            }
        }
    }
}


@Serializable object DashRoute
@Serializable object WorkoutRoute
