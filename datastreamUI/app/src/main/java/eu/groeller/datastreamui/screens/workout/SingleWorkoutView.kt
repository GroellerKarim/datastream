package eu.groeller.datastreamui.screens.workout

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.groeller.datastreamui.data.model.WorkoutResponse
import java.time.Duration
import java.time.OffsetDateTime

@Composable
fun SingleWorkoutView(workout: WorkoutResponse) {
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(top = 30.dp, bottom = 25.dp)
    ){
        Row(
            modifier = Modifier.fillMaxHeight(0.08f)
                //.border(BorderStroke(2.dp, Color.Black))
                .fillMaxWidth()
                .padding(top = 20.dp, start = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth(0.3f)
            ) {
                Text("Back")
            }
            Column(
                modifier = Modifier.padding(start = 5.dp),
                    //.border(BorderStroke(2.dp, Color.Black)),
                verticalArrangement = Arrangement.Center
            ){
                Text("Duration:", fontSize = 16.sp)
                Text(createDurationString(workout.durationMs), fontSize = 16.sp)
            }
        }
        workout.exercises.forEach { exercise ->
            SingleExerciseCard(exercise)
        }
    }
}
 @SuppressLint("DefaultLocale")
 fun createDurationString(ms: Long): String {
    val duration = Duration.ofMillis(ms)
    val hours = duration.toHours()
    val minutes = duration.toMinutesPart() 
    val seconds = duration.toSecondsPart()
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

fun createDurationString(start: OffsetDateTime, end: OffsetDateTime): String {
    val durationBetween = Duration.between(start, end)
    return createDurationString(durationBetween.toMillis())
}