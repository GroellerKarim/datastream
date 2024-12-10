package eu.groeller.datastreamui.widgets

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.OffsetDateTime


@Composable
fun DateBox(date: OffsetDateTime) {
    Box(modifier = Modifier.border(1.dp, Color.Black).fillMaxHeight(0.8f).fillMaxWidth(0.135f).padding(horizontal = 5.dp)) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${date.dayOfMonth}${getDayOfMonthSuffix(date.dayOfMonth)}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = date.month.toString().substring(0, 3),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun getDayOfMonthSuffix(day: Int): String = when {
    day in 11..13 -> "th"
    day % 10 == 1 -> "st"
    day % 10 == 2 -> "nd"
    day % 10 == 3 -> "rd"
    else -> "th"
}
