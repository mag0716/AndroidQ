# Scoped storage

https://developer.android.com/preview/privacy/scoped-storage

* アプリがデバイスの外部ストレージ上のファイルにアクセスする方法が変更される
* `READ_EXTERNAL_STORAGE`, `WRITE_EXTERNAL_STORAGE` が置き換えられる

## Isolated storage sandbox for app-private files

* 他のアプリからのアクセスが制限されるサンドボックスがアプリごとに生成される
  * 必要なパーミッションを少なくできる
    * サンドボックスのファイルへのアクセスにはパーミッションが不要
  * セキュリティがより強固になる
    * 他のアプリは別アプリのサンドボックスのファイルにはアクセスできない
* Note：アプリがアンインストールされたらサンドボックスにあるファイルは削除される
* ファイルを保存するための外部ストレージのパスは `Context.getExternalFilesDir()` で返される
  * 全ての Android バージョンで同じ

## Shared collections for media files

* ユーザに関係するファイルを作成する場合やアプリがアンインストールされてもファイルが残ることを期待している場合は、共通のメディア領域に保存する
  * Shared collections は Photos & Videos, Music, Download が含まれる

### Permissions for viewing other apps' files

* Shared collections に自身のファイルを作成、修正のためにパーミッションは不要
* 他のアプリのファイルが作成したファイルに対して修正する場合はパーミッションが不要になる
  * Photos & Videos：`READ_MEDIA_IMAGES`, `READ_MEDIA_VIDEO`
  * Music：`READ_MEDIA_AUDIO`
* Note：Downloads のためのパーミッションはない
  * 他のアプリが生成したファイルにアクセスするためにはシステムのアプリを利用する必要がある
* Note：Storage Access Framework を利用していたら、これらのパーミッションは不要

### Access shared collections

* Shared collections のファイルへのアクセスには `MediaStore` API を利用する
  * Phots & Videos：`MediaStore.Images`, `MediaStore.Video`
  * Music：`MediaStore.Audio`
  * Downlaods：`MediaStore.Downloads`
* Caution：Android Q で `getExternalStoragePublicDirectory()` を呼び出すと自身のアプリ用のサンドボックスのパスが返される
  * 他のアプリのファイルへのアクセスするためには、以下のどちらかの対応が必要
    * `READ_MEDIA_*` パーミッションを取得し、`MediaStore` API を使う
    * パーミッションが不要な Storage Access Framework を使う

### Preserve your app's files in shared collections

* デフォルトではアプリがアンインストールされるとサンドボックスのファイルは削除される
* ファイルを残すには Storage Access Framework を使うか shared collection に保存する必要がある
  * [疑問点] 画像とか音声とかはいいがテキストファイルなどの保存には Storage Access Framework を使うしかないの？
* Shared collection にファイルを保持するために、`MediaStore` に適切な row を追加する必要がある
  * 最低限 `DISPLAY_NAME`, `MIME_TYPE` は必要
  * ディスク上の配置先を `PRIMARY_DIRECTORY`, `SECONDARY_DIRECTORY` で変えられる
  * `DATA` はなくなった
* 上記の row を追加後に `ContentResolver.openFileDescriptor` などの API をファイルへのアクセスに利用することができる
* アプリが再インストールすると、前に作成したファイルは他のアプリで作成されたファイルとして扱われる
  * [TODO] サンプルアプリで動作確認

## Special considerations for photographs

### Access location information in pictures

* Exif 情報に含まれている位置情報は Android Q ではデフォルトで修正される？
  * [疑問点] 何に訂正されるの？
  * この制限は [camera characteristics](https://developer.android.com/preview/privacy/camera-connectivity#camera-info-permission) とは別物
* [重要] Note：アプリがデフォルトの Photo Manager app になっていたら、写真の位置情報にアクセスすることができる
* 写真の位置情報が必要であれば、以下の手順が必要
  * `ACCESS_MEDIA_LOCATION` を AndroidManifest に追加
  * `setRequireOriginal()` の利用

### Show users a gallery of photos

* カメラアプリの場合は、デフォルトの Photo Manager app でない限り、Photos & Videos Shared collections にアクセスされた写真への直接アクセスはできない
  * `ACTION_REVIEW` でギャラリーアプリを使う必要がある

## Work with other apps' files

### Access files created by other apps

* 他のアプリが外部ストレージに保存したファイルにアクセスするためには以下が必要
  * パーミッションの取得
  * `ContentResolver` の利用
* Note：
  `loadThumbnail()` はプレビュー画像を取得するための新 API
  ファイル全体を取得する前に `loadThumbnail()` を使うのが良い

### Write to files created by other apps

* [重要] 通常はアプリ所有のファイルしか書き込みできないがデフォルトアプリに指定されていたら書き込みが可能になる
  * デフォルトの Photo Manager app だったら、Photos & Videos Shared collection のファイルは修正可能
  * デフォルトの Music app だったら、Music Shared collection のファイルは修正可能
* Note：デフォルトアプリでないこともちゃんと考慮しておく必要がある
* ファイルの修正には `ContentResolver` を利用する
  * 修正操作中は `RecoverableSecurityException` を catch してユーザに書き込み権限の許可をリクエストすることができる

## Access media files from native code

* 特殊なメディアファイルを扱う必要がある場合、ファイル記述子を指定する必要がある
  * `var fileOpenMode = "r"`
* [疑問点] native code って何？
* [TODO] https://www.youtube.com/watch?v=oIn0MZQJpp0&t=15m20s みる

## Access specific files

* 以下のようなケースではパーミッションは不要
  * photo-editing app で描画を開く
    * [疑問点] イメージつかない
  * ユーザが選択した位置にテキストファイルを保存する
* これらのケースでは Storage Access Framework を使う

## Companion app file sharing

* 他のアプリと相互にファイルアクセスが必須な場合は、`content://` を利用する
  * [詳細](https://developer.android.com/training/secure-file-sharing/setup-sharing)

## Compatibility mode for previously installed apps on upgrading devices

* 外部ストレージのファイルアクセスの制限は targetSdkVersion が Android Q 以上のアプリかAndroid Q の端末に新規インストールした場合に適用される
  * [重要] Android Q でも動きが変わる
* ファイルアクセスの互換モードは以下の状態に適用される
  * アプリの targetSdkVersion が Android 9 以下
  * アプリがインストールされた状態で、端末の OS が Android Q に更新された
* 互換モードは以下のような動作になる
  * 他のアプリが作成した MediaStore collections にあるファイルにアクセスできる
  * Storage パーミッションが Photos & Videos, Music などの個別のパーミッションよりも優先される
* 互換モードはアプリがアンインストールされるまで維持される
* Note：互換モードは同じ端末にアプリを再インストールしても有効にならない

## Identify a specific external storage device

* Android 9 以下では全てのストレージ上のファイルは `external` 以下に保存される
* Android Q では端末の外部ストレージごとにユニークな名前が提供される
* Note：primary な外部ストレージは常に `external` が使われる
* ユニークな名前が使われるので、volume名と ID を一緒に使う必要がある
  * `MediaStore.Images.getContentUri()` などで volume名を渡してアクセスすることができる

### Get the list of external storage devices

* 全ての有効な volume のリストを取得するには、`MediaStore.getAllVolumeNames()` を利用する

### Set up virtual external storage device

* 取り外し可能な外部ストレージがない場合は、テスト目的で以下のコマンドで有効にできる
  * `adb shell sm set-virtual-disk true`

## Test the behavior change

### Toggle the behavior change

* Android Q でデフォルトで有効になっている動作を以下のコマンドで無効化することができる
  * `adb shell sm set-isolated-storage off`
* 上記のコマンド後に端末が再起動する
  * 再起動されなかったら数分後にリトライする
* 設定値の確認は以下のコマンドで
  * `adb shell getprop sys.isolated_storage_snapshot`

### Test compatibility mode behavior

* 互換モードをテスト用に有効化することができる
  * `adb shell cmd appops set your-package-name android:legacy_storage allow && \
adb shell am force-stop your-package-name`
* 互換モードを無効化するにはアプリのアンインストールか以下のコマンドで
  * `adb shell cmd appops set your-package-name android:legacy_storage default && \
adb shell am force-stop your-package-name`

## Browse external storage as file manager

* [重要] 外部ストレージを確認するには `ACTION_OPEN_DOCUMENT_TREE` を使う
  * [サンプルアプリ](https://github.com/googlesamples/android-DirectorySelection)
* Caution：
  `StorageVolume#createAccessIntent()` は deprecated になった
  使わないようにすれば、Android Q のユーザが外部ストレージのファイルを見れないようにすることができる


## TODO：サンプルアプリ

* Shared collections に画像を保存するアプリ
  * 別アプリが作成した画像の読み込みを確認するために flavor で applicationId、保存する画像を分ける
