package net.harutiro.newalerm.features.alarm.apis

import net.harutiro.newalerm.features.alarm.entities.AlarmSound

/** [AlarmSound] を Intent extra / 永続化で受け渡しできる文字列キーへ変換する。 */
fun AlarmSound.serializeKey(): String = when (this) {
    AlarmSound.SystemDefaultAlarm -> "system_alarm"
    AlarmSound.SystemRingtone -> "system_ringtone"
    AlarmSound.SystemNotification -> "system_notification"
}

/** 文字列キーから復元するための逆変換。未知キーはデフォルト音にフォールバック。 */
fun deserializeAlarmSound(key: String?): AlarmSound = when (key) {
    "system_ringtone" -> AlarmSound.SystemRingtone
    "system_notification" -> AlarmSound.SystemNotification
    else -> AlarmSound.SystemDefaultAlarm
}
