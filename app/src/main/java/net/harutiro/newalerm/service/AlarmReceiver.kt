package net.harutiro.newalerm.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import net.harutiro.newalerm.core.presenter.ringing.page.AlarmRingingActivity

/**
 * AlarmManager から送出されるブロードキャストを受けて、
 * 全画面の [AlarmRingingActivity] を起動する。
 */
class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_FIRE) return

        val launchIntent = Intent(context, AlarmRingingActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_IS_FINAL, intent.getBooleanExtra(EXTRA_IS_FINAL, false))
            putExtra(EXTRA_SOUND_KEY, intent.getStringExtra(EXTRA_SOUND_KEY))
            putExtra(EXTRA_INDEX, intent.getIntExtra(EXTRA_INDEX, 0))
            putExtra(EXTRA_TOTAL, intent.getIntExtra(EXTRA_TOTAL, 0))
        }
        context.startActivity(launchIntent)
    }

    companion object {
        const val ACTION_FIRE = "net.harutiro.newalerm.ACTION_FIRE"
        const val EXTRA_IS_FINAL = "extra_is_final"
        const val EXTRA_SOUND_KEY = "extra_sound_key"
        const val EXTRA_INDEX = "extra_index"
        const val EXTRA_TOTAL = "extra_total"
    }
}
