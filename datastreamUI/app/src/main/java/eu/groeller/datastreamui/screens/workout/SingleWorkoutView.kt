package eu.groeller.datastreamui.screens.workout

import android.annotation.SuppressLint
import android.widget.ListView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.groeller.datastreamui.data.model.WorkoutResponse
import eu.groeller.datastreamui.data.workout.WorkoutRepository
import java.time.Duration
import java.time.OffsetDateTime
import java.util.concurrent.TimeUnit

@Composable
fun SingleWorkoutView(workout: WorkoutResponse) {
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(top = 25.dp, bottom = 25.dp)
    ){
        Row(
            modifier = Modifier.fillMaxHeight(0.08f)
                .border(BorderStroke(2.dp, Color.Black))
                .fillMaxWidth()
                .padding(top = 20.dp, start = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth(0.3f)
            ) {
                Text("Back")
            }
            Column(
                modifier = Modifier.padding(start = 5.dp)
                    .border(BorderStroke(2.dp, Color.Black)),
                verticalArrangement = Arrangement.Center
            ){
                Text("Duration:", fontSize = 16.sp)
                Text(createDurationString(workout), fontSize = 16.sp)
            }
        }
    }
}
 @SuppressLint("DefaultLocale")
 fun createDurationString(workout: WorkoutResponse): String {
    val duration = Duration.ofMillis(workout.durationMs)
    val hours = duration.toHours()
    val minutes = duration.toMinutesPart() 
    val seconds = duration.toSecondsPart()
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}