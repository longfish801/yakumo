/*
 * BLLine.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo.bltxt.node;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.lang.ArgmentChecker;

/**
 * BLtxt文書のライン要素です。
 * @version 1.0.00 2017/08/02
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class BLLine extends BLNode {
	/** XMLとしてのタグ名 */
	static String xmlTag = 'line';
	/** 下位要素として可能なクラスの候補 */
	static validLowerClasses = [ BLInline.class, BLText.class ];
	
	/**
	 * コンストラクタ。
	 * @param lineNo 行番号
	 */
	BLLine(int lineNo){
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
	
	/** {@inheritDoc} */
	@Override
	String toString(){
		return nodes.collect { it.toString() }.join();
	}
}
