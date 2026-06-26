package net.harutiro.newalerm.features.alarm.apis

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import net.harutiro.newalerm.features.alarm.entities.AlarmConfig
import net.harutiro.newalerm.features.alarm.entities.AlarmSound
import net.harutiro.newalerm.service.AlarmReceiver
import java.util.Calendar

/**
 * AlarmManager に対する薄いラッパ実装。
 * - 範囲内のすべてのアラーム時刻を算出して個別にスケジュールする
 * - 各アラームには「順番」「最後フラグ」を埋め込み、Receiver 側で取り出せるようにする
 *
 * このクラス自体は state を持たない。スケジュールした request code 一覧は呼び出し元
 * （Repository / サービス）で保持し、必要に応じてキャンセルする。
 */
class AlarmSchedulerApiImpl(
    private val context: Context,
) : AlarmSchedulerApi {

    override fun schedule(
        config: AlarmConfig,
        baseRequestCode: Int,
    ): AlarmSchedulerApi.Scheduled {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerTimes = computeTriggerTimes(config)
        val total = triggerTimes.size
        val requestCodes = mutableListOf<Int>()

        triggerTimes.forEachIndexed { index, triggerAtMillis ->
            val isFinal = index == total - 1
            val sound = if (isFinal && config.finalSound != null) {
                config.finalSound
            } else {
                config.regularSound
            }
            val requestCode = baseRequestCode + index
            val pendingIntent = buildPendingIntent(
                requestCode = requestCode,
                isFinal = isFinal,
                sound = sound,
                index = index,
                total = total,
            )
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent,
            )
            requestCodes += requestCode
        }
        return AlarmSchedulerApi.Scheduled(requestCodes)
    }

    override fun cancelAll(requestCodes: List<Int>) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        for (code in requestCodes) {
            val intent = Intent(context, AlarmReceiver::class.java)
            val pi = PendingIntent.getBroadcast(
                context,
                code,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
            )
            if (pi != null) {
                alarmManager.cancel(pi)
                pi.cancel()
            }
        }
    }

    private fun buildPendingIntent(
        requestCode: Int,
        isFinal: Boolean,
        sound: AlarmSound,
        index: Int,
        total: Int,
    ): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_FIRE
            putExtra(AlarmReceiver.EXTRA_IS_FINAL, isFinal)
            putExtra(AlarmReceiver.EXTRA_SOUND_KEY, sound.serializeKey())
            putExtra(AlarmReceiver.EXTRA_INDEX, index)
            putExtra(AlarmReceiver.EXTRA_TOTAL, total)
        }
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    /**
     * 開始から終了までの発火時刻リストを計算する。
     *
     * 動作:
     * - 開始時刻は本日のものを基準とし、すでに過去なら全体を翌日に繰り下げる
     * - end が start 以前なら end を翌日扱いとする ([AlarmConfig.endTotalMinutesNormalized])
     */
    private fun computeTriggerTimes(config: AlarmConfig): List<Long> {
        val now = Calendar.getInstance()
        val baseStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, config.startHour)
            set(Calendar.MINUTE, config.startMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (baseStart.timeInMillis <= now.timeInMillis) {
            baseStart.add(Calendar.DAY_OF_YEAR, 1)
        }
        val spanMinutes = config.endTotalMinutesNormalized - config.startTotalMinutes
        val results = mutableListOf<Long>()
        var offset = 0
        while (offset <= spanMinutes) {
            val time = (baseStart.clone() as Calendar).apply {
                add(Calendar.MINUTE, offset)
            }
            results += time.timeInMillis
            offset += config.intervalMinutes
        }
        return results
    }
}
