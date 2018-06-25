/*
 * Clmap.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo.washscr;

import groovy.transform.InheritConstructors;
import groovy.util.logging.Slf4j;
import io.github.longfish801.yakumo.clmap.Clinfo;
import io.github.longfish801.yakumo.clmap.Clmap;
import io.github.longfish801.yakumo.tpac.TpacMaker;
import io.github.longfish801.yakumo.washscr.conveyer.Conveyer;
import io.github.longfish801.yakumo.washscr.conveyer.ConveyerSystem;

/**
 * WashScr記法に沿って文字列を変換します。
 * @version 1.0.00 2017/07/27
 * @author io.github.longfish801
 */
@Slf4j('LOG')
@InheritConstructors
class WashScr extends Clmap {
	static {
		// クロージャ情報のGroovyShellを上書きしておきます
		Clinfo.shell = new GroovyShell(WashScr.classLoader);
	}
	
	/**
	 * TPAC文書を解析するためのインスタンスとしてClmapMakerを返します。
	 * @return TPAC文書を解析するためのインスタンス
	 */
	TpacMaker getMaker(){
		return new WashScrMaker();
	}
	
	/**
	 * WashScr記法に沿ってファイル内の文字列を変換します。<br>
	 * 文字コードは環境変数file.encodingで指定された値を使用します。
	 * @param file 処理対象ファイル
	 * @return 変換結果
	 */
	String wash(File file){
		return wash(file, System.getProperty('file.encoding'));
	}
	
	/**
	 * WashScr記法に沿ってファイル内の文字列を変換します。
	 * @param file 処理対象ファイル
	 * @param enc WashScrを記述したファイルの文字コード
	 * @return 変換結果
	 */
	String wash(File file, String enc){
		return wash(new BufferedReader(new InputStreamReader(new FileInputStream(file), enc)));
	}
	
	/**
	 * WashScr記法に沿って文字列を変換します。
	 * @param text 処理対象文字列
	 * @return 変換結果
	 */
	String wash(String text){
		return wash(new BufferedReader(new StringReader(text)));
	}
	
	/**
	 * WashScr記法に沿って BufferedReaderから読みこんだ文字列を変換します。
	 * @param reader 処理対象のBufferedReader
	 * @return 変換結果
	 */
	String wash(BufferedReader reader){
		StringWriter writer = new StringWriter();
		wash(reader, new BufferedWriter(writer));
		return writer.toString();
	}
	
	/**
	 * WashScr記法に沿って BufferedReaderから読みこんだ文字列を変換し、BufferdWriterに出力します。<br>
	 * 例外発生時は WARNログを出力し、呼び元にスローします。
	 * @param reader 処理対象のBufferedReader
	 * @param outWriter 処理結果のBufferdWriter
	 */
	void wash(BufferedReader reader, BufferedWriter writer){
		LOG.debug('washscr実行開始 key={}', dec.key);
		try {
			ConveyerSystem.conveyBuffer(new ParentConveyer(this as Clmap).getConveyers(dec.lowers.values() as List), reader, writer);
		} catch (exc){
			LOG.error('文字列変換中に問題が発生しました。key={}, exc={}', dec.key, exc.toString());
			throw exc;
		}
		LOG.debug("washscr実行終了 key={}", dec.key);
	}
}
