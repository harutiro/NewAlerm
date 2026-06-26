package net.harutiro.newalerm.features.alarm.repositories

import net.harutiro.newalerm.features.alarm.entities.AlarmSound

/**
 * アラーム音設定（通常音 / 最後だけ別音）の永続化を扱うリポジトリ。
 *
 * ホーム画面と音設定画面で同じ選択を共有するため、保存先を一元化する。
 */
interface AlarmSoundPreferenceRepository {

    /** 保存済みのアラーム音設定。 */
    data class SoundPreference(
        val regularSound: AlarmSound,
        val useFinalSound: Boolean,
        val finalSound: AlarmSound,
    )

    /** 現在の設定を読み込む。未保存の場合はデフォルトを返す。 */
    fun load(): SoundPreference

    /** 設定を保存する。 */
    fun save(preference: SoundPreference)
}
