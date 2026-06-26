package net.harutiro.newalerm.features.alarm.repositories

import android.content.Context
import kotlinx.coroutines.flow.StateFlow
import net.harutiro.newalerm.features.alarm.entities.AlarmConfig

/**
 * アラーム予定の開始/キャンセルと、現在稼働中の予定状態を扱うリポジトリ。
 *
 * 稼働状態はフォアグラウンドサービス（[net.harutiro.newalerm.service.AlarmForegroundService]）が
 * 唯一の source of truth であり、本リポジトリはその状態をプロセス内で共有・観測するための窓口。
 */
interface AlarmRepository {

    /** 現在稼働中のアラーム予定。null の場合はアイドル（未予定）。 */
    val activeConfig: StateFlow<AlarmConfig?>

    /** 予定を開始する（フォアグラウンドサービスを起動）。 */
    fun startSchedule(context: Context, config: AlarmConfig)

    /** 稼働中の予定をキャンセルする（サービスへ停止指示）。 */
    fun cancelSchedule(context: Context)

    /** サービスから稼働開始を通知する内部用。 */
    fun notifyActivated(config: AlarmConfig)

    /** サービスから稼働終了を通知する内部用。 */
    fun notifyCleared()
}
