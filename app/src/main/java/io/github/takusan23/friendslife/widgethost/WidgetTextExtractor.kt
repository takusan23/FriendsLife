package io.github.takusan23.friendslife.widgethost

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import io.github.takusan23.friendslife.R
import kotlinx.coroutines.delay

object WidgetTextExtractor {

    /**
     * Widgetを設置してテキストを抽出する
     *
     * @param context Context
     * @param appWidgetId ウイジェットのID
     * */
    suspend fun extract(context: Context, appWidgetId: Int): List<String> {
        val appWidgetManager by lazy { AppWidgetManager.getInstance(context) }
        val appWidgetHost by lazy { AppWidgetHost(context, R.string.app_name) }
        appWidgetHost.startListening()
        // ウイジェット情報
        val appWidgetInfo = appWidgetManager.getAppWidgetInfo(appWidgetId)
        // ウイジェットのView
        val hostView = appWidgetHost.createView(context, appWidgetId, appWidgetInfo)
        // 再帰的にViewを取得してすべてのViewを取る
        val resultViewList = arrayListOf<View>()
        fun findViewGroup(viewGroup: ViewGroup) {
            viewGroup.children.forEach { view ->
                resultViewList.add(view)
                if (view is ViewGroup) {
                    findViewGroup(view)
                }
            }
        }
        // 多分遅延実行したほうがいい
        if (hostView.rootView is ViewGroup) {
            delay(1000)
            findViewGroup(hostView.rootView as ViewGroup)
        }
        // テキストを取り出す
        val textList = resultViewList
            .filterIsInstance<TextView>()
            .map { textView -> textView.text.toString() }
        // 終了
        appWidgetHost.stopListening()
        return textList
    }
}