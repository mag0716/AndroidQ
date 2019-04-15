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




## TODO：サンプルアプリ

* Shared collections に画像を保存するアプリ
  * 別アプリが作成した画像の読み込みを確認するために flavor で applicationId、保存する画像を分ける
