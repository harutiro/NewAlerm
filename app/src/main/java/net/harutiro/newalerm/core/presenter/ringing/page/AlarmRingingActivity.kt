package net.harutiro.newalerm.core.presenter.ringing.page

import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.harutiro.newalerm.core.presenter.ringing.viewModel.AlarmRingingViewModel
import net.harutiro.newalerm.features.alarm.apis.SystemAlarmSoundPlayerApiImpl
import net.harutiro.newalerm.features.alarm.apis.deserializeAlarmSound
import net.harutiro.newalerm.service.AlarmReceiver
import net.harutiro.newalerm.ui.theme.NewAlermTheme

/**
 * フルスクリーンで表示される鳴動中アラーム画面。
 *
 * - ロック画面の上に出る (showWhenLocked) ／ 画面をオンにする (turnScreenOn)
 * - 物理ボタン (Volume / Back) では停止できないようにキー入力をブロック
 * - 「停止」ボタンタップでのみ finish
 */
class AlarmRingingActivity : ComponentActivity() {

    private lateinit var viewModel: AlarmRingingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupWindowFlags()

        val isFinal = intent.getBooleanExtra(AlarmReceiver.EXTRA_IS_FINAL, false)
        val sound = deserializeAlarmSound(intent.getStringExtra(AlarmReceiver.EXTRA_SOUND_KEY))
        val index = intent.getIntExtra(AlarmReceiver.EXTRA_INDEX, 0)
        val total = intent.getIntExtra(AlarmReceiver.EXTRA_TOTAL, 0)

        val player = SystemAlarmSoundPlayerApiImpl(applicationContext)
        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AlarmRingingViewModel(player) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[AlarmRingingViewModel::class.java]
        viewModel.start(sound, isFinal, index, total)

        setContent {
            NewAlermTheme {
                AlarmRingingScreen(
                    viewModel = viewModel,
                    onStopped = { finish() },
                )
            }
        }
    }

    private fun setupWindowFlags() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    /** Back キーで閉じられないようにする。 */
    @Deprecated("intentionally suppressing default back behavior")
    override fun onBackPressed() {
        // no-op: 停止ボタンからのみ閉じる
    }

    /** 音量ボタンなどでの停止操作を無効化（音量変更自体は OS が処理する範囲は通す）。 */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) return true
        return super.onKeyDown(keyCode, event)
    }
}
