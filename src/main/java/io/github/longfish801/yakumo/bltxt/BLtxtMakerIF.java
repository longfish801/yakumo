/*
 * BLtxtMakerIF.java
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo.bltxt;

/**
 * BLtxt文書の変換に用いるインタフェースです。
 * @version 1.0.00 2017/08/02
 * @author io.github.longfish801
 */
public interface BLtxtMakerIF {
	/**
	 * 解析を開始します。
	 */
	void createRoot();
	
	/**
	 * 段落の作成を開始します。
	 * @param lineNo 行番号
	 */
	void createParaBgn(int lineNo);
	
	/**
	 * 段落の作成を終了します。
	 * @param lineNo 行番号
	 */
	void createParaEnd(int lineNo);
	
	/**
	 * 行タグの作成を開始します。
	 * @param tagName タグ名
	 * @param lineNo 行番号
	 */
	void createBlock(String tagName, int lineNo);
	
	/**
	 * 行タグの作成を終了します。
	 * @param lineNo 行番号
	 */
	void createBlockTerminate(int lineNo);
	
	/**
	 * 行範囲タグの作成を開始します。
	 * @param tagName タグ名
	 * @param lineNo 行番号
	 */
	void createBlockBgn(String tagName, int lineNo);
	
	/**
	 * 行範囲タグの作成を終了します。
	 * @param tagName タグ名
	 * @param lineNo 行番号
	 */
	void createBlockEnd(String tagName, int lineNo);
	
	/**
	 * 含意タグの作成を開始します。
	 * @param tagName タグ名
	 * @param lineNo 行番号
	 */
	void createMeta(String tagName, int lineNo);
	
	/**
	 * 含意タグの作成を終了します。
	 * @param lineNo 行番号
	 */
	void createMetaTerminate(int lineNo);
	
	/**
	 * 含意範囲タグの作成を開始します。
	 * @param tagName タグ名
	 * @param lineNo 行番号
	 */
	void createMetaBgn(String tagName, int lineNo);
	
	/**
	 * 含意範囲タグの作成を終了します。
	 * @param tagName タグ名
	 * @param lineNo 行番号
	 */
	void createMetaEnd(String tagName, int lineNo);
	
	/**
	 * 行の解析を開始します。
	 * @param lineNo 行番号
	 */
	void createLineBgn(int lineNo);
	
	/**
	 * 行の解析を終了します。
	 * @param lineNo 行番号
	 */
	void createLineEnd(int lineNo);
	
	/**
	 * 文中タグを作成します。
	 * @param tagName タグ名
	 * @param lineNo 行番号
	 */
	void createInline(String tagName, int lineNo);
	
	/**
	 * 文範囲タグの作成を開始します。
	 * @param tagName タグ名
	 * @param lineNo 行番号
	 */
	void createInlineBgn(String tagName, int lineNo);
	
	/**
	 * 文範囲タグの作成を終了します。
	 * @param tagName タグ名
	 * @param lineNo 行番号
	 */
	void createInlineEnd(String tagName, int lineNo);
	
	/**
	 * 属性を作成します。
	 * @param value 属性値
	 * @param lineNo 行番号
	 */
	void createAttr(String value, int lineNo);
	
	/**
	 * 平文を作成します。
	 * @param text テキスト
	 * @param lineNo 行番号
	 */
	void createText(String text, int lineNo);
	
	/**
	 * BLtxt文書の変換結果を返します。
	 * @return BLtxt文書の変換結果
	 */
	Object getResult();
	
	/**
	 * 現在の解析内容を WARNログに出力します。
	 */
	void loggingCurrentStatus();
}
