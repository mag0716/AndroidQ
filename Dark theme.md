# Dark theme

https://developer.android.com/preview/features/darktheme

Android Q では端末設定によってシステム UI とアプリに Dark theme が適用される
Dark theme は以下のメリットがある

* バッテリー消費を減らせる
* 視力が弱い人や明るい光に敏感な人の視認性をあげる
* 暗い環境での視認性をあげる

Android Q で Dark theme を有効にするには

* 設定アプリで Settings -> Display -> Theme で Dark を選択
* Quick Settings から　Dark Theme を有効化する
* Pixel 端末では Battery Saver モードで有効になる

## Supporting Dark theme in your app

Dark theme をサポートするために `DayNight` を継承する
MDC を使っている場合は `Theme.MaterialComponets.DayNight`

システム設定によってアプリの theme が切り替えられる

### Themes and styles

theme や style では light 用の色やアイコンを直接指定するのは避け、attr を使うか night ディレクトリにリソースを用意すべき

* `?android:attr/textColorPrimary`：主なテキスト色で disabled 時の色も含まれている
* `?attr/colorControlNormal`：主なアイコン色で disabled 時の色も含まれている

MDC の color theming を使うことが推奨されている
カスタム attributes を用意することもできる

### Changing themes in-app

アプリ起動中にユーザに theme を切り替えさせたい場合は以下の項目を用意する

Android 9以下

* Light
* Dark
* Set by Battery Saver
  * デフォルト推奨

Android Q

* Light
* Dark
* System default
  * デフォルト推奨

`AppCompatDelegate.setDefaultNightMode` で モードを切り替える

* Light：MODE_NIGHT_NO
* Dark：MODE_NIGHT_YES
* Set by Battery Saver：MODE_NIGHT_AUTO_BATTERY
* System default：MODE_NIGHT_FOLLOW_SYSTEM

Note：AppCompat 1.1.0-alpha05 から自動で Acitivity の再起動が行われなくなる

## Force Dark

Android Q では `DayNight` の設定なしに簡単に Dark theme をサポートするための機能が提供されている。

Force Dark は Light theme の 各 View を解析し、自動的に Dark theme を適用する。Force Dark とネイティブ実装を同時に利用することも可能。

アプリの theme に `android:forceDarkAllowed="true"` を設定する必要があり、システムが提供する `Theme.Material.Light` のようなLight theme に利用される。
Force Dark を利用するとアプリのテストし、必要に応じて除外する必要がある。

Dark theme を利用されている場合は、Force Dark は適用されない。
`DayNight` を継承している場合も適用されない。

### Disabling Force Dark on a view

View ごとに `android:forceDarkAllowed` か `setForceDarkAllowed` で無効化することができる。

## Best Practices

### Notifications and Widgets

画面に表示されるが直接操作しない Notification や Widget について注意する。

#### Notifications

`MessagingStyle` のようなテンプレートを利用している場合は自動的に適用される

#### Widgets and custom notification views

Light, Dark theme どちらもテストし、以下のような実装をしていないかを確認する

* 背景色が常に light になっている
* テキスト色をハードコーディングしている
* 背景色はハードコーディングしているのにテキスト色はデフォルトを利用している
* 特定の色のアイコンを利用している

これらのケースを attributes を使うことで修正する必要がある。

### Launch screens

起動画面の theme を反映させる必要がある。

色のハードコードを削除して `?android:attr/colorBackground` などを利用する。

Note：dark theme の `android:windowBackground` は Android Q のみで動作する。

### Configuration changes

テーマが変化すると、`uiMode` が `onConfigurationChanged` で渡され、Activity が自動で再生成される。

動画再生中など設定変更をハンドリングしたい場合は、Manifest で `uiMode` を指定しアプリ側でハンドリングする必要がある。

```
val currentNightMode = configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
when (currentNightMode) {
    Configuration.UI_MODE_NIGHT_NO -> {} // Night mode is not active, we're using the light theme
    Configuration.UI_MODE_NIGHT_YES -> {} // Night mode is active, we're using dark theme
}
```
