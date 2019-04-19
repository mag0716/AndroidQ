## Android Q privacy change: Restrictions to background activity starts

https://developer.android.com/preview/privacy/background-activity-starts

* Beta 2 変更点
  * ユーザ操作なしに Activity を起動したら影響する
  * Notification 契機の Activity で移行する
  * 開発者オプションで設定を変えられる
* Android Q では以下の条件で Activity を起動することができる
  * アプリが見える Window を持っている
  * フォアグラウンドのアプリが PendingIntent を利用
    * メニューなど
  * システムが PendingIntent を利用
    * Notification のタップなど
  * システムが SECRET_CODE_ACTION などの Broadcast を送信
* Note：Foreground Service はフォアグラウンドとは見なさない
* targetSdkVersion に関係なく Android Q で起動しているアプリに影響する

### Warning messages

* Beta2 ではバックグラウンドから起動しようとしたら warning が表示されるが、起動自体は行われる

### Create notifications for time-sensitive events

* 大体のケースでバックグラウンドでは Notification を通知すべき
* 着信など緊急でユーザに通知する必要がある場合は、後述する full-screen Intent を利用する

#### Create a high-priority notification

* `setFullScreenIntent` を full-screen Intent をオプションで提供できる
  * 優先度が高いもののみ利用すべき
    * [疑問] Notification の priority で動作が変わる？
  * `USE_FULL_SCREEN_INTENT` permission も必要

#### Display the notification to the user

* Notification を Foreground Service と関連付けすることができる
  * `startForeground`
* Note：システムは heads-up Notification を full-screen Notification の代わりに表示することがある

### Benefits of notifications

* ユーザに以下のメリットがある
  * 現在のコンテキストに沿った画面が表示されるので理解しやすくなる
    * [疑問] Notification だらけになったら同じことでは？
  * 着信やアラームが Do Not Disturb の設定に従うようになる
    * [理解] Activity だと防げない
  * デバイスを OFF にしている時にでも full-screen Intent は即座に起動する
  * デバイスの設定で最近の Notification を確認できるので設定を変えることができる

## Enable the behavior change

* Beta2 ではデフォルトでは有効になっていないので以下のいずれかの方法で有効にする
  * 開発者オプションの「Allow background activity starts」を OFF にする
  * `adb shell settings put global background_activity_starts_enabled 0`

## TODO

* 意図的に background から Activity を起動するサンプル
* full-screen Intent を試すサンプル
