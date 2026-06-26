package net.harutiro.newalerm.features.alarm.entities

/**
 * 鳴らすアラーム音の種類。
 *
 * 将来的に「カスタム楽曲（端末内のmp3、内蔵リソースなど）」へ拡張できるよう sealed interface にしている。
 * 拡張例:
 *   data class RawResource(val resId: Int, override val label: String) : AlarmSound
 *   data class Custom(val uri: String, override val label: String) : AlarmSound
 *
 * 追加した実装は [AlarmSound.selectable] に列挙すれば、UIピッカーへ自動で並ぶ。
 */
sealed interface AlarmSound {
    val label: String

    /** 端末標準のアラーム音 (RingtoneManager.TYPE_ALARM)。 */
    data object SystemDefaultAlarm : AlarmSound {
        override val label: String = "標準アラーム音"
    }

    /** 端末標準の着信音 (RingtoneManager.TYPE_RINGTONE)。最後のアラーム用の差別化音として利用。 */
    data object SystemRingtone : AlarmSound {
        override val label: String = "標準着信音"
    }

    /** 端末標準の通知音 (RingtoneManager.TYPE_NOTIFICATION)。 */
    data object SystemNotification : AlarmSound {
        override val label: String = "標準通知音"
    }

    companion object {
        /** UIピッカーで列挙する候補。新しい音源を追加したらここに足す。 */
        val selectable: List<AlarmSound> = listOf(
            SystemDefaultAlarm,
            SystemRingtone,
            SystemNotification,
        )
    }
}
