/*
 * BLInline.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo.bltxt.node;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.lang.ArgmentChecker;

/**
 * BLtxt文書のインライン要素です。
 * @version 1.0.00 2017/08/02
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class BLInline extends BLNode {
	/** XMLとしてのタグ名 */
	static String xmlTag = 'inline';
	/** 下位要素として可能なクラスの候補 */
	static validLowerClasses = [ BLInline.class, BLText.class ];
	
	/**
	 * コンストラクタ。
	 * @param tag タグ
	 * @param lineNo 行番号
	 */
	BLInline(String tag, int lineNo){
		ArgmentChecker.checkMatchRex('タグ', tag, /[^￥：【】＝－｜＃＊]+/);
		this.tag = tag;
		this.lineNo = lineNo;
	}
	
	/** {@inheritDoc} */
	@Override
	String toString(){
		String result = null;
		if (nodes.size() == 0){
			result = "【${tag}${attrs.toString()}】";
		} else {
			String cont = nodes.collect { it.toString() }.join();
			result = "【｜${tag}${attrs.toString()}】${cont}【${tag}｜】";
		}
		return result;
	}
}
