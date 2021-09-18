package io.github.takusan23.friendslife

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.preference.PreferenceManager

/**
 * 端末起動時のブロードキャストを受け取る
 * */
class BootCompletedBroadCastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        val prefSetting = PreferenceManager.getDefaultSharedPreferences(context)
        val isAutoStartService = prefSetting.getBoolean("boot_completed", false)
        if (isAutoStartService) {
            // 端末起動時にサービス起動
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(Intent(context, EarphoneStatusService::class.java))
            } else {
                context.startService(Intent(context, EarphoneStatusService::class.java))
            }
        }
    }

}