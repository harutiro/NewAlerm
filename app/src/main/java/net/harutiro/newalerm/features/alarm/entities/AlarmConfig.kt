package net.harutiro.newalerm.features.alarm.entities

/**
 * ユーザーが設定するアラーム条件。
 *
 * @param startHour 開始時刻（時）
 * @param startMinute 開始時刻（分）
 * @param endHour 終了時刻（時）
 * @param endMinute 終了時刻（分）
 * @param intervalMinutes 鳴動間隔（分）
 * @param regularSound 通常のアラーム音
 * @param finalSound 最後のアラームで鳴らす音。null の場合は regularSound を使う
 *
 * 開始/終了は「本日中の時刻」として解釈する。
 * end が start 以前（同分含む）の場合は翌日扱いとして 24h オフセットする。
 */
data class AlarmConfig(
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int,
    val intervalMinutes: Int,
    val regularSound: AlarmSound = AlarmSound.SystemDefaultAlarm,
    val finalSound: AlarmSound? = AlarmSound.SystemRingtone,
) {
    init {
        require(intervalMinutes > 0) { "intervalMinutes must be > 0" }
        require(startHour in 0..23) { "startHour out of range" }
        require(endHour in 0..23) { "endHour out of range" }
        require(startMinute in 0..59) { "startMinute out of range" }
        require(endMinute in 0..59) { "endMinute out of range" }
    }

    val startTotalMinutes: Int get() = startHour * 60 + startMinute

    /** 日跨ぎを考慮した終了分。end <= start の場合は翌日扱い。 */
    val endTotalMinutesNormalized: Int
        get() {
            val raw = endHour * 60 + endMinute
            return if (raw <= startTotalMinutes) raw + 24 * 60 else raw
        }

    /** 指定範囲内に鳴る予定回数（start を含み、end も範囲内であれば含む）。 */
    val occurrenceCount: Int
        get() {
            val span = endTotalMinutesNormalized - startTotalMinutes
            return span / intervalMinutes + 1
        }
}
