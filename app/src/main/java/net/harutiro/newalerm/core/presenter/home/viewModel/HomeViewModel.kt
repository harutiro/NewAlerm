package net.harutiro.newalerm.core.presenter.home.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import net.harutiro.newalerm.features.alarm.entities.AlarmConfig
import net.harutiro.newalerm.features.alarm.repositories.AlarmRepository
import net.harutiro.newalerm.features.alarm.repositories.AlarmRepositoryImpl
import net.harutiro.newalerm.features.alarm.repositories.AlarmSoundPreferenceRepository
import net.harutiro.newalerm.features.alarm.repositories.AlarmSoundPreferenceRepositoryImpl

/**
 * ホーム画面 ViewModel。
 *
 * - アイドル時: 時刻範囲・間隔の編集状態を保持する（揮発で十分）
 * - 稼働時: [AlarmRepository.activeConfig] を観測し、稼働中の予定を表示へ反映する
 *
 * アラーム音の選択は音設定画面と共有するため [AlarmSoundPreferenceRepository] から都度読み出す。
 */
class HomeViewModel(
    application: Application,
    private val alarmRepository: AlarmRepository,
    private val soundPreferenceRepository: AlarmSoundPreferenceRepository,
) : AndroidViewModel(application) {

    /** [androidx.lifecycle.viewmodel.compose.viewModel] から生成するためのコンストラクタ。 */
    constructor(application: Application) : this(
        application = application,
        alarmRepository = AlarmRepositoryImpl(),
        soundPreferenceRepository = AlarmSoundPreferenceRepositoryImpl(application),
    )

    private val editing = MutableStateFlow(
        Editing(
            startHour = 7,
            startMinute = 0,
            endHour = 9,
            endMinute = 0,
            intervalMinutes = 10,
        )
    )

    val uiState: StateFlow<UiState> =
        combine(editing, alarmRepository.activeConfig) { editing, active ->
            UiState(editing = editing, activeConfig = active)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState(editing = editing.value, activeConfig = alarmRepository.activeConfig.value),
        )

    fun setStartTime(hour: Int, minute: Int) {
        editing.value = editing.value.copy(startHour = hour, startMinute = minute)
    }

    fun setEndTime(hour: Int, minute: Int) {
        editing.value = editing.value.copy(endHour = hour, endMinute = minute)
    }

    fun setInterval(minutes: Int) {
        if (minutes <= 0) return
        editing.value = editing.value.copy(intervalMinutes = minutes)
    }

    /**
     * 編集中の値と保存済みのアラーム音設定から [AlarmConfig] を生成する。invalid な場合は null。
     */
    fun buildConfigOrNull(): AlarmConfig? = runCatching {
        val e = editing.value
        val sound = soundPreferenceRepository.load()
        AlarmConfig(
            startHour = e.startHour,
            startMinute = e.startMinute,
            endHour = e.endHour,
            endMinute = e.endMinute,
            intervalMinutes = e.intervalMinutes,
            regularSound = sound.regularSound,
            finalSound = if (sound.useFinalSound) sound.finalSound else null,
        )
    }.getOrNull()

    /** 編集中の入力。アイドル時のみ意味を持つ。 */
    data class Editing(
        val startHour: Int,
        val startMinute: Int,
        val endHour: Int,
        val endMinute: Int,
        val intervalMinutes: Int,
    )

    data class UiState(
        val editing: Editing,
        val activeConfig: AlarmConfig?,
    ) {
        /** 稼働中（予定が走っている）かどうか。 */
        val isActive: Boolean get() = activeConfig != null
    }
}
