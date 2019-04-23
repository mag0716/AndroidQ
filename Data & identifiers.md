# Android Q privacy: Changes to data and identifiers

https://developer.android.com/preview/privacy/data-identifiers

## Changes affecting all apps

### Contacts affinity

* 連絡先の血縁関係を保存しなくなり、親しい順にソートなどしなくなる

### Randomized MAC addresses

* Android Q の端末はデフォルトでランダムな MAC アドレスを送る
* エンタープライズなユースケースを扱う場合は新 API を利用する
  * `getRandomizedMacAddress()`
  * `getWifiMacAddress()`
    * [疑問] Device owner apps, profile owner apps ってどういうの？
    -> https://source.android.com/devices/tech/admin/managed-profiles#device_administration

### Access to /proc/net filesystem

* デバイスのネットワーク状態を保持している /proc/net へのアクセスが不可になった
  * VPNs などへアクセスする場合は `NetworkStatsManager`, `ConnectivitiManager` からアクセスする

### Non-resettable device identifiers

* リセット不可なデバイス識別子(IMEI, シリアルナンバー)へのアクセスには `READ_PRIVILEGED_PHONE_STATE` の保持が必須になった
* `READ_PRIVILAGED_PHONE_STATE` なしでアクセスすると
  * targetSdkVersion が Q：`SecurityException`
  * targetSdkVersion が Q未満：null が返却されるか `READ_PHONE_STATE` を保持している場合にはプレースホルダーデータが返却される。それ以外は `SecurityException`
* Note：device or profile owner app の場合は、`READ_PHONE_STATE` のみでよい。[special carrier permissions](https://source.android.com/devices/tech/config/uicc) を保持している場合にはパーミッションは不要
* 広告のために利用している場合は、Advertising ID の利用するべき

### Access to clipboard data

* デフォルトの IME や起動中のアプリでもクリップボードのデータにアクセスすることは不可になった
  * [疑問] [リファレンス](https://developer.android.com/reference/android/content/ClipboardManager.html) はまだ更新されてないだけ？

## Changes affecting apps targeting Android Q

### Access to USB serial requires user permission

* シリアル番号の取得は USB デバイスへのアクセスのための permission 取得後にしか行えなくなった
