# Behavior changes for apps

## Behavior changes: all apps

https://developer.android.com/preview/behavior-changes-all

全てのアプリに影響する内容

### Non-SDK interface restrictions

* targetSdkVersion を更新していなければすぐには影響はない
* greylist にある API を使っているとクラッシュのリスクが高い
* 代替 API がない場合は [ここ](https://developer.android.com/distribute/best-practices/develop/restrictions-non-sdk-interfaces#feature-request) からリクエストできる

### Wi-Fi Direct broadcasts

* Wi-Fi Direct の broadcast が来なくなる
* `get()` で明示的に取得する必要がある

### SYSTEM_ALERT_WINDOW on Go devices

* Android Go のみの話
  * `SYSTEM_ALERT_WINDOW` permission を持つことができない
  * `ACTION_MANAGE_OVERLAY_PERMISSION` を送ると、許可されていないことを伝える設定画面へ自動的に遷移する
  * `Settings.canDrawOverlays()` は常に `false` になる

### Warnings for apps targeting older Android versions

* targetSdkVersion が 23(Android 6.0) 未満だと、アプリの初回起動時に警告が表示される
* permission の設定をユーザが選択できるようになる？
* [TODO] 試す

### App Usage

#### UsageStats app usage improvements

* 分割画面や Picture-in-Picuter 時に `UsageStats` が正確にトラッキングされるようになる
* Instant App でもトラッキングされる

#### Per-app grayscale

* ディスプレイを grayscale 表示に変更できる

#### Suspension improvements

* 一時停止中のアプリはオーディを再生できなくなる
* [疑問] 一時停止中とは？

## Android Q behavior changes: apps targeting Q

https://developer.android.com/preview/behavior-changes-q

targetSdkVersion を Q 以上にしたら影響する内容

### Updates to non-SDK interface restrictions

すでに記載済みのため省略

### Shared memory

* Ashmem が扱う dalvik maps のフォーマットが変わった
* 直接 ashmem を扱えなくなり、NDK の AshareMemory を使う必要がある

### Android runtime only accepts system-generated OAT files

* ART が dex2oat を呼び出さなくなる

### Enforcing AOT correctness in ART

カバーしていない領域なので省略

### Permissions changes for fullscreen intents

* **Notificationを全画面表示させるために USE_FULL_SCREEN_INTENT のリクエストが必要**
  * `USE_FULL_SCREEN_INTENT` は normal permission のため自動的に許可される
  * permission がない場合は無視される
