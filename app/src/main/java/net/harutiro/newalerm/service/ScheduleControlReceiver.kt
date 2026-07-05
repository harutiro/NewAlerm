package net.harutiro.newalerm.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import net.harutiro.newalerm.features.alarm.repositories.AlarmRepositoryImpl

/**
 * 継続通知の「キャンセル」操作を受けて、稼働中のアラーム予定を解除する。
 */
class ScheduleControlReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_CANCEL_SCHEDULE) return
        AlarmRepositoryImpl().cancelSchedule(context)
    }

    companion object {
        const val ACTION_CANCEL_SCHEDULE = "net.harutiro.newalerm.CANCEL_SCHEDULE"
    }
}
