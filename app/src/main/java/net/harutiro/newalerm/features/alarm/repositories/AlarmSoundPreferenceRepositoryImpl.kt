package net.harutiro.newalerm.features.alarm.repositories

import android.content.Context
import androidx.core.content.edit
import net.harutiro.newalerm.features.alarm.apis.deserializeAlarmSound
import net.harutiro.newalerm.features.alarm.apis.serializeKey
import net.harutiro.newalerm.features.alarm.entities.AlarmSound

/**
 * SharedPreferences へアラーム音設定を保存する [AlarmSoundPreferenceRepository] 実装。
 */
class AlarmSoundPreferenceRepositoryImpl(
    context: Context,
) : AlarmSoundPreferenceRepository {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    override fun load(): AlarmSoundPreferenceRepository.SoundPreference {
        val regular = deserializeAlarmSound(
            prefs.getString(KEY_REGULAR, null),
        ).takeIfStored(KEY_REGULAR) ?: AlarmSound.SystemDefaultAlarm
        val useFinal = prefs.getBoolean(KEY_USE_FINAL, true)
        val final = deserializeAlarmSound(
            prefs.getString(KEY_FINAL, null),
        ).takeIfStored(KEY_FINAL) ?: AlarmSound.SystemRingtone
        return AlarmSoundPreferenceRepository.SoundPreference(
            regularSound = regular,
            useFinalSound = useFinal,
            finalSound = final,
        )
    }

    override fun save(preference: AlarmSoundPreferenceRepository.SoundPreference) {
        prefs.edit {
            putString(KEY_REGULAR, preference.regularSound.serializeKey())
            putBoolean(KEY_USE_FINAL, preference.useFinalSound)
            putString(KEY_FINAL, preference.finalSound.serializeKey())
        }
    }

    /** 未保存（キーが無い）の場合は null を返し、呼び出し側のデフォルトを使わせる。 */
    private fun AlarmSound.takeIfStored(key: String): AlarmSound? =
        if (prefs.contains(key)) this else null

    companion object {
        private const val PREF_NAME = "alarm_sound_preference"
        private const val KEY_REGULAR = "regular_sound"
        private const val KEY_USE_FINAL = "use_final_sound"
        private const val KEY_FINAL = "final_sound"
    }
}
