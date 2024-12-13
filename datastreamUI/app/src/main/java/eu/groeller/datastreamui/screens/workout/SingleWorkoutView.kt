package eu.groeller.datastreamui.screens.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import eu.groeller.datastreamui.data.model.WorkoutResponse
import eu.groeller.datastreamui.data.workout.WorkoutRepository

@Composable
fun SingleWorkoutView(workout: WorkoutResponse) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(workout.name)
    }
}