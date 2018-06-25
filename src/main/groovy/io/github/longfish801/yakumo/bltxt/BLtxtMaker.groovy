/*
 * BLtxtMaker.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo.bltxt;

import groovy.util.logging.Slf4j;
import io.github.longfish801.yakumo.bltxt.node.BLBlock;
import io.github.longfish801.yakumo.bltxt.node.BLInline;
import io.github.longfish801.yakumo.bltxt.node.BLLine;
import io.github.longfish801.yakumo.bltxt.node.BLMeta;
import io.github.longfish801.yakumo.bltxt.node.BLNode;
import io.github.longfish801.yakumo.bltxt.node.BLPara;
import io.github.longfish801.yakumo.bltxt.node.BLRoot;
import io.github.longfish801.yakumo.bltxt.node.BLText;
import io.github.longfish801.yakumo.parser.ParseException;

/**
 * BLtxt記法の文字列の解析にともない、各要素を生成します。<br>
 * 解析は io.github.longfish801.yakumo.parser.BLtxtParserで実施します。<br>
 * BLtxt文書のインスタンスを生成したい場合は{@link BLtxt}を参照してください。
 * @version 1.0.00 2017/08/02
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class BLtxtMaker implements BLtxtMakerIF {
	/** ブロック要素／メタ要素の階層 */
	List<BLNode> blockLevels = [];
	/** インライン要素／ライン要素の階層 */
	List<BLNode> inlineLevels = [];
	/** 行タグ／含意タグの解析行 */
	int singleLineNo;
	/** 解析中の属性リスト */
	List<String> attrs = [];
	
	/** {@inheritDoc} */
	void createRoot(){
		blockLevels.clear();
		blockLevels << new BLRoot();
		inlineLevels.clear();
		singleLineNo = 0;
		attrs.clear();
	}
	
	/** {@inheritDoc} */
	void createParaBgn(int lineNo){
		LOG.trace('*createParaBgn called lineNo={}', lineNo);
		// 段落要素を開きます
		openBlock(new BLPara(lineNo));
	}
	
	/** {@inheritDoc} */
	void createParaEnd(int lineNo){
		LOG.trace('*createParaEnd called lineNo={}', lineNo);
		// 段落要素があれば閉じます
		closePara();
	}
	
	/** {@inheritDoc} */
	void createBlock(String tagName, int lineNo){
		LOG.trace('*createBlock called tagName={} lineNo={}', tagName, lineNo);
		singleLineNo = lineNo;
		// 段落要素を閉じます
		if (!(blockLevels.last() instanceof BLPara)) throw new InternalError("段落要素がありません。行番号=${lineNo}");
		closePara();
		// ブロック要素を開きます
		openBlock(new BLBlock(tagName, lineNo));
	}
	
	/** {@inheritDoc} */
	void createBlockTerminate(int lineNo){
		// ブロック要素を閉じます
		if (!(blockLevels.last() instanceof BLBlock)) throw new InternalError("含意タグが開始していません。タグ名=${blockLevels.last().tag} 行番号=${lineNo}");
		blockLevels.pop();
		// 段落要素を開きます
		openBlock(new BLPara(lineNo));
	}
	
	/** {@inheritDoc} */
	void createBlockBgn(String tagName, int lineNo){
		LOG.trace('*createBlockBgn called tagName={} lineNo={}', tagName, lineNo);
		// 段落要素を閉じます
		if (!(blockLevels.last() instanceof BLPara)) throw new InternalError("段落要素がありません。行番号=${lineNo}");
		closePara();
		// ブロック要素と段落要素を開きます
		openBlock(new BLBlock(tagName, lineNo));
		openBlock(new BLPara(lineNo));
	}
	
	/** {@inheritDoc} */
	void createBlockEnd(String tagName, int lineNo){
		LOG.trace('*createBlockEnd called tagName={} lineNo={}', tagName, lineNo);
		// 段落要素を閉じます
		closePara();
		// ブロック要素を閉じます
		if (!(blockLevels.last() instanceof BLBlock)) throw new ParseException("行範囲タグの終了タグと、開始タグの種類が一致しません。開始タグ名=${blockLevels.last().tag} 終了タグ名=${tagName} 行番号=${blockLevels.last().lineNo},${lineNo}");
		if (blockLevels.last().tag != tagName) throw new ParseException("行範囲タグの終了タグと開始タグの名前が一致しません。開始タグ名=${blockLevels.last().tag} 終了タグ名=${tagName} 行番号=${blockLevels.last().lineNo},${lineNo}");
		blockLevels.pop();
		// 段落要素を開きます
		openBlock(new BLPara(lineNo));
	}
	
	/** {@inheritDoc} */
	void createMeta(String tagName, int lineNo){
		LOG.trace('*createMeta called tagName={} lineNo={}', tagName, lineNo);
		singleLineNo = lineNo;
		// 段落要素を閉じます
		if (!(blockLevels.last() instanceof BLPara)) throw new InternalError("段落要素がありません。行番号=${lineNo}");
		closePara();
		// ブロック要素を開きます
		openBlock(new BLMeta(tagName, lineNo));
	}
	
	/** {@inheritDoc} */
	void createMetaTerminate(int lineNo){
		// メタ要素を閉じます
		if (!(blockLevels.last() instanceof BLMeta)) throw new InternalError("含意タグが開始していません。タグ名=${blockLevels.last().tag} 行番号=${lineNo}");
		blockLevels.pop();
		// 段落要素を開きます
		openBlock(new BLPara(lineNo));
	}
	
	/** {@inheritDoc} */
	void createMetaBgn(String tagName, int lineNo){
		LOG.trace('*createMetaBgn called tagName={} lineNo={}', tagName, lineNo);
		// 段落要素を閉じます
		if (!(blockLevels.last() instanceof BLPara)) throw new InternalError("段落要素がありません。行番号=${lineNo}");
		closePara();
		// メタ要素と段落要素を開きます
		openBlock(new BLMeta(tagName, lineNo));
		openBlock(new BLPara(lineNo));
	}
	
	/** {@inheritDoc} */
	void createMetaEnd(String tagName, int lineNo){
		LOG.trace('*createMetaEnd called tagName={} lineNo={}', tagName, lineNo);
		// 段落要素を閉じます
		closePara();
		// メタ要素を閉じます
		if (!(blockLevels.last() instanceof BLMeta)) throw new ParseException("含意範囲タグの終了タグと、開始タグの種類が一致しません。開始タグ名=${blockLevels.last().tag} 終了タグ名=${tagName} 行番号=${blockLevels.last().lineNo},${lineNo}");
		if (blockLevels.last().tag != tagName) throw new ParseException("含意範囲タグの終了タグと開始タグの名前が一致しません。開始タグ名=${blockLevels.last().tag} 終了タグ名=${tagName} 行番号=${blockLevels.last().lineNo},${lineNo}");
		blockLevels.pop();
		// 段落要素を開きます
		openBlock(new BLPara(lineNo));
	}
	
	/** {@inheritDoc} */
	void createLineBgn(int lineNo){
		LOG.trace('*createLineBgn called lineNo={}', lineNo);
		// 行タグあるいは含意タグならば、段落要素を開きます
		if (singleLineNo == lineNo) openBlock(new BLPara(lineNo));
		// ライン要素を作成します
		inlineLevels.clear();
		BLLine line = new BLLine(lineNo);
		blockLevels.last() << line;
		inlineLevels << line;
	}
	
	/** {@inheritDoc} */
	void createLineEnd(int lineNo){
		LOG.trace('*createLineEnd called lineNo={}', lineNo);
		// 行タグあるいは含意タグならば、段落要素を閉じます
		if (singleLineNo == lineNo) closePara();
		// ライン要素を閉じます
		inlineLevels.pop();
		if (inlineLevels.size() > 0) throw new ParseException("行範囲タグの閉じ忘れがあります。タグ名=${inlineLevels.last().tag} 行番号=${lineNo}");
	}
	
	/** {@inheritDoc} */
	void createInline(String tagName, int lineNo){
		LOG.trace('*createInline called tagName={} lineNo={}', tagName, lineNo);
		openInline(new BLInline(tagName, lineNo));
		inlineLevels.pop();
	}
	
	/** {@inheritDoc} */
	void createInlineBgn(String tagName, int lineNo){
		LOG.trace('*createInlineBgn called tagName={} lineNo={}', tagName, lineNo);
		openInline(new BLInline(tagName, lineNo));
	}
	
	/** {@inheritDoc} */
	void createInlineEnd(String tagName, int lineNo){
		LOG.trace('*createInlineEnd called tagName={} lineNo={}', tagName, lineNo);
		if (inlineLevels.size() <= 1) throw new ParseException("開始タグより先に終了タグが記述されています。タグ名=${tagName} 行番号=${lineNo}");
		if (inlineLevels.last().tag != tagName) throw new ParseException("終了タグと開始タグの名前が一致しません。終了タグ名=${tagName} 開始タグ名=${inlineLevels.last().tag} 行番号=${lineNo}");
		inlineLevels.pop();
	}
	
	/** {@inheritDoc} */
	void createAttr(String value, int lineNo){
		LOG.trace('*createAttr called value={} lineNo={}', value, lineNo);
		attrs << value;
	}
	
	/** {@inheritDoc} */
	void createText(String text, int lineNo){
		LOG.trace('*createText called text={} lineNo={}', text, lineNo);
		inlineLevels.last() << new BLText(text, lineNo);
	}
	
	/** {@inheritDoc} */
	Object getResult(){
		BLRoot root = blockLevels.first();
		root.refreshIndex();
		return root;
	}
	
	/**
	 * 現在の解析内容を XML形式で WARNログに出力します。
	 */
	void loggingCurrentStatus(){
		try {
			LOG.warn('blockLevels={}', blockLevelsStatus);
			LOG.warn('current XML={}', blockLevels.first().toXml());
		} catch (exc){
			LOG.error('現在の解析内容を出力時に問題が生じました。', exc);
		}
	}
	
	/**
	 * ブロック要素の階層の状況を返します。
	 * @return ブロック要素の階層の状況
	 */
	String getBlockLevelsStatus(){
		return blockLevels.collect { "${it.class.simpleName}#${it.tag}" }.join(', ');
	}
	
	/**
	 * インライン要素の階層の状況を返します。
	 * @return インライン要素の階層の状況
	 */
	String getInlineLevelsStatus(){
		return inlineLevels.collect { "${it.class.simpleName}#${it.tag}" }.join(', ');
	}
	
	/**
	 * ブロック要素／メタ要素の解析を始めます。
	 * @param block ブロック要素／メタ要素
	 */
	protected void openBlock(BLBlock block){
		blockLevels.last() << block;
		blockLevels << block;
		if (attrs.size() > 0){
			block.attrs.addAll(attrs);
			attrs.clear();
		}
	}
	
	/**
	 * 最下層が段落要素であれば最下層から除きます。
	 */
	protected void closePara(){
		if (blockLevels.last() instanceof BLPara){
			BLPara para = blockLevels.pop();
			if (para.nodes.size() == 0) para.parent.nodes.pop();
		} else {
			String blockLevelStatus = blockLevels.collect { "${it.class.simpleName}#${it.tag}" }.join(', ');
			LOG.debug('*closePara blockLevels={}', blockLevelStatus);
		}
	}
	
	/**
	 * インライン要素の解析を始めます。
	 * @param inline インライン要素
	 */
	protected void openInline(BLInline inline){
		inlineLevels.last() << inline;
		inlineLevels << inline;
		if (attrs.size() > 0){
			inline.attrs.addAll(attrs);
			attrs.clear();
		}
	}
}
