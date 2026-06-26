package net.harutiro.newalerm.core.utils

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat

/**
 * アラーム予定の開始に必要な権限まわりのヘルパ。
 */
object AlarmPermissions {

    /** 通知権限（Android 13+）が未許可ならリクエストする。 */
    fun ensureNotificationPermission(
        context: Context,
        requestPermission: (String) -> Unit,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) requestPermission(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    /**
     * 正確なアラーム権限（Android 12+）を確認する。
     * 未許可の場合は設定画面へ誘導し false を返す。
     */
    fun ensureExactAlarmPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (am.canScheduleExactAlarms()) return true
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
            data = Uri.parse("package:${context.packageName}")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
        return false
    }
}
