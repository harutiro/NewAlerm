package net.harutiro.newalerm.features.alarm.apis

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import net.harutiro.newalerm.features.alarm.entities.AlarmSound

/**
 * 端末標準音 (RingtoneManager) を MediaPlayer で再生する [AlarmSoundPlayerApi] 実装。
 * - フリー音源を別途用意しなくても動作する
 * - 音量はシステムのアラームストリームに従う
 */
class SystemAlarmSoundPlayerApiImpl(
    private val context: Context,
) : AlarmSoundPlayerApi {

    private var mediaPlayer: MediaPlayer? = null

    override fun play(sound: AlarmSound) {
        stop()
        val uri = sound.toUri() ?: fallbackUri() ?: return
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            setDataSource(context, uri)
            isLooping = true
            prepare()
            start()
        }
    }

    override fun stop() {
        mediaPlayer?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        mediaPlayer = null
    }

    private fun AlarmSound.toUri(): Uri? = when (this) {
        AlarmSound.SystemDefaultAlarm ->
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        AlarmSound.SystemRingtone ->
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        AlarmSound.SystemNotification ->
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    }

    private fun fallbackUri(): Uri? =
        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
}
