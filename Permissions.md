# Android Q privacy: Changes to permissions

https://developer.android.com/preview/privacy/permissions

## Changes affecting all apps

### Restricted access to screen contents

* ユーザのスクリーン情報を保護するために、`READ_FRAME_BUFFER`, `CAPTURE_VIDEO_OUTPUT`, `CAPTURE_SECURE_VIDEO_UPDATE` によってサイレントでのアクセスを防ぐ
  * プリインアプリのみ
* スクリーン情報にアクセスするためには `MediaProjection` API を使う

### User-facing permission check on legacy apps

* targetSdkVersion が 22 以下の場合、パーミッションの画面が初回に表示されるようになった

### Permission groups removed from UI

* UI でどのようにパーミッションがグループ化されているかを調べられなくなった
  * [疑問] 詳細が不明。端末の設定画面のこと？

## Changes affecting apps targeting Android Q

### Physical activity recognition

* ユーザ活動を検知するために `ACTIVITY_RECOGNITION` が必要になった
* Note：Activity Recognition API のような Google Play service を使ういくつかのライブラリは権限なしには結果を返さなくなる
