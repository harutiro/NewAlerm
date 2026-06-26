package net.harutiro.newalerm.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import net.harutiro.newalerm.MainActivity
import net.harutiro.newalerm.R
import net.harutiro.newalerm.features.alarm.apis.AlarmSchedulerApi
import net.harutiro.newalerm.features.alarm.apis.AlarmSchedulerApiImpl
import net.harutiro.newalerm.features.alarm.apis.deserializeAlarmSound
import net.harutiro.newalerm.features.alarm.apis.serializeKey
import net.harutiro.newalerm.features.alarm.entities.AlarmConfig
import net.harutiro.newalerm.features.alarm.entities.AlarmSound
import net.harutiro.newalerm.features.alarm.repositories.AlarmRepository
import net.harutiro.newalerm.features.alarm.repositories.AlarmRepositoryImpl

/**
 * 「アラーム予定が有効」であることを表すフォアグラウンドサービス。
 *
 * - サービスが存在する間だけ、AlarmManager に予定が登録されている
 * - UI からのキャンセル指示（[ACTION_CANCEL_SCHEDULE]）、タスクスワイプ、onDestroy 時に
 *   すべての予定をキャンセルし、自身も停止する
 * - 稼働状態は [AlarmRepository] を通して UI 層へ共有する
 */
class AlarmForegroundService : Service() {

    private val scheduler: AlarmSchedulerApi by lazy { AlarmSchedulerApiImpl(this) }
    private val alarmRepository: AlarmRepository = AlarmRepositoryImpl()
    private val scheduledRequestCodes = mutableListOf<Int>()

    override fun onCreate() {
        super.onCreate()
        ensureChannel()
        startForegroundInternal(message = "アラーム予定なし")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_SCHEDULE -> {
                val config = intent.toAlarmConfig() ?: return START_NOT_STICKY
                cancelInternal()
                val result = scheduler.schedule(config)
                scheduledRequestCodes += result.requestCodes
                alarmRepository.notifyActivated(config)
                updateNotification(buildScheduleSummary(config, result.requestCodes.size))
            }

            ACTION_CANCEL_SCHEDULE -> {
                cancelInternal()
                stopSelf()
            }
        }
        // 自動再起動はさせない（プロセスが死ぬ＝予定終了 を表現するため）。
        return START_NOT_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        // タスクスワイプ = アプリ停止扱い。予定を全消ししてサービス終了。
        cancelInternal()
        stopSelf()
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        cancelInternal()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun cancelInternal() {
        if (scheduledRequestCodes.isNotEmpty()) {
            scheduler.cancelAll(scheduledRequestCodes.toList())
            scheduledRequestCodes.clear()
        }
        alarmRepository.notifyCleared()
    }

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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

    private fun startForegroundInternal(message: String) {
        val notification = buildNotification(message)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE,
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun updateNotification(message: String) {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIFICATION_ID, buildNotification(message))
    }

    private fun buildNotification(message: String): Notification {
        val openIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pi = PendingIntent.getActivity(
            this,
            0,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("アラーム予定")
            .setContentText(message)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(pi)
            .build()
    }

    private fun buildScheduleSummary(config: AlarmConfig, count: Int): String {
        val s = "%02d:%02d".format(config.startHour, config.startMinute)
        val e = "%02d:%02d".format(config.endHour, config.endMinute)
        return "$s〜$e / ${config.intervalMinutes}分間隔（${count}回）"
    }

    companion object {
        const val ACTION_START_SCHEDULE = "net.harutiro.newalerm.START_SCHEDULE"
        const val ACTION_CANCEL_SCHEDULE = "net.harutiro.newalerm.CANCEL_SCHEDULE"

        private const val CHANNEL_ID = "alarm_schedule_channel"
        private const val NOTIFICATION_ID = 1

        private const val EXTRA_START_HOUR = "start_hour"
        private const val EXTRA_START_MIN = "start_min"
        private const val EXTRA_END_HOUR = "end_hour"
        private const val EXTRA_END_MIN = "end_min"
        private const val EXTRA_INTERVAL = "interval"
        private const val EXTRA_REG_SOUND = "reg_sound"
        private const val EXTRA_FIN_SOUND = "fin_sound"
        private const val EXTRA_FIN_ENABLED = "fin_enabled"

        fun startIntent(context: Context, config: AlarmConfig): Intent =
            Intent(context, AlarmForegroundService::class.java).apply {
                action = ACTION_START_SCHEDULE
                putExtra(EXTRA_START_HOUR, config.startHour)
                putExtra(EXTRA_START_MIN, config.startMinute)
                putExtra(EXTRA_END_HOUR, config.endHour)
                putExtra(EXTRA_END_MIN, config.endMinute)
                putExtra(EXTRA_INTERVAL, config.intervalMinutes)
                putExtra(EXTRA_REG_SOUND, config.regularSound.serializeKey())
                putExtra(EXTRA_FIN_ENABLED, config.finalSound != null)
                putExtra(EXTRA_FIN_SOUND, config.finalSound?.serializeKey())
            }

        fun cancelIntent(context: Context): Intent =
            Intent(context, AlarmForegroundService::class.java).apply {
                action = ACTION_CANCEL_SCHEDULE
            }

        private fun Intent.toAlarmConfig(): AlarmConfig? {
            val startHour = getIntExtra(EXTRA_START_HOUR, -1)
            val startMin = getIntExtra(EXTRA_START_MIN, -1)
            val endHour = getIntExtra(EXTRA_END_HOUR, -1)
            val endMin = getIntExtra(EXTRA_END_MIN, -1)
            val interval = getIntExtra(EXTRA_INTERVAL, -1)
            if (startHour < 0 || startMin < 0 || endHour < 0 || endMin < 0 || interval <= 0) return null
            val regular = deserializeAlarmSound(getStringExtra(EXTRA_REG_SOUND))
            val finalEnabled = getBooleanExtra(EXTRA_FIN_ENABLED, false)
            val finalSound: AlarmSound? = if (finalEnabled) {
                deserializeAlarmSound(getStringExtra(EXTRA_FIN_SOUND))
            } else null
            return AlarmConfig(
                startHour = startHour,
                startMinute = startMin,
                endHour = endHour,
                endMinute = endMin,
                intervalMinutes = interval,
                regularSound = regular,
                finalSound = finalSound,
            )
        }
    }
}
