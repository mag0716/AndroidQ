# Android Q features and APIs

https://developer.android.com/preview/features

### Security enhancements

#### improved biometric authentication dialogs

* 生体認証のダイアログが改善される

##### Specify user confirmation requirements

* 認証後にユーザ確認が必要かどうかをシステムに対して渡せるようになった
  * `setRequireConfirmation`
  * リスクが低い操作に対して使う
  * [疑問] ユースケースが不明

##### Improved fallback support for device credentials

* 生体認証失敗後に、PIN、パスワードなど認証を求めるようにできる
  * `setAllowDeviceCredential`
  * `createConfirmDeviceCredentialIntent()` で自前実装していたのが不用になる

##### Check device for biometric capability

* 生体認証に端末が対応しているかを簡単にチェックできるようになる
  * `BiometricPrompt#canAuthenticate()`

#### Run embedded DEX code directly from APK

* 改ざん検知のために APK に埋め込んだ DEX コードを実行できるようになる
  * この機能を有効にするとパフォーマンスに影響する可能性があるので注意
  * `android:useEmbeddedDex`

#### TLS 1.3 support

* TLS 1.2 と比べると 40% 以上早くなっている
* 全ての TLS 接続で 1.3 がデフォルトで有効になる
* TLS 1.3 を無効化する場合は、`SSLContext.getInstance("TLSv1.2")` などで
* `setEnabledProtocols()` で接続前に有効・無効を切り替えられる
* cipher suits のカスタマイズは不可
  * `setEnabledCipherSuits()` は無視される
* セッションがキャッシュに追加される前に `HandshakeCompletedListeners` が呼ばれる
  * TLS 1.2 などの動作とは異なる
* SSLEngine が `SSLHandshakeException` 前に `SSLProtocolException` をスローするケースがある
* 0-RTT はサポートされない

#### Public Conscrypt API

* 以前はリフレクションで利用していた TLS 機能のための API が公開された
  * greylist に含まれているのでリフレクションの使用は停止した方がよい
  * android.net.ssl

#### ELF TLS

NDK 絡みでカバーしていない領域なので省略

### Connectivity features

#### Wi-Fi network connection API

* P2P 接続がサポート
* Chromecast のようなデバイスの起動設定などの目的で利用される

#### Wi-Fi network suggestion API

* アプリ上で Wi-Fi のアクセスポイント接続を行わせる機能をサポート
  * おすすめのネットワークを指定できる
  * アプリごとに異なるアクセスポイントに接続させることが可能
  * システムが接続するためにはユーザが承認している必要がある
* [疑問] どういう見た目なのか？

#### Improvements to Wi-Fi high-performancee and low-latency modes

* Wi-Fi lock API が拡張される
* リアルタイムなゲームなどで便利

#### Specialized lockups in DNS resolver

* DNS over TLS がサポート

#### Wi-Fi Easy Connect

* URI を利用して Wi-Fi 認証し、接続を簡単にすることできる
  * URI は QR コードや Bluetooth や NFC などを通じて取得できる
  * URI が有効になると `ACTION_PROCESS_WIFI_EASY_CONECT_QR_CODE` を利用して認証する
  * `WifiManager.isEasyConnectSupported()` でサポートされているか確認が必要

#### Wi-Fi Direct connection API

* Wi-Fi Direct のために `WifiP2pConfig`, `WifiP2pManager` が更新された

#### Bluetooth LE Connection Oriented Channels(CoC)

* BLE デバイス間でサイズの大きいデータの転送をするための BLE CoC が有効化された

### Telephony features

#### Call quality improvements

* 着信中の IMS の品質を取得できるようになった
  * 端末がサポートしていれば取得可能

#### Call screening and caller ID

* アドレス帳からスパムかどうかを検証できるようになった
  * サイレントで拒否することも可能
  * ブロックした情報はログとして残る
    * `READ_CALL_LOG` permission が必須な API を使えば削除することも可能

#### Call redirection service API

* call Intent の扱いが変わる
  * `NEW_OUTGOING_CALL` が deprecated になり、`CallRedirectionService` に置き換えられる

### Media and graphics

#### Native MIDI API

* カバーしていない領域なので省略

#### MediaCodecInfo Improvements

* カバーしていない領域なので省略

#### Monochrome camera support

* モノクロカメラのサポートが改善される

#### Dynamic Depth Format

* カバーしていない領域なので省略

#### ANGLE

* カバーしていない領域なので省略

### Accessibility services API

#### AccessibilityNodeInfo entry key flag

* `AccessibilityNodeInfo` が改善される

#### Accessibility dialog spoken feedback

* アクセシビリティサービス開始を繰り返すとテキストを読み上げるプロンプトが表示されるようになった？

#### Accessibility shortcut for physical keyboards

* 物理キーボード上からショートカットを起動できるようになった

#### Soft keyboard controller enhancement

* 物理キーボードを接続している時でもソフトウェアキーボードにリクエストするようになる

#### User-defined accessibility timeouts

* `AccessibilityManager.getRecommendedTimeoutMillis()`
  * タイムアウト値をユーザが設定できるようになった

### Autofill improvements

[TODO] Autofill サンプル作る

#### Compatibility-related autofill requests

* `FillRequest.FLAG_COMPATIBILITY_MODE_REQUEST`
  * 互換性モードで生成するかどうかを決定する
  * [疑問] 互換性モードってなに？

#### Save username and password simultaneously

* `SaveInfo.FLAG_DELAY_SAVE`
  * ユーザ名やパスワードが別の画面で利用するケースがサポートされた
  * ユーザ名、パスワードの保存タイミングを変えられる

#### User interaction with the Save UI

* パスワードの入力領域の表示を切り替えられる

#### Support for updating datasets

* Autofill が既存のパスワードを更新できるようになる
* 新しいパスワードで置き換えるかの確認が表示される

#### Field Classification improvements

[疑問] Field Classification API って何用？

##### UserData.Builder constructor

* Builder パターンぽくなるようにコンストラクタが変わった
  * [疑問] API リファレンス的には何も変わっていない

##### Allow a Value to be mapped to multiple types of Category IDs

* `UserData.Builder` 利用時に 複数の Category IDs に値をマッピングできるようになった
  * Q 未満だと例外が throw される

##### Improved support for credit card numbers

* 4桁の数字をクレジットカードの最後の入力領域として扱うようになった

##### Support for app-specific field classification

* `FillResponse.setUserData()`
  * アプリ独自のユーザデータを許可するようになった
  * アプリのコンテンツの領域を Autofill 対象として扱うようになる

### UI and system controls

#### Support JVMTI PopFrame caps

* JVMTI = Java Virtual Machine Tools Interface
* カバーしていない領域なので省略

#### Surface control API

* `SurfaceControl`
* カバーしていない領域なので省略

#### WebView huge renderer detection

* `WebViewRendererClient`
* カバーしていない領域なので省略

#### Settings panels

* アプリ利用中に設定パネルを表示させることができる
  * Internet Connectivity
  * NFC
  * Volume
* Android Q 以上では Settigs Panel を、Q 未満では端末の設定アプリに遷移するように実装予定
* [バグ？] Seettings Panel を終了すると、`onActivityResult` の `resultCode` は必ず 0(RESULT_CANCELLED) になっている
  * 一度設定を変更し、バックキーをタップしても設定は反映されているので、キャンセルという概念はなさそう

#### Sharing improvements

* [TODO] 詳細を https://developer.android.com/preview/features/sharing で確認
* [TODO] サンプルを https://github.com/googlesamples/android-SharingShortcuts で確認

#### Roles

* Roles という標準機能が導入される
  * OS はアプリに与えられた役割に従ったシステム機能やユーザデータ領域へのアクセスが許可される
  * `RoleManager` を使用してアプリに Roles を保持するように要求できる(beta1 の時点では)
* [TODO] 詳細を https://developer.android.com/preview/features/roles で確認
  -> beta2 で消えた？

### Kotlin

#### Nullability annotations for libcore APIs

* libcore の API に `@Nullable` がつくようになった
  * `@RecentlyNullable`, `@RecentlyNonNull` が使われるようになる
    * `@Nullable`, `@NonNull` だとエラーになるが、上記だと警告になる
    * Android 9 で追加されたアノテーションも変更される

### NDK

#### Mallinfo-based garbage collection triggering

* カバーしていない領域なので省略

#### Improved debugging of file descriptor ownership

* カバーしていない領域なので省略
