package eu.groeller.datastreamui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import eu.groeller.datastreamui.data.model.WorkoutResponse
import eu.groeller.datastreamui.data.workout.WorkoutState
import eu.groeller.datastreamui.viewmodel.WorkoutViewModel
import eu.groeller.datastreamui.widgets.DateBox

@Composable
fun WorkoutScreen(
    viewModel: WorkoutViewModel = viewModel(),
    viewRecentWorkout: (WorkoutResponse) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val currentState = uiState) {
        is WorkoutState.Success -> WorkoutScreen(currentState.workouts, viewRecentWorkout)
        is WorkoutState.Error -> Text(currentState.error.message)
        is WorkoutState.Loading -> Text("Loading...")
    }
}

@Composable
fun WorkoutScreen(
    workouts: List<WorkoutResponse>,
    viewRecentWorkout: (WorkoutResponse) -> Unit
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
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TextButton(
                            modifier = Modifier.fillMaxSize(),
                            onClick = { viewRecentWorkout(workout) },
                            shape = RectangleShape,
                        ) {
                            DateBox(date = workout.date)
                            Column(
                                modifier = Modifier.fillMaxSize().absolutePadding(left = 5.dp),
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(workout.name)
                            }
                        }
                    }
                    if (workoutIterator.hasNext())
                        HorizontalDivider(thickness = 2.dp, color = Color.Gray)
                }
            }
        }
        Column(
            modifier = Modifier.fillMaxSize()
                .border(BorderStroke(2.dp, Color.Black), shape = RoundedCornerShape(9.dp)),
        ) {
            Row(
                modifier = Modifier.fillMaxSize().weight(1f)
                    .border(BorderStroke(2.dp, Color.Black), shape = RoundedCornerShape(9.dp)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Calendar / Workout Plan")
            }
            Row(
                modifier = Modifier.fillMaxSize().weight(1f)
                    .border(BorderStroke(2.dp, Color.Black), shape = RoundedCornerShape(9.dp)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { }) {
                    Text("Track")
                }
                Button(onClick = { }) {
                    Text("Statistics")
                }
            }
        }
    }
}