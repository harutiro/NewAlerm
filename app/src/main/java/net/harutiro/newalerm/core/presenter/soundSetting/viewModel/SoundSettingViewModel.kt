package net.harutiro.newalerm.core.presenter.soundSetting.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.harutiro.newalerm.features.alarm.entities.AlarmSound
import net.harutiro.newalerm.features.alarm.repositories.AlarmSoundPreferenceRepository
import net.harutiro.newalerm.features.alarm.repositories.AlarmSoundPreferenceRepositoryImpl

/**
 * アラーム音設定画面の ViewModel。
 *
 * 変更のたびに [AlarmSoundPreferenceRepository] へ保存し、ホーム画面の予定生成と共有する。
 */
class SoundSettingViewModel(
    application: Application,
    private val repository: AlarmSoundPreferenceRepository,
) : AndroidViewModel(application) {

    /** [androidx.lifecycle.viewmodel.compose.viewModel] から生成するためのコンストラクタ。 */
    constructor(application: Application) : this(
        application = application,
        repository = AlarmSoundPreferenceRepositoryImpl(application),
    )

    private val _state = MutableStateFlow(repository.load().toUiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    fun setRegularSound(sound: AlarmSound) = update { it.copy(regularSound = sound) }

    fun setUseFinalSound(enabled: Boolean) = update { it.copy(useFinalSound = enabled) }

    fun setFinalSound(sound: AlarmSound) = update { it.copy(finalSound = sound) }

    private fun update(transform: (UiState) -> UiState) {
        val next = transform(_state.value)
        _state.value = next
        repository.save(next.toPreference())
    }

    data class UiState(
        val regularSound: AlarmSound,
        val useFinalSound: Boolean,
        val finalSound: AlarmSound,
    )

    private fun AlarmSoundPreferenceRepository.SoundPreference.toUiState() = UiState(
        regularSound = regularSound,
        useFinalSound = useFinalSound,
        finalSound = finalSound,
    )

    private fun UiState.toPreference() = AlarmSoundPreferenceRepository.SoundPreference(
        regularSound = regularSound,
        useFinalSound = useFinalSound,
        finalSound = finalSound,
    )
}
