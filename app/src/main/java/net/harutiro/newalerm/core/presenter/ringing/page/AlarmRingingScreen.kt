package net.harutiro.newalerm.core.presenter.ringing.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.harutiro.newalerm.core.presenter.ringing.viewModel.AlarmRingingViewModel

@Composable
fun AlarmRingingScreen(
    viewModel: AlarmRingingViewModel,
    onStopped: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val bg = if (state.isFinal) Color(0xFF7B1FA2) else Color(0xFF1976D2)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Text(
                text = if (state.isFinal) "最後のアラーム" else "アラーム",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
            )
            if (state.indexLabel.isNotEmpty()) {
                Text(
                    text = state.indexLabel,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 20.sp,
                )
            }
            Text(
                text = "音源: ${state.soundLabel}",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp,
            )

            Button(
                onClick = {
                    viewModel.stop()
                    onStopped()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = bg,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp),
            ) {
                Text(
                    text = "停止",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Text(
                text = "スヌーズはありません。次のアラームは予定どおり鳴ります。",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
