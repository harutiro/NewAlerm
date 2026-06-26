package net.harutiro.newalerm.features.alarm.apis

import net.harutiro.newalerm.features.alarm.entities.AlarmConfig

/**
 * AlarmManager への予定登録/解除を抽象化したデータソース。
 *
 * interface に切り出すことで、上位層（Repository）からはモックへ差し替え可能にしている。
 */
interface AlarmSchedulerApi {

    /** スケジュール結果。キャンセルに必要な request code 一覧を返す。 */
    data class Scheduled(val requestCodes: List<Int>)

    /**
     * 設定範囲内に発火するアラームを登録する。
     *
     * @param baseRequestCode リクエストコードの開始値。連番で消費される。
     */
    fun schedule(config: AlarmConfig, baseRequestCode: Int = REQUEST_CODE_BASE): Scheduled

    /** 指定の request code 一覧をすべてキャンセルする。 */
    fun cancelAll(requestCodes: List<Int>)

    companion object {
        const val REQUEST_CODE_BASE = 10_000
    }
}
