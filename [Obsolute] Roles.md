# Roles

https://developer.android.com/preview/features/roles

* beta2 で消えた？

* アプリが Role を保持すると、その Role の特権を行えるようになる
* アプリは適切な Roles を宣言することができるが、必須項目を満たしている必要がある
  * Android Q 用の `uses-permission` の定義
  * Roles に必要な `<category>` の定義

[TODO] サンプルアプリ作成 -> googlesamples もなくユースケースなども不明のため後回し
[疑問] デフォルトアプリ設定との関係は？
  -> デフォルトアプリ設定の画面に Role の設定項目が追加されている

## Role defined in Android Q

* ROLE_BROWSER
  * Webページを開く Intent を優先的に扱えるようになる
* ROLE_DIALER
  * 着信と履歴を優先的に扱えるようになる。また、SMS の送信が可能になる
* ROLE_SMS
  * SMS の送受信が可能になる。また、連絡先への読み込みが可能になる
* ROLE_HOME
  * デフォルトランチャーになる
* ROLE_MUSIC
  * 音楽メディアディレクトリへのフルアクセス権限
* ROLE_GALLERY
  * 写真、動画のメディアディレクトリへのフルアクセス権限

[疑問点] どのタイミングで Role を要求するのがよいのか？
[疑問点] Roles が与えられていないアプリは使えるの？ アプリによっては Roles がないとまともに動かないアプリもありそう？
[疑問点] どのアプリにも Roles が与えられていない状況はありえる？
  -> ありえる
[疑問点] フルアクセスというのは他アプリで作成したファイルの参照も含まれる？

## Satisfy role requirements

* アプリが Roles を保持するためには必須項目を満たす必要がある
  * AndroidManifest.xml の変更

[疑問点] 必須項目を満たしていない場合に、Roles を要求するとどうなる？

## Request role assignment to your app

* Roles が定義されているかを決める
* ユーザがアプリに Roles をアサインするかどうかを決める
* アプリが Roles を受け取るかどうかを決める
* `RoleManager`
