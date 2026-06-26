package net.harutiro.newalerm.core.entities

/**
 * アプリ内のナビゲーション経路定義。
 */
enum class ScreenRoute(val route: String) {
    /** ホーム（時刻範囲・間隔の設定／稼働中表示）。 */
    Home("home"),

    /** アラーム音設定。 */
    SoundSetting("sound_setting"),
}
