package net.harutiro.newalerm.core.presenter.ringing.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.harutiro.newalerm.features.alarm.apis.AlarmSoundPlayerApi
import net.harutiro.newalerm.features.alarm.entities.AlarmSound

/**
 * 鳴動中アラームの画面用 ViewModel。
 *
 * - 音の再生は [AlarmSoundPlayerApi] に委譲（差し替え可能）
 * - スヌーズなし：[stop] で再生を止め、UI 側でアクティビティを finish させる
 */
class AlarmRingingViewModel(
    private val player: AlarmSoundPlayerApi,
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    fun start(sound: AlarmSound, isFinal: Boolean, index: Int, total: Int) {
        _state.value = UiState(
            isPlaying = true,
            isFinal = isFinal,
            indexLabel = if (total > 0) "${index + 1} / $total" else "",
            soundLabel = sound.label,
        )
        player.play(sound)
    }

    fun stop() {
        player.stop()
        _state.value = _state.value.copy(isPlaying = false)
    }

    override fun onCleared() {
        player.stop()
        super.onCleared()
    }

    data class UiState(
        val isPlaying: Boolean = false,
        val isFinal: Boolean = false,
        val indexLabel: String = "",
        val soundLabel: String = "",
    )
}
