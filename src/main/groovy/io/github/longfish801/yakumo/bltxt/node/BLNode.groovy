/*
 * BLNode.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo.bltxt.node;

import groovy.util.logging.Slf4j;
import groovy.xml.MarkupBuilder;
import io.github.longfish801.shared.lang.ArgmentChecker;

/**
 * BLtxt文書のノードを表す抽象クラスです。
 * @version 1.0.00 2017/08/02
 * @author io.github.longfish801
 */
@Slf4j('LOG')
abstract class BLNode {
	/** タグ */
	String tag = null;
	/** 属性リスト */
	BLAttrs<String> blAttrs = new BLAttrs<String>();
	/** 上位ノード */
	BLNode parent = null;
	/** 下位ノードリスト */
	List<BLNode> blNodes = [];
	/** 行番号 */
	int lineNo = 0;
	/** 通番 */
	int serialNo = 0;
	/** 下位要素として可能なクラスのリスト */
	List<Class> lowerClass = [];
	
	/**
	 * 属性リストを返します。
	 * @return 属性リスト
	 */
	BLAttrs<String> getAttrs(){
		return blAttrs;
	}
	
	/**
	 * 下位ノードリストを返します。
	 * @return 下位ノードリスト
	 */
	List<BLNode> getNodes(){
		return blNodes;
	}
	
	/**
	 * 下位ノードを追加します。
	 * @param subNode 下位ノード
	 * @return 本インスタンス
	 */
	BLNode leftShift(BLNode subNode){
		ArgmentChecker.checkNotNull('下位ノード', subNode);
		ArgmentChecker.checkClasses('下位ノード', subNode, this.validLowerClasses);
		subNode.parent = this;
		nodes << subNode;
		return this;
	}
	
	/**
	 * 下位ノードとして可能なクラスの候補を返します。
	 * @return 下位ノードとして可能なクラスの候補
	 */
	static List<Class> getValidLowerClasses() {
		return [];
	}
	
	/**
	 * XML形式で表現した文字列を返します。
	 * @return XML形式で表現した文字列
	 */
	String toXml(){
		StringWriter writer = new StringWriter();
		MarkupBuilder builder = new MarkupBuilder(writer);
		builder.doubleQuotes = true;
		this.writeXml(builder);
		return writer.toString();
	}
	
	/**
	 * このインスタンスをXML形式で表現した文字列をMarkupBuilderで生成します。
	 * @param builder MarkupBuilder
	 */
	void writeXml(MarkupBuilder builder){
		Map attributes = [:];
		if (tag != null && !tag.empty) attributes['tag'] = tag;
		if (lineNo > 0) attributes['lnum'] = lineNo;
		if (serialNo > 0) attributes['snum'] = serialNo;
		builder."${xmlTag}"(attributes){
			if (attrs != null) attrs.writeXml(builder);
			if (nodes != null && nodes.size() > 0){
				nodes.each { BLNode node -> node.writeXml(builder) }
			}
		}
	}
	
	/**
	 * XMLとしてのタグ名を返します。
	 * @return XMLとしてのタグ名
	 */
	static String getXmlTag(){
		return '';
	}
}
