package net.harutiro.newalerm.features.alarm.repositories

import android.content.Context
import kotlinx.coroutines.flow.StateFlow
import net.harutiro.newalerm.features.alarm.entities.AlarmConfig

/**
 * アラーム予定の開始/キャンセルと、現在稼働中の予定状態を扱うリポジトリ。
 *
 * 稼働状態の source of truth は永続ストア（[ActiveScheduleStore]）で、
 * 本リポジトリはそれをプロセス内の [StateFlow] としてミラーし、UI へ観測させる。
 */
interface AlarmRepository {

    /** 現在稼働中のアラーム予定。null の場合はアイドル（未予定）。 */
    val activeConfig: StateFlow<AlarmConfig?>

    /** 予定を開始する（AlarmManager へ登録し、状態を永続化・通知を表示）。 */
    fun startSchedule(context: Context, config: AlarmConfig)

    /** 稼働中の予定をキャンセルする（登録解除・永続状態のクリア・通知の消去）。 */
    fun cancelSchedule(context: Context)

    /** 永続化された稼働状態を読み出して復元する（プロセス起動時に呼ぶ）。 */
    fun restore(context: Context)
}
