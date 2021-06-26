# FriendsLife

WF-1000XM4のノイキャンに感動したので常に通知領域に電池残量を表示するように  
今の所自動更新機能はないです。

<img src="https://imgur.com/pRuJsPl.png" width="300">

# ダウンロード
https://github.com/takusan23/FriendsLife/releases

`app-release.apk`をダウンロードしてください。

## 仕組み？
これ。ウイジェットから電池残量が見れるのでそれを取得するように。  
別にBluetoothで通信してるとかではない。

https://github.com/takusan23/AndroidGetWidgetText

## その他
他に取得方法無いのって話ですが、

- BluetoothDevice#getBatteryLevel()
  - 大雑把でしか取れない。他のデバイスでも対応させたい場合はこれしか無いけど。
    - 多分クイック設定パネルに表示されてる残量と同じ値になると思われ
  - ついでに`@UnsupportedAppUsage`アノテーションのせいでリフレクションしないと行けない
- 設定アプリのソースコードを読め
  - `有効、L: 電池残量100% R: 電池残量100%`みたいなやつ
  - `BluetoothUtils.getIntMetaData(mDevice,BluetoothDevice.METADATA_UNTETHERED_LEFT_BATTERY)`
    - `BluetoothDevice#getMetadata()`がサードパーティに開放されていないので無理。親切に`@SystemApi`で保護されてる。
      - https://cs.android.com/android/platform/superproject/+/master:frameworks/base/core/java/android/bluetooth/BluetoothDevice.java;drc=master;l=2436
    - ついでにプリインストールアプリ限定権限 Manifest.permission.BLUETOOTH_PRIVILEGED で保護されてもいるので確実に無理。

## 終わりに
Xperia 5 Ⅱのみ動作確認済。  
OneUIとかのガラッと見た目変えてくるROMの場合はうまく通知が出るかわからん。
