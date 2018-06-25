/*
 * BLBlock.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo.bltxt.node;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.lang.ArgmentChecker;

/**
 * BLtxt文書のブロック要素です。
 * @version 1.0.00 2017/08/02
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class BLBlock extends BLNode {
	/** XMLとしてのタグ名 */
	static String xmlTag = 'block';
	/** 下位要素として可能なクラスの候補 */
	static validLowerClasses = [ BLBlock.class, BLPara.class, BLMeta.class ];
	
	/**
	 * コンストラクタ。<br>
	 * 継承クラスのコンストラクタのためのものです。<br>
	 * 本コンストラクタで BLBlockインスタンスを生成することは想定していません。
	 */
	BLBlock(){
	}
	
	/**
	 * コンストラクタ。
	 * @param tag タグ
	 * @param lineNo 行番号
	 */
	BLBlock(String tag, int lineNo){
		ArgmentChecker.checkMatchRex('タグ', tag, /[^￥：【】＝－｜＃＊]+/);
		this.tag = tag;
		this.lineNo = lineNo;
	}
	
	/**
	 * 単一か否か返します。
	 * 単一とは、下位ノードがない、あるいは下位ノードがひとつの段落要素でそれがライン要素しか持たない場合を指します。
	 * @return 単一か否か
	 */
	boolean isSingle(){
		return (nodes.size() == 0 || (nodes.size() == 1 && nodes.first() instanceof BLPara && nodes.first().nodes.size() == 1))? true : false;
	}
	
	/**
	 * 下位ノードリストの先頭ノードの、その下位ノードリストの先頭ノードを返します。<br>
	 * 単一のときに利用することを想定しています。<br>
	 * 存在しない場合は nullを返します。
	 * @return 下位ノードリストの先頭ノードの、その下位ノードリストの先頭ノード
	 * @see #isSingle()
	 */
	BLNode getSingleNode(){
		return nodes.first()?.nodes.first();
	}
	
	/** {@inheritDoc} */
	@Override
	String toString(){
		String result = null;
		String cont = nodes.collect { it.toString() }.join("\n");
		if (isSingle()){
			result = "【＝${tag}${attrs.toString()}】${cont}";
		} else {
			result = "【－${tag}${attrs.toString()}】\n${cont}\n【${tag}－】";
		}
		return result;
	}
}
