package net.harutiro.newalerm.core.router

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import net.harutiro.newalerm.core.entities.ScreenRoute
import net.harutiro.newalerm.core.presenter.home.page.HomePage
import net.harutiro.newalerm.core.presenter.soundSetting.page.SoundSettingPage
import net.harutiro.newalerm.core.utils.AlarmPermissions
import net.harutiro.newalerm.features.alarm.entities.AlarmConfig
import net.harutiro.newalerm.features.alarm.repositories.AlarmRepository
import net.harutiro.newalerm.features.alarm.repositories.AlarmRepositoryImpl

/**
 * アプリ全体のナビゲーションを定義する Router。
 *
 * 権限要求とアラーム予定の開始/キャンセル（[AlarmRepository] 経由）はここで束ねる。
 */
@Composable
fun Router(
    alarmRepository: AlarmRepository = AlarmRepositoryImpl(),
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { /* 結果に関わらず続行（要件上、致命的ではない） */ }

    fun startSchedule(config: AlarmConfig) {
        AlarmPermissions.ensureNotificationPermission(
            context = context,
            requestPermission = notificationPermissionLauncher::launch,
        )
        if (!AlarmPermissions.ensureExactAlarmPermission(context)) return
        alarmRepository.startSchedule(context, config)
    }

    NavHost(
        navController = navController,
        startDestination = ScreenRoute.Home.route,
    ) {
        composable(ScreenRoute.Home.route) {
            HomePage(
                onStartSchedule = ::startSchedule,
                onCancelSchedule = { alarmRepository.cancelSchedule(context) },
                onOpenSoundSetting = { navController.navigate(ScreenRoute.SoundSetting.route) },
            )
        }
        composable(ScreenRoute.SoundSetting.route) {
            SoundSettingPage(
                onBack = { navController.popBackStack() },
            )
        }
    }
}
