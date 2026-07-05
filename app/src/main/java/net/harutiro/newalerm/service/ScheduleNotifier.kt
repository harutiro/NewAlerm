package net.harutiro.newalerm.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import net.harutiro.newalerm.MainActivity
import net.harutiro.newalerm.R
import net.harutiro.newalerm.features.alarm.entities.AlarmConfig

/**
 * 稼働中のアラーム予定を知らせる継続通知を扱うヘルパ。
 *
 * フォアグラウンドサービスは使わず、通常の継続通知（ongoing）として表示する。
 * 通知にはアプリを開く導線と、予定をキャンセルする操作（[ScheduleControlReceiver]）を持たせる。
 */
object ScheduleNotifier {

    fun show(context: Context, config: AlarmConfig) {
        ensureChannel(context)
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIFICATION_ID, build(context, config))
    }

    fun cancel(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(NOTIFICATION_ID)
    }

    private fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (nm.getNotificationChannel(CHANNEL_ID) == null) {
                nm.createNotificationChannel(
                    NotificationChannel(
                        CHANNEL_ID,
                        "アラーム予定",
                        NotificationManager.IMPORTANCE_LOW,
                    ).apply {
                        description = "間隔アラームのスケジュールが有効な間表示されます"
                    }
                )
            }
        }
    }

    private fun build(context: Context, config: AlarmConfig): Notification {
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val contentPi = PendingIntent.getActivity(
            context,
            0,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val cancelPi = PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, ScheduleControlReceiver::class.java).apply {
                action = ScheduleControlReceiver.ACTION_CANCEL_SCHEDULE
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("アラーム予定")
            .setContentText(summary(config))
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(contentPi)
            .addAction(0, "キャンセル", cancelPi)
            .build()
    }

    private fun summary(config: AlarmConfig): String {
        val s = "%02d:%02d".format(config.startHour, config.startMinute)
        val e = "%02d:%02d".format(config.endHour, config.endMinute)
        return "$s〜$e / ${config.intervalMinutes}分間隔（${config.occurrenceCount}回）"
    }

    private const val CHANNEL_ID = "alarm_schedule_channel"
    private const val NOTIFICATION_ID = 1
}
