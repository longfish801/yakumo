# yakumo

[TOC levels=2-6]

## 概要

　テキストの形式を変換します。
　変換元となるテキストは独自の形式で構いません。

　個人が学習のために開発したものです。
　故障対応や問合せ回答などのサポートはしていません。

## 特徴

* 独自の形式のテキストを、いったん階層構造のある文書に変換します。
  そこからHTMLなど目的の形式に変換して出力します。
  変換のためのスクリプトはGroovy DSLを利用します。
* テキストをHTMLに変換するためのサンプル資材が準備されています。

　このライブラリの名称はマークアップに約物を利用することに由来しています。

## サンプルコード

　以下はyakumo記法で記述されたテキストをHTMLへ変換するサンプルスクリプトです（src/test/groovy/Sample.groovy）。

```
import io.github.longfish801.yakumo.Yakumo

try {
	def yakumo = new Yakumo()
	String converted = yakumo.run(new File('src/test/resources/convert.groovy'), null)
	assert converted.normalize() == new File('src/test/resources/result.html').text
	assert yakumo.script.fprint.warns.size() == 0
} catch (exc){
	println "Failed to convert: message=${exc.message}"
	throw exc
}
```

　上記で実行している変換スクリプトは以下です（src/test/resources/convert.groovy）。

```
load {
	material 'fyakumo', 'thtml'
}

def writer = new StringWriter()
script {
	targets {
		target 'target', new File('src/test/resources/target.txt')
	}
	results {
		result 'target', writer
	}
	doLast {
		fprint.logs.each { println it }
	}
}
return writer.toString()
```

　変換対象は [target.txt](https://github.com/longfish801/yakumo/tree/master/src/test/resources/target.txt) です。
　変換結果は [result.html](https://github.com/longfish801/yakumo/tree/master/src/test/resources/result.html) です。

　このサンプルコードは build.gradle内の execSamplesタスクで実行しています。

## ドキュメント

* [Groovydoc](groovydoc/)
* [使い方](howto.html)
* [yakumo記法](fyakumo.html)
* [HTML化](thtml.html)

## GitHubリポジトリ

* [yakumo](https://github.com/longfish801/yakumo)

## Mavenリポジトリ

　本ライブラリの JARファイルを [GitHub上の Mavenリポジトリ](https://github.com/longfish801/maven)で公開しています。
　build.gradleの記述例を以下に示します。

```
repositories {
	mavenCentral()
	maven { url 'https://longfish801.github.io/maven/' }
}

dependencies {
	implementation group: 'io.github.longfish801', name: 'yakumo', version: '0.3.00'
}
```

## 改版履歴

1.1.00
: gradle 7.4の記法に対応しました。
: クロージャによる変換時に補足情報を渡せるよう対応しました。
: outDirに指定されたディレクトリの存在チェックはcopyメソッド実行時に移しました。
: 資材スクリプト、変換スクリプトの委任クラスをyakumoにしました。
: 基底のswitem宣言の名前、clmap宣言の名前を設定するようにしました。

1.1.01
: 足跡は変換結果毎ではなく全体でひとつのみに修正しました。
: クロージャマップはクローンによって変換結果毎に異なるものを用いるようにしました。
: 変換結果キーはclmapスクリプトに引数ではなく大域変数として渡すよう修正しました。

1.1.02
: 事前準備のためのクロージャprepare#dfltの実行に対応しました。

1.1.03
: tpac 1.1.05に対応しました。

1.1.04
: clmap 2.1.00に対応しました。
: thtmlの clmapを複数の資材(tbase, ttext, thtml)に分割しました。

1.2.01
: ブロック要素として用語説明、変換済に対応しました。
: ドキュメントをサンプル資材にあわせて構成を見直しました。

1.2.02
: 資材スクリプト、変換スクリプトに指定できるバインド変数を見直しました。

1.2.03
: ブロック要素に行範囲、インライン要素に範囲を追加しました。
