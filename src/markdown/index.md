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
	def writer = new StringWriter()
	def yakumo = new Yakumo()
	yakumo.load { material 'fyakumo', 'thtml' }
	yakumo.script {
		targets {
			target 'target', new File('src/test/resources/target.txt'), 'fyakumo'
		}
		results {
			result 'target', writer, 'thtml'
		}
	}
	assert writer.toString().normalize() == new File('src/test/resources/result.html').text
	assert yakumo.script.results.target.fprint.warns.size() == 0
} catch (exc){
	println "Failed to convert: message=${exc.message}"
	throw exc
}
```

　変換対象は [target.txt](https://github.com/longfish801/yakumo/tree/master/src/test/resources/target.txt) です。
　変換結果は [result.html](https://github.com/longfish801/yakumo/tree/master/src/test/resources/result.html) です。

　このサンプルコードは build.gradle内の execSamplesタスクで実行しています。

## ドキュメント

* [Groovydoc](groovydoc/)
* [使い方](howto.html)
* [yakumo記法](notation.html)

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