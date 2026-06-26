package net.harutiro.newalerm.core.presenter.soundSetting.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import net.harutiro.newalerm.core.presenter.soundSetting.viewModel.SoundSettingViewModel
import net.harutiro.newalerm.core.presenter.widget.SoundPicker

/** アラーム音設定画面。通常のアラーム音と「最後だけ別の音」を設定する。 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoundSettingPage(
    onBack: () -> Unit,
    viewModel: SoundSettingViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("アラーム音設定") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "戻る",
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text("通常のアラーム音", fontWeight = FontWeight.Bold)
                    SoundPicker(
                        selected = state.regularSound,
                        onSelect = viewModel::setRegularSound,
                    )

                    HorizontalDivider()

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "最後だけ別の音にする",
                            modifier = Modifier.weight(1f),
                            fontWeight = FontWeight.Bold,
                        )
                        Switch(
                            checked = state.useFinalSound,
                            onCheckedChange = viewModel::setUseFinalSound,
                        )
                    }
                    if (state.useFinalSound) {
                        SoundPicker(
                            selected = state.finalSound,
                            onSelect = viewModel::setFinalSound,
                        )
                    }
                }
            }
        }
    }
}
