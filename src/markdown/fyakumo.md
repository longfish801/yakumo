# yakumo記法

[TOC levels=2-6]

## 概要

　サンプルとして、独自の糖衣構文（yakumo記法）を bltxt文書に変換する変換資材 thtmlが用意されています。
　変換処理は、変換資材 fyakumo内の fyakumo.tpacに記述しています。

## 糖衣構文

### 見出し

　行頭に「■」「□」「▼」があれば、見出しとみなします。

```
■おいしいカレーの作り方
□材料の準備
▼牛の飼い方
```

　上記を以下のbltxt文書に変換します。

```
【＝見出し】おいしいカレーの作り方
【＝見出し：2】材料の準備
【＝見出し：3】牛の飼い方
```

### 箇条書き

　一文字目に中黒（・）があれば、箇条書きとみなします。
　半角空白でインデントすることで、複数行を記述できます。
　項目の間に空行があっても無視します。
　箇条書きを終了するには、空行の後に中黒始まりではない行を記述してください。

```
・じゃがいも
・ニンジンを、
  二本。

・玉ねぎ
```

　上記を以下のbltxt文書に変換します。

```
【－箇条書き】
【－項目】
じゃがいも
【項目－】
【－項目】
ニンジンを、
二本。
【項目－】
【－項目】
玉ねぎ
【項目－】
【箇条書き－】
```

　入れ子にすることもできます。
　インデントとして半角スペース２個が必要です。

```
・じゃがいも
・ニンジン
・玉ねぎ
  ・刻むときは注意。
    涙がでます。
・カレー粉
  ・ジャワカレー
  ・バーモンドカレー
```

　上記を以下のbltxt文書に変換します。

```
【－箇条書き】
【－項目】
じゃがいも
【項目－】
【－項目】
ニンジン
【項目－】
【－項目】
玉ねぎ
【－箇条書き】
【－項目】
刻むときは注意。
涙がでます。
【項目－】
【箇条書き－】
【項目－】
【－項目】
カレー粉
【－箇条書き】
【－項目】
ジャワカレー
【項目－】
【－項目】
バーモンドカレー
【項目－】
【箇条書き－】
【項目－】
【箇条書き－】
```

### 引用、引用元

　半角イコール５つを区切り行として、引用を表現できます。
　必要であれば引用元を行タグで記述します。

```
=====
　山路を登りながら、こう考えた。
　智に働けば角が立つ。情に棹させば流される。意地を通せば窮屈だ。とかくに人の世は住みにくい。
【＝引用元】夏目漱石『草枕』
=====
```

　上記を以下のbltxt文書に変換します。

```
【－引用】
　山路を登りながら、こう考えた。
　智に働けば角が立つ。情に棹させば流される。意地を通せば窮屈だ。とかくに人の世は住みにくい。
【＝引用元】夏目漱石『草枕』
【引用－】
```

### コード

　半角イコール５つを区切り行として、コードを表現できます。

```
-----
println "Hello, World!";
println "This is sample code of how to hello to all of the world, which is executed " + Date().format("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
-----
```

　上記を以下のbltxt文書に変換します。

```
【－コード】
println "Hello, World!";
println "This is sample code of how to hello to all of the world, which is executed " + Date().format("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
【コード－】
```

### マスキング

　yakumo記法の糖衣構文で、switemスクリプトによる整形の対象外としたい文章を表現できます。

```
【－マスキング】
・帰りに綿棒を買ってくること。
【マスキング－】
```

　上記を以下のbltxt文書に変換します。
　本来なら箇条書きとして整形されるはずですが、マスキングしているため処理の対象外となっています。
　行範囲タグは削除されることにご注意ください。

```
・帰りに綿棒を買ってくること。
```

### 文範囲終了タグの省略

　以下は本来ならば文範囲タグを用いて記述すべきですが、文範囲終了タグを省略して文中タグのように記述することができます。

* リンク
* 重要
* 補足
* 訂正
* 縦中横
* 傍点
* ルビ

　リンクの場合は以下のとおりです。
　一番目の属性にリンクとして表示する文字列を、二番目の属性にリンク先アドレスを指定してください。
　一番目の属性のみ指定した場合は、表示する文字列とリンク先アドレスを同じ値とみなします。

```
　詳しくは【リンク：ネット検索：https://www.google.com/】してください。
　【リンク：https://www.google.com/】でネット検索できます。
```

　上記を以下のbltxt文書に変換します。

```
　詳しくは【｜リンク：https://www.google.com/】ネット検索【リンク｜】してください。
　【｜リンク：https://www.google.com/】https://www.google.com/【リンク｜】でネット検索できます。
```

　特例として、URLのみが記述された行もリンクとみなして文範囲タグに変換します。

```
https://www.google.com/
```

　上記を以下のbltxt文書に変換します。

```
【｜リンク：https://www.google.com/】https://www.google.com/【リンク｜】
```

　重要の場合は以下のとおりです。
　補足、訂正、縦中横、傍点も同様です。

```
　間違えて【重要：自爆スイッチ】を押さないでください。
```

　上記を以下のbltxt文書に変換します。

```
　間違えて【｜重要】自爆スイッチ【重要｜】を押さないでください。
```

　ルビの場合は以下のとおりです。
　一番目の属性にルビをふる対象を、二番目の属性にルビを指定します。

```
　【ルビ：出納：すいとう】係は言った。
```

　上記を以下のbltxt文書に変換します。

```
　【｜ルビ：すいとう】出納【ルビ｜】係は言った。
```