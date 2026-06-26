package net.harutiro.newalerm.core.presenter.home.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import net.harutiro.newalerm.core.presenter.home.viewModel.HomeViewModel
import net.harutiro.newalerm.core.presenter.widget.IntervalField
import net.harutiro.newalerm.core.presenter.widget.TimeRangeField
import net.harutiro.newalerm.features.alarm.entities.AlarmConfig

/**
 * ホーム画面。
 *
 * - アイドル時: 時刻範囲と鳴動間隔だけを設定し、予定を開始する
 * - 稼働時: 設定項目を隠し、時刻範囲・間隔の表示とキャンセルボタンのみ。文字/背景/ボタンの3色だけの単色UI
 */
@Composable
fun HomePage(
    onStartSchedule: (AlarmConfig) -> Unit,
    onCancelSchedule: () -> Unit,
    onOpenSoundSetting: () -> Unit,
    viewModel: HomeViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val active = state.activeConfig
    if (active != null) {
        ActiveContent(config = active, onCancel = onCancelSchedule)
    } else {
        IdleContent(
            state = state,
            onStartTime = viewModel::setStartTime,
            onEndTime = viewModel::setEndTime,
            onInterval = viewModel::setInterval,
            onStart = { viewModel.buildConfigOrNull()?.let(onStartSchedule) },
            onOpenSoundSetting = onOpenSoundSetting,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IdleContent(
    state: HomeViewModel.UiState,
    onStartTime: (Int, Int) -> Unit,
    onEndTime: (Int, Int) -> Unit,
    onInterval: (Int) -> Unit,
    onStart: () -> Unit,
    onOpenSoundSetting: () -> Unit,
) {
    val editing = state.editing
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("間隔アラーム") },
                actions = {
                    IconButton(onClick = onOpenSoundSetting) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "アラーム音設定",
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text("時刻範囲", fontWeight = FontWeight.Bold)
                    TimeRangeField(
                        startHour = editing.startHour,
                        startMinute = editing.startMinute,
                        endHour = editing.endHour,
                        endMinute = editing.endMinute,
                        onStartPicked = onStartTime,
                        onEndPicked = onEndTime,
                    )
                    Text(
                        text = "終了が開始以前の場合は翌日扱いになります",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.outline,
                    )
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text("鳴動間隔", fontWeight = FontWeight.Bold)
                    IntervalField(
                        intervalMinutes = editing.intervalMinutes,
                        onChange = onInterval,
                    )
                }
            }

            Text(previewText(editing), color = MaterialTheme.colorScheme.outline)

            Button(
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
            ) {
                Text("アラーム予定を開始", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Text(
                text = "アラーム音は右上の設定から変更できます。",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.outline,
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

/** 文字・背景・ボタンの3色だけで構成する稼働中画面。 */
@Composable
private fun ActiveContent(
    config: AlarmConfig,
    onCancel: () -> Unit,
) {
    // 単色（モノクロ）3色構成
    val background = MaterialTheme.colorScheme.background
    val foreground = MaterialTheme.colorScheme.outline
    val buttonColor = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "アラーム予定 稼働中",
            color = foreground,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(40.dp))

        Text(
            text = "%02d:%02d 〜 %02d:%02d".format(
                config.startHour,
                config.startMinute,
                config.endHour,
                config.endMinute,
            ),
            color = foreground,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "${config.intervalMinutes} 分間隔",
            color = foreground,
            fontSize = 22.sp,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "全 ${config.occurrenceCount} 回",
            color = foreground,
            fontSize = 18.sp,
        )

        Spacer(Modifier.height(56.dp))

        Button(
            onClick = onCancel,
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor,
                contentColor = background,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
        ) {
            Text("アラームをキャンセル", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

/** アイドル時の予定回数プレビュー文字列。 */
private fun previewText(editing: HomeViewModel.Editing): String =
    runCatching {
        AlarmConfig(
            startHour = editing.startHour,
            startMinute = editing.startMinute,
            endHour = editing.endHour,
            endMinute = editing.endMinute,
            intervalMinutes = editing.intervalMinutes,
        ).occurrenceCount
    }.fold(
        onSuccess = { "予定回数: $it 回" },
        onFailure = { "設定値が不正です" },
    )
