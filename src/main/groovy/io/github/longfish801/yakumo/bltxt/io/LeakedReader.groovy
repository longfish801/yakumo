/*
 * Clmap.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo.bltxt.io;

import groovy.util.logging.Slf4j;

/**
 * 読みだした文字列のコピーを出力するReaderです。
 * @version 1.0.00 2018/01/21
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class LeakedReader extends Reader {
	/** Reader */
	Reader reader = null;
	/** Writer */
	Writer writer = null;
	
	/**
	 * コンストラクタ。<br/>
	 * Readerから読込をすると、その内容を Writerへ書きこみます。
	 * @param reader Reader
	 * @param writer Writer
	 */
	LeakedReader(Reader reader, Writer writer){
		this.reader = reader;
		this.writer = writer;
	}
	
	/**
	 * 配列の一部に文字を読み込みます。
	 * @param cbuf 転送先バッファー
	 * @param off 文字の格納開始オフセット
	 * @param len 読み込む文字の最大数
	 * @return 読み込まれた文字数。ストリームの終わりに達した場合は -1
	 */
	@Override
	int read(char[] cbuf, int off, int len){
		int ret = reader.read(cbuf, off, len);
		if (ret > 0){
			int endIdx = (off + ret > cbuf.length)? cbuf.length : off + ret;
			writer.write(new String(Arrays.copyOfRange(cbuf, off, endIdx)));
		}
		return ret;
	}
	
	/**
	 * ストリームを閉じて、それに関連するすべてのシステムリソースを解放します。
	 */
	@Override
	void close(){
		writer.close();
		reader.close();
	}
}
