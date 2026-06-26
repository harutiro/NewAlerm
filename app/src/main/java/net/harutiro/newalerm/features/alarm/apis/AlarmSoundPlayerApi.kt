package net.harutiro.newalerm.features.alarm.apis

import net.harutiro.newalerm.features.alarm.entities.AlarmSound

/**
 * アラーム音の再生抽象。
 * 将来カスタム楽曲を扱う場合も、この interface を実装することで MVVM 層を変えずに差し替え可能。
 */
interface AlarmSoundPlayerApi {
    /** 指定の音をループ再生する。すでに再生中なら一度停止して切り替える。 */
    fun play(sound: AlarmSound)

    /** 再生を停止する。再生中でない場合は no-op。 */
    fun stop()
}
