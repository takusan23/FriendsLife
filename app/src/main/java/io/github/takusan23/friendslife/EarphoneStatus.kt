package io.github.takusan23.friendslife

import android.content.Context
import androidx.preference.PreferenceManager
import io.github.takusan23.friendslife.widgethost.WidgetTextExtractor
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * イヤホンの状態をウイジェットから取得するなど
 * */
object EarphoneStatus {

    /**
     * イヤホンの状態をウイジェットから取得します。サスペンド関数です。
     *
     * [WidgetTextExtractor.extract]をラップして使いやすくしただけ。
     *
     * @param context Context
     * @return 未接続時はnull。接続中は[EarphoneData]を返します。
     * */
    suspend fun getEarphoneData(context: Context): EarphoneData? {
        // ウイジェットのID
        val widgetId = PreferenceManager.getDefaultSharedPreferences(context).getInt("widget_id", -1)
        // ウイジェット内のテキスト取得
        val textList = WidgetTextExtractor.extract(context, widgetId)
        // 未接続時は情報取れないので
        return if (textList.size < 3) {
            // 未接続。nullを返す
            null
        } else {
            // 接続中。情報取り出し
            val name = textList[0]
            val left = textList[1]
            val right = textList[2]
            val case = textList[3]
            EarphoneData(name, left, right, case)
        }
    }

    /**
     * イヤホンの状態
     *
     * @param name イヤホンの名前。「WF-1000XM4」など
     * @param left 左のイヤホンの残量。文字列なのは％が入ってるから
     * @param right 右のイヤホンの残量。文字列なのは上記
     * @param case ケースの電池残量。文字列なのは以下略
     * @param leftNum [left]の数値版。100-0の間
     * @param rightNum [right]の数値版。100-0の間
     * @param caseNum [case]の数値版。100-0の間
     * */
    data class EarphoneData(
        val name: String,
        val left: String,
        val right: String,
        val case: String,
        val leftNum: Int = left.replace("%", "").toIntOrNull() ?: -1,
        val rightNum: Int = right.replace("%", "").toIntOrNull() ?: -1,
        val caseNum: Int = case.replace("%", "").toIntOrNull() ?: -1,
    )

}