package io.github.takusan23.friendslife

import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * イヤホンの電池残量をウイジェットから取得するサービス
 *
 * ぶっちゃけウイジェットである必要もない。けどなにかするかもしれないので
 *
 * # ブロードキャスト
 *
 * このクラスへ向かってブロードキャストを飛ばすと通知の内容を更新できます。（通知上にある更新ボタンを押したときに更新できるのはブロードキャストを利用しているから）
 * */
class EarphoneStatusService : Service() {

    /** ブロードキャストを受信できるように。通知からのイベントはブロードキャストを経由する必要あり */
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateNotification()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // サービス確保
        startForeground(EarphoneStatusNotification.notificationId, EarphoneStatusNotification.earphoneStatusNotify(this, null))

        // ブロードキャスト受信準備
        initBroadcast()
        // データ取得
        updateNotification()

        return START_NOT_STICKY
    }

    /** ブロードキャスト初期化 */
    private fun initBroadcast() {
        val intentFilter = IntentFilter("update")
        registerReceiver(broadcastReceiver, intentFilter)
    }

    /** 最新の情報に更新する */
    private fun updateNotification() {
        GlobalScope.launch {
            val earphoneData = EarphoneStatus.getEarphoneData(this@EarphoneStatusService)
            startForeground(EarphoneStatusNotification.notificationId, EarphoneStatusNotification.earphoneStatusNotify(this@EarphoneStatusService, earphoneData))
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }

    companion object {

        /** このクラスへブロードキャストを送信するためのPendingIntentを生成する */
        fun getBroadcastPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getBroadcast(context, 114, Intent("update"), PendingIntent.FLAG_UPDATE_CURRENT)
        }

    }

}