package net.harutiro.newalerm.features.alarm.repositories

import android.content.Context
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.harutiro.newalerm.features.alarm.entities.AlarmConfig
import net.harutiro.newalerm.service.AlarmForegroundService

/**
 * [AlarmRepository] の実装。
 *
 * 稼働状態はプロセス内シングルトン（[State]）で保持する。フォアグラウンドサービスが生きている間は
 * プロセスも生存しているため、UI 層とサービスとで同一の状態を共有できる。
 * （プロセスが死ねばサービスも消えるので、状態 null へのリセットと整合する。）
 */
class AlarmRepositoryImpl : AlarmRepository {

    override val activeConfig: StateFlow<AlarmConfig?> = State.activeConfig.asStateFlow()

    override fun startSchedule(context: Context, config: AlarmConfig) {
        val intent = AlarmForegroundService.startIntent(context, config)
        ContextCompat.startForegroundService(context, intent)
    }

    override fun cancelSchedule(context: Context) {
        context.startService(AlarmForegroundService.cancelIntent(context))
    }

    override fun notifyActivated(config: AlarmConfig) {
        State.activeConfig.value = config
    }

    override fun notifyCleared() {
        State.activeConfig.value = null
    }

    /** プロセス内で共有する稼働状態。 */
    private object State {
        val activeConfig = MutableStateFlow<AlarmConfig?>(null)
    }
}
