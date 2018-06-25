/*
 * BLText.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo.bltxt.node;

import groovy.util.logging.Slf4j;
import groovy.xml.MarkupBuilder;
import io.github.longfish801.shared.lang.ArgmentChecker;

/**
 * BLtxt文書の平文です。
 * @version 1.0.00 2017/08/02
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class BLText extends BLNode {
	/** XMLとしてのタグ名 */
	static String xmlTag = 'text';
	/** 平文 */
	String text = null;
	
	/**
	 * コンストラクタ。
	 * @param text 平文
	 * @param lineNo 行番号
	 */
	BLText(String text, int lineNo){
		ArgmentChecker.checkNotEmpty('平文', text);
		this.text = text;
		this.lineNo = lineNo;
	}
	
	/**
	 * 本クラスは属性リストを利用しないため、nullを返します。
	 * @return null
	 */
	@Override
	BLAttrs<String> getAttrs(){
		return null;
	}
	
	/**
	 * 本クラスは下位ノードリストを利用しないため、nullを返します。
	 * @return null
	 */
	@Override
	List<BLNode> getNodes(){
		return null;
	}
	
	/**
	 * 本クラスは下位ノードリストを利用しないため、UnsupportedOperationExceptionをスローします。
	 * @param subNode 下位ノード
	 * @return 本インスタンス
	 * @throws UnsupportedOperationException
	 */
	@Override
	BLNode leftShift(BLNode subNode){
		throw new UnsupportedOperationException();
	}
	
	/** {@inheritDoc} */
	@Override
	String toString(){
		return escape(text);
	}
	
	/**
	 * BLtxt記法上エスケープが必要な文字があればエスケープします。
	 * @param text エスケープ対象文字列
	 * @return エスケープ後の文字列
	 */
	static String escape(String text){
		if (text.indexOf('￥') >= 0) text = text.replaceAll('￥', '￥￥');
		if (text.indexOf('【') >= 0) text = text.replaceAll('【', '￥【');
		if (text.indexOf('】') >= 0) text = text.replaceAll('】', '￥】');
		return text;
	}
	
	/**
	 * このインスタンスをXML形式で表現した文字列をMarkupBuilderで生成します。
	 * @param builder MarkupBuilder
	 */
	void writeXml(MarkupBuilder builder){
		builder."${xmlTag}"(text){ }
	}
}
