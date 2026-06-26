package net.harutiro.newalerm.core.presenter.widget

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

/** 鳴動間隔（分）を入力するフィールド。 */
@Composable
fun IntervalField(
    intervalMinutes: Int,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var text by remember(intervalMinutes) { mutableStateOf(intervalMinutes.toString()) }
    OutlinedTextField(
        value = text,
        onValueChange = { new ->
            text = new.filter(Char::isDigit).take(4)
            val v = text.toIntOrNull() ?: 0
            if (v > 0) onChange(v)
        },
        label = { Text("分") },
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
    )
}
