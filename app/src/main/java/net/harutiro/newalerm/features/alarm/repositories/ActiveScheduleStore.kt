package net.harutiro.newalerm.features.alarm.repositories

import android.content.Context
import androidx.core.content.edit
import net.harutiro.newalerm.features.alarm.apis.deserializeAlarmSound
import net.harutiro.newalerm.features.alarm.apis.serializeKey
import net.harutiro.newalerm.features.alarm.entities.AlarmConfig

/**
 * 稼働中のアラーム予定を永続化するストア。
 *
 * フォアグラウンドサービスを使わない構成では「予定が有効かどうか」の source of truth を
 * プロセスの生存に頼れない。そのため予定内容とキャンセルに必要な request code を
 * SharedPreferences に保存し、プロセス再生成後も復元・解除できるようにする。
 */
class ActiveScheduleStore(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    /** 予定と、キャンセルに必要な request code 一覧を保存する。 */
    fun save(config: AlarmConfig, requestCodes: List<Int>) {
        prefs.edit {
            putBoolean(KEY_ACTIVE, true)
            putInt(KEY_START_HOUR, config.startHour)
            putInt(KEY_START_MIN, config.startMinute)
            putInt(KEY_END_HOUR, config.endHour)
            putInt(KEY_END_MIN, config.endMinute)
            putInt(KEY_INTERVAL, config.intervalMinutes)
            putString(KEY_REG_SOUND, config.regularSound.serializeKey())
            putString(KEY_FIN_SOUND, config.finalSound?.serializeKey())
            putString(KEY_REQUEST_CODES, requestCodes.joinToString(","))
        }
    }

    /** 保存済みの予定を返す。未保存または不正な場合は null。 */
    fun loadConfig(): AlarmConfig? {
        if (!prefs.getBoolean(KEY_ACTIVE, false)) return null
        return runCatching {
            AlarmConfig(
                startHour = prefs.getInt(KEY_START_HOUR, 0),
                startMinute = prefs.getInt(KEY_START_MIN, 0),
                endHour = prefs.getInt(KEY_END_HOUR, 0),
                endMinute = prefs.getInt(KEY_END_MIN, 0),
                intervalMinutes = prefs.getInt(KEY_INTERVAL, 1),
                regularSound = deserializeAlarmSound(prefs.getString(KEY_REG_SOUND, null)),
                finalSound = prefs.getString(KEY_FIN_SOUND, null)
                    ?.let { deserializeAlarmSound(it) },
            )
        }.getOrNull()
    }

    /** 保存済みの request code 一覧を返す。未保存なら空。 */
    fun loadRequestCodes(): List<Int> =
        prefs.getString(KEY_REQUEST_CODES, null)
            ?.split(",")
            ?.mapNotNull { it.toIntOrNull() }
            ?: emptyList()

    /** 保存内容をすべて消す。 */
    fun clear() {
        prefs.edit { clear() }
    }

    companion object {
        private const val PREF_NAME = "active_schedule"
        private const val KEY_ACTIVE = "active"
        private const val KEY_START_HOUR = "start_hour"
        private const val KEY_START_MIN = "start_min"
        private const val KEY_END_HOUR = "end_hour"
        private const val KEY_END_MIN = "end_min"
        private const val KEY_INTERVAL = "interval"
        private const val KEY_REG_SOUND = "reg_sound"
        private const val KEY_FIN_SOUND = "fin_sound"
        private const val KEY_REQUEST_CODES = "request_codes"
    }
}
