# Introducing Android Q Beta

https://android-developers.googleblog.com/2019/03/introducing-android-q-beta.html

* Building on top of privacy protections in Android
  * Project Strobe
  * **位置情報の取得に関する権限が細かく設定できるようになった**
    * iOS のようにアプリを使っている間だけ許可するが可能に
    * [TODO] [詳細](https://developer.android.com/preview/privacy/device-location)
  * 共有ファイルへのアクセス権限を細かく設定できるようになる
    * `MediaStore.Images`, `MediaStore.Video` などアクセス可能なディレクトリを細かく設定できる
    * [TODO] [詳細](https://developer.android.com/preview/privacy/scoped-storage)
  * アプリバックグラウンド中の Activity の起動が抑制される
    * **priority の高い Notification で全画面表示が実現できるようになる**
    * [TODO] [詳細](https://developer.android.com/preview/privacy/background-activity-starts)
  * IMEI などリセットできない値へのアクセスが制限される
    * 取得には `READ_PRIVILEGED_PHONE_STATE` が必要
    * [詳細](https://developer.android.com/preview/privacy/data-identifiers#device-ids)

* New ways to engage users
  * Foldable
    * 複数のアプリで同時に `onResume`, `onPause` が走ることがある
    * `resizableActivity` の動作も変わる
    * エミュレータは準備中
  * Sharing shortcuts
    * 共有時に表示されるアプリ一覧にショートカットを追加できる
    * 既存の API である `ShortcutInfo` が拡張される
    * [TODO] [サンプル](https://github.com/googlesamples/android-SharingShortcuts)
  * Settings Panels
    * アプリ起動中に Settings Panels を表示できる
      * Wi-Fi、NFC、音量などの設定が変えられる
    * アプリを離れなくても端末の設定を変えることができるようになる

* Connectivity
  * Connectivity permissions, privacy, and security
    * FILE location permission が必須になる
    * 異なる Wi-Fi 接続時に、ランダムな MAC アドレスが提供される
  * Improved peer-to-peer internet connectivity
    * IoT デバイスへの接続が簡単になる
    * location permission の不用になる
  * Wi-Fi performance mode
    * high performance なモードが有効にできる

* Camera, media, graphics

カバーしていない領域なので省略

* ANGLE on Vulkan

カバーしていない領域なので省略

* Neural Networks API 1.2

カバーしていない領域なので省略

* Strengthening Android's Foundations
  * ART performance
    * ART runtime のパフォーマンスが改善された
    * アプリの起動が早くなる
    * 世代別 GC の追加
  * Security for apps
    * BiometricPrompt に顔認証がサポートされる
    * TLS 1.3 がサポート
  * Compatibility through public APIs
    * Android P から始まった hide API の利用制限を増やす
      * https://developer.android.com/preview/non-sdk-q#greylist-now-restricted
  * Modern Android
    * targetSdkVersion が古いとアプリの初回起動時に警告されるようになる
