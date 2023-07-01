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
	new Yakumo().run(new File('src/test/resources/sample/convert.groovy'), null)
} catch (exc){
	println "Failed to convert: message=${exc.message}"
	throw exc
}

File outDir = new File('build/sample')
File expectDir = new File('src/test/resources/sample/expected')
assert new File(outDir, 'index.html').text == new File(expectDir, 'index.html').text
assert new File(outDir, 'curry.html').text == new File(expectDir, 'curry.html').text
assert new File(outDir, 'img/curry.png').exists() == true
```

　上記で実行している変換スクリプトは以下です（src/test/resources/sample/convert.groovy）。

```
File inputDir = new File(scriptFile.parentFile, 'target')
File outputDir = new File('build/sample')
File relateDir = new File(scriptFile.parentFile, 'related')

load {
	material 'fyakumo', 'thtml'
}

script {
	doFirst {
		if (!inputDir.exists()){
			throw new IOException("No input directory. path=${inputDir.absolutePath}")
		}
		if (!outputDir.exists() && !outputDir.mkdirs()){
			throw new IOException("Failed to create output directory. path=${outputDir.absolutePath}")
		}
	}
	
	def scanner = new AntBuilder().fileScanner {
		fileset(dir: inputDir.path) { include(name: '*.txt') }
	}
	List keys = []
	for (File file in scanner){
		keys << file.name.take(file.name.lastIndexOf('.'))
	}
	
	targets {
		keys.each { String key ->
			target key, new File(inputDir, "${key}.txt")
		}
	}
	
	results {
		keys.each { String key ->
			result key, new File(outputDir, "${key}.html")
		}
		templateKey 'index', 'index'
	}
	
	doLast {
		fprint.logs.each { println it }
		assert fprint.warns.size() == 0
	}
}

related {
	outDir outputDir
	def scanner = new AntBuilder().fileScanner {
		fileset(dir: relateDir.path) { include(name: '**/*') }
	}
	for (File file in scanner){
		source 'sample', file.absolutePath.substring(relateDir.absolutePath.length() + 1), file
	}
}
```

　変換対象は [index.txt](https://github.com/longfish801/yakumo/tree/master/src/test/resources/sample/target/index.txt), [curry.txt](https://github.com/longfish801/yakumo/tree/master/src/test/resources/sample/target/curry.txt) です。
　変換結果は [index.html](https://github.com/longfish801/yakumo/tree/master/src/test/resources/sample/expected/index.html), [curry.html](https://github.com/longfish801/yakumo/tree/master/src/test/resources/sample/expected/curry.html) です。

　このサンプルコードは build.gradle内の execSamplesタスクで実行しています。

## ドキュメント

* [Groovydoc](groovydoc/)
* [使い方](howto.html)
* [糖衣構文](fyakumo.html)
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

1.2.04
: タイトルに他の要素を追加しやすいよう改修しました。

1.2.05
: 注目、特記、訂正、上付き、下付きに対応しました。
: 画像にはリンクを付与するよう修正しました。

1.2.06
: clmap関数を HTMLテンプレート内から呼ぶよう修正しました。
: clmapの宣言を見直しました。

1.2.07
: カスタマイズ性と Bootstrapの活用を考慮して HTMLテンプレートを修正しました。
: 併せてサブタイトル、見出し、目次、案内、注意、引用、コードの HTMLを見直しました。

1.2.08
: カクヨム記法、ページ内リンク、見出しの別名、総目次に対応しました。
: ナビゲーションリンクの作成内容を見直しました。
: サンプルについて index.htmlも作成するよう対応しました。

1.2.09
: 総目次のファイル名の順番が変換結果キーの順番となっていなかった不具合を修正しました。

1.2.10
: yakumo記法で見出しに別名を与える場合の区切り記号を全角コロンから全角シャープに修正しました。
: ConvertMaterialクラスのログ出力を強化しました。
: ConvertMaterial#formatでのストリームのクローズ漏れを修正しました。
: fyakumo資材の reprexハンドルでの正規表現を厳密なものに見直しました。

1.2.11
: thtml資材についてコラムタグを案内タグに名称変更しました。
: thtml資材にコラムタグを新規追加しました。
: 行範囲タグの汎用的な糖衣構文として全角イコールによる区切り行に対応しました。
: 用語説明タグの糖衣構文に対応しました。
: 手順タグならびにその糖衣構文に対応しました。
