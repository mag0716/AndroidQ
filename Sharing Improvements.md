## Sharing Improvements

https://developer.android.com/preview/features/sharing

## Sharing Shortcuts API

* ShareSheet が変わる
* Direct Share API は新 API に置き換わる
  * `ShortcutInfo` API の拡張
  * Share 対象はアプリが更新するか、アンインストールするまで永続化される
  * `ChooserTargetService` は不要になり、`ShortcutManagerCompat` 経由で追加することが可能

~~[疑問点]~~ Direct　Share API は deprecated になるの？
-> そのまま使える模様が新 API が優先される

~~[TODO]~~ [サンプル](https://github.com/googlesamples/android-SharingShortcuts) アプリを動作させてみる
  * 新 API 対応前のサンプルは [こっち](https://github.com/googlesamples/android-DirectShare)
~~[疑問点]~~ どういうアプリで使う？
-> Twitter とか LINE みたいにユーザ同士の交流があるアプリ
~~[TODO]~~ Direct Share を復習
  * Android 6.0 から導入
  * 共有先の選択でアプリではなくユーザ(連絡先にある)を選択して、共有を簡単にする機能
    * LINE とか Twitter などのようにアプリ内で管理しているユーザに共有することができる

## Publish direct share targets

* リソースファルを定義する
* 定義された `share-targets` に合致するカテゴリを使って Dynamic Shortcuts を作成する
  * `ShortcutManager`, `ShortcutManagerCompat` 経由

## The DirectShare API

* `ShortcutInfo.Builder`
  * `setCategories()`
    * share targets とする場合は必須
  * `setLongLived()`
    * true を設定しておくと、アプリで削除されても、システムによってキャッシュされる
    * [疑問点]　キャッシュすることでのメリット
  * `setPerson()`, `setPersons()`
    * `Person` オブジェクトをショートカットに関連づける
    * 追加することはオプションだが強く推奨されている
    * 一般的なメッセージングアプリでは `Person` に連絡先の情報を含む必要がある

## Declare a share target

* `<shortcuts>` 以下に `<share-target>` を定義する
  * 対象クラス、シェアデータ種別、カテゴリを定義する
  * カテゴリは複数定義できる

## DirectShare in AndroidX

* `ShortcutManagerCompat` は古い DirectShare API の互換性がある
  * DirectShare API で必要だった AndroidManifest への定義は不要になる
  * ただし、`ChooserTargetServiceCompat` の定義は必要

## Content preview

* Sharesheet にシェア内容のプレビューを表示することができる
  * `EXTRA_TITLE`：タイトル
  * `setClipData`：画像の URI
    * `FileProvider` 経由で取得
    * `FLAG_GRAN_READ_URI_PERMISSION` が必要

## FAQ

### What are the main differences between the new API and the old DirectShare API?

* 新 API は push model
  * ShareSheet の準備がより早くなった
  * アプリは内部状態が変わるたびにショートカットを更新する必要がある

### What happens if I don't migrate to use the new APIs?

* Android Q 以上では ShortcutManager でショートカットが優先される
  * シェア時に無視される可能性がある

### Can I use both old and new DirectShare APIs in my app for backwards compatibility?

* 使えないので、代わりに `ShortcutManagerCompat` を使う
* 2種類の API を混ぜると、予期しない動作になる

### How are published shortcuts for share targets different from launcher shortcuts (the typical usage of shortcuts when long pressing on app icons in launcher)?

* share targets 目的で追加したショートカットもアプリアイコンの長押しで表示される
  * アプリが公開しているショートカットの数にも適用される
