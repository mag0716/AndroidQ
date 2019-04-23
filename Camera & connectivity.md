# Android Q privacy: Changes to camera and connectivity

https://developer.android.com/preview/privacy/camera-connectivity

## Changes affecting all apps

### Access to all camera information requires permission

* `getCameraCharacteristics()` でカメラ情報をデフォルトで変更する
* カメラの仕様に関する情報は `CAMERA` パーミッションの取得が必須になった

### Restriction on enabling and disabling Wi-Fi

* Wi-Fi の有効/無効化が不可能になり、`WifiManager.setWifiEnabled` が常に `false` を返すようになった
  * もし、この機能が必要であれば、settings panel を利用する

## Changes affecting app targeting Android Q

### Wi-Fi network configuration restrictions

* 個人情報を保護するために Wi-Fi 一覧の手動設定はシステムによって制限される
* アプリの targetSdkVesrion が Q だと以下の情報が取得できなくなる
  * `getConfiguredNetworks()`
  * `addNetwork()`, `updateNetwork()`
  * `removeNetwork()`, `reassociate()`, `enableNetwork()`, `disableNetwork()`, `reconnect()`, `disconnect()`
* Wi-Fi に接続する場合は以下の代替手段を使う必要がある
  * `NetworkRequest` が持つ `WifiNetworkSpecifier` を利用し、Wi-Fi 接続する
  * Wi-Fi を追加するには `WifiNetworkSuggestion` の利用を検討する
    * `WifiManager.addNetworkSuggestions()`, `WifiManager.removeNetworkSuggestions()` で呼び出したダイアログ経由で行う
    * パーミッションは不要

### Fine location permission needed for telephony, Wi-Fi, Bluetooth APIs

* `ACCESS_FILE_LOCATION` を保持していても、いくつかのメソッドが使えなくなる
  * Wi-Fi
  * Wi-Fi Aware
  * Bluetooth
* Note：targetSdkVersion が Q 未満の場合は、`ACCESS_COARSE_LOCATION`, `ACCESS_FINE_LOCATION` を保持していたら取得可能
