package io.github.takusan23.friendslife

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import io.github.takusan23.friendslife.databinding.ActivityMainBinding
import io.github.takusan23.friendslife.widgethost.WidgetTextExtractor
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val appWidgetManager by lazy { AppWidgetManager.getInstance(this) }
    private val appWidgetHost by lazy { AppWidgetHost(this, R.string.app_name) }
    private val prefSetting by lazy { PreferenceManager.getDefaultSharedPreferences(this) }
    private val viewBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val REQUEST_PICK_WIDGET = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)


        // 未設定時のみ
        val widgetId = prefSetting.getInt("widget_id", -1)

        if (widgetId == -1) {
            // ウイジェットピッカーでSonyのイヤホンアプリを選んでもらう
            showWidgetPicker()
        } else {
            // サービス起動と終了ボタン初期化
            startService()
            viewBinding.activityMainStopServiceButton.setOnClickListener {
                stopService(Intent(this, EarphoneStatusService::class.java))
            }
            // うまく動かない場合は設定をリセットする
            viewBinding.activityMainFactoryResetButton.setOnClickListener {
                prefSetting.edit { remove("widget_id") }
                // 再度選んでもらう
                showWidgetPicker()
            }
            // 端末の起動時にサービスを起動する
            viewBinding.activityMainBootCompleted.isChecked = prefSetting.getBoolean("boot_completed", false)
            viewBinding.activityMainBootCompleted.setOnCheckedChangeListener { buttonView, isChecked ->
                prefSetting.edit { putBoolean("boot_completed", isChecked) }
            }
        }

    }

    /** ウイジェットピッカーを出す */
    private fun showWidgetPicker() {
        // ウイジェットピッカーでSonyのイヤホンアプリを選んでもらう
        Toast.makeText(this, "Sonyのイヤホンアプリのウイジェットを選択してください", Toast.LENGTH_SHORT).show()
        val appWidgetId = appWidgetHost.allocateAppWidgetId()
        val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_PICK).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        startActivityForResult(intent, REQUEST_PICK_WIDGET)
    }

    /** イヤホンのバッテリー残量取得サービスを起動する */
    private fun startService() {
        val serviceIntent = Intent(this, EarphoneStatusService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 成功時
        if (resultCode == RESULT_OK && requestCode == REQUEST_PICK_WIDGET) {
            val extras = data!!.extras!!
            val appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
            // ウイジェットのIDを保存する
            prefSetting.edit { putInt("widget_id", appWidgetId) }
            // サービス起動
            startService()
        }
    }

}