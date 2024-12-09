package eu.groeller.datastreamui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import eu.groeller.datastreamui.User
import eu.groeller.datastreamui.data.model.WorkoutResponse
import eu.groeller.datastreamui.data.workout.WorkoutState
import eu.groeller.datastreamui.viewmodel.WorkoutViewModel
import java.time.OffsetDateTime

@Composable
fun WorkoutScreen(
    viewModel: WorkoutViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val currentState = uiState) {
        is WorkoutState.Success -> WorkoutScreen(currentState.workouts)
        is WorkoutState.Error -> Text(currentState.error.message)
        is WorkoutState.Loading -> Text("Loading...")
    }
}

@Composable
fun WorkoutScreen(
    workouts: List<WorkoutResponse>
) {
    Column (
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(0.5f).fillMaxWidth().padding(20.dp, 45.dp),
        ) {
            Text(text = "Recent Workouts", style = MaterialTheme.typography.headlineSmall, softWrap = false)
            Column(
                modifier = Modifier.fillMaxSize()
                    .border(BorderStroke(2.dp, Color.Black), shape = RoundedCornerShape(9.dp))
                    .padding(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                val workoutIterator = workouts.iterator()
                while (workoutIterator.hasNext()) {
                    val workout = workoutIterator.next()
                    Row(
                        modifier = Modifier.fillMaxWidth().fillMaxHeight().weight(1f).padding(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DateBox(date = workout.date)
                        Text(workout.name)
                    }
                    if (workoutIterator.hasNext())
                        HorizontalDivider(thickness = 2.dp, color = Color.Gray)
                }
            }
        }
        Column(
            modifier = Modifier.fillMaxSize()
        ) {  }
    }
}

@Composable
fun DateBox(date: OffsetDateTime) {
    Box(modifier = Modifier.border(1.dp, Color.Black).fillMaxSize(0.2f)) {

    }
}
