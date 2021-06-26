package io.github.takusan23.friendslife

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Icon
import android.os.Build
import android.widget.ImageView
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService

object EarphoneStatusNotification {

    /** 通知ID */
    const val notificationId = 4

    /**
     * イヤホンの状態を表示した通知を作成する
     *
     * @param context Context
     * @param earphoneData イヤホンの状態
     * */
    fun earphoneStatusNotify(context: Context, earphoneData: EarphoneStatus.EarphoneData?): Notification {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "earphone_status_notification"
            // 通知チャンネル登録
            if (notificationManager.getNotificationChannel(channelId) == null) {
                val channel = NotificationChannel(channelId, "イヤホンの電池残量 - FriendsLife", NotificationManager.IMPORTANCE_DEFAULT)
                notificationManager.createNotificationChannel(channel)
            }
            Notification.Builder(context, channelId)
        } else {
            Notification.Builder(context)
        }
        // 通知に乗せるレイアウト
        if (earphoneData != null) {
            val customView = RemoteViews(context.packageName, R.layout.notification_earphone_battery).apply {
                setTextViewText(R.id.notification_earphone_battery_title, earphoneData.name)
                setTextViewText(R.id.notification_earphone_battery_left, earphoneData.left)
                setTextViewText(R.id.notification_earphone_battery_case, earphoneData.case)
                setTextViewText(R.id.notification_earphone_battery_right, earphoneData.right)
                // 更新ボタン
                setOnClickPendingIntent(R.id.notification_earphone_battery_sync, EarphoneStatusService.getBroadcastPendingIntent(context))
                if ((context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
                    // ダークモード時
                    setTextColor(R.id.notification_earphone_battery_title, Color.WHITE)
                    setTextColor(R.id.notification_earphone_battery_left, Color.WHITE)
                    setTextColor(R.id.notification_earphone_battery_case, Color.WHITE)
                    setTextColor(R.id.notification_earphone_battery_right, Color.WHITE)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        setImageViewIcon(R.id.notification_earphone_battery_left_icon, Icon.createWithResource(context, R.drawable.earphone_left).apply { setTintList(ColorStateList.valueOf(Color.WHITE)) })
                        setImageViewIcon(R.id.notification_earphone_battery_case_icon, Icon.createWithResource(context, R.drawable.earphone_case).apply { setTintList(ColorStateList.valueOf(Color.WHITE)) })
                        setImageViewIcon(R.id.notification_earphone_battery_right_icon, Icon.createWithResource(context, R.drawable.earphone_right).apply { setTintList(ColorStateList.valueOf(Color.WHITE)) })
                        setImageViewIcon(R.id.notification_earphone_battery_sync, Icon.createWithResource(context, R.drawable.ic_baseline_sync_24).apply { setTintList(ColorStateList.valueOf(Color.WHITE)) })
                    }
                } else {
                    // ライトテーマ
                    setTextColor(R.id.notification_earphone_battery_title, Color.BLACK)
                    setTextColor(R.id.notification_earphone_battery_left, Color.BLACK)
                    setTextColor(R.id.notification_earphone_battery_case, Color.BLACK)
                    setTextColor(R.id.notification_earphone_battery_right, Color.BLACK)
                }
            }
            notification.apply {
                setSmallIcon(R.drawable.ic_outline_headphones_battery_24)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    style = Notification.DecoratedCustomViewStyle()
                    setCustomContentView(customView)
                } else {
                    setContent(customView)
                }
            }
        } else {
            notification.apply {
                setSmallIcon(R.drawable.ic_outline_battery_unknown_24)
                setContentTitle("取得に失敗")
                style = Notification.BigTextStyle().apply {
                    this.bigText("イヤホンと未接続、取得に失敗、サービス起動中のどれかのため利用できません。")
                }
                addAction(Notification.Action.Builder(R.drawable.ic_baseline_sync_24, "再取得", EarphoneStatusService.getBroadcastPendingIntent(context)).build())
            }
        }
        return notification.build()
    }

}