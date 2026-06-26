package net.harutiro.newalerm.core.presenter.widget

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/** 開始〜終了の時刻を選択する行。各ボタンタップで [TimePickerDialog] を表示する。 */
@Composable
fun TimeRangeField(
    startHour: Int,
    startMinute: Int,
    endHour: Int,
    endMinute: Int,
    onStartPicked: (Int, Int) -> Unit,
    onEndPicked: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TimeButton(
            label = "開始",
            hour = startHour,
            minute = startMinute,
            modifier = Modifier.weight(1f),
            onPick = onStartPicked,
        )
        Text("〜")
        TimeButton(
            label = "終了",
            hour = endHour,
            minute = endMinute,
            modifier = Modifier.weight(1f),
            onPick = onEndPicked,
        )
    }
}

@Composable
private fun TimeButton(
    label: String,
    hour: Int,
    minute: Int,
    modifier: Modifier = Modifier,
    onPick: (Int, Int) -> Unit,
) {
    val context = LocalContext.current
    Button(
        onClick = {
            TimePickerDialog(
                context,
                { _, h, m -> onPick(h, m) },
                hour,
                minute,
                true,
            ).show()
        },
        modifier = modifier,
    ) {
        Text("$label  %02d:%02d".format(hour, minute))
    }
}
