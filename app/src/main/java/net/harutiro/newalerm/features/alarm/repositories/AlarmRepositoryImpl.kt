package net.harutiro.newalerm.features.alarm.repositories

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.harutiro.newalerm.features.alarm.apis.AlarmSchedulerApiImpl
import net.harutiro.newalerm.features.alarm.entities.AlarmConfig
import net.harutiro.newalerm.service.ScheduleNotifier

/**
 * [AlarmRepository] の実装。フォアグラウンドサービスを使わない構成。
 *
 * - 予定の発火は AlarmManager（[AlarmSchedulerApiImpl]）が担う
 * - 稼働状態の source of truth は永続ストア（[ActiveScheduleStore]）
 * - UI へは、プロセス内で共有する [State] の StateFlow を通して観測させる
 * - プロセス再生成後は [restore] で永続状態から StateFlow を復元する
 */
class AlarmRepositoryImpl : AlarmRepository {

    override val activeConfig: StateFlow<AlarmConfig?> = State.activeConfig.asStateFlow()

    override fun startSchedule(context: Context, config: AlarmConfig) {
        val appContext = context.applicationContext
        val scheduler = AlarmSchedulerApiImpl(appContext)
        val store = ActiveScheduleStore(appContext)

        // 既存予定が残っていれば消してから登録し直す
        scheduler.cancelAll(store.loadRequestCodes())

        val scheduled = scheduler.schedule(config)
        store.save(config, scheduled.requestCodes)
        ScheduleNotifier.show(appContext, config)
        State.activeConfig.value = config
    }

    override fun cancelSchedule(context: Context) {
        val appContext = context.applicationContext
        val scheduler = AlarmSchedulerApiImpl(appContext)
        val store = ActiveScheduleStore(appContext)

        scheduler.cancelAll(store.loadRequestCodes())
        store.clear()
        ScheduleNotifier.cancel(appContext)
        State.activeConfig.value = null
    }

    override fun restore(context: Context) {
        State.activeConfig.value = ActiveScheduleStore(context.applicationContext).loadConfig()
    }

    /** プロセス内で共有する稼働状態。永続ストアのミラー。 */
    private object State {
        val activeConfig = MutableStateFlow<AlarmConfig?>(null)
    }
}
