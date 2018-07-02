/*
 * BLtxt.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo.bltxt;

import groovy.transform.InheritConstructors;
import groovy.util.logging.Slf4j;
import groovy.xml.MarkupBuilder;
import io.github.longfish801.yakumo.bltxt.io.LeakedReader;
import io.github.longfish801.yakumo.bltxt.node.BLNode;
import io.github.longfish801.yakumo.bltxt.node.BLRoot;
import io.github.longfish801.yakumo.parser.BLtxtParser;
import io.github.longfish801.yakumo.parser.ParseException;
import io.github.longfish801.yakumo.parser.TokenMgrError;
import io.github.longfish801.shared.lang.ArgmentChecker;
import io.github.longfish801.shared.lang.ExchangeResource;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * BLtxt文書です。
 * @version 1.0.00 2017/08/02
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class BLtxt {
	/** ConfigObject */
	protected static final ConfigObject constants = ExchangeResource.config(BLtxt.class);
	/** ルート要素 */
	BLRoot root = null;
	/** 解析内容の書込み */
	StringWriter leakedWriter = new StringWriter();
	
	/**
	 * ルート要素を指定するコンストラクタです。
	 * @param root ルート要素
	 */
	BLtxt(BLRoot root){
		this.root = root;
	}
	
	/**
	 * ファイル内容をBLtxt文書とみなして解析結果を保持するコンストラクタです。
	 * @param file ファイル
	 * @exception ParseException 対象文字列がBLtxt記法に違反しています。
	 */
	BLtxt(File file){
		root = new FileReader(file).withCloseable { parse(it) };
	}
	
	/**
	 * URL先の内容をBLtxt文書とみなして解析結果を保持するコンストラクタです。
	 * @param url URL
	 * @exception ParseException 対象文字列がBLtxt記法に違反しています。
	 */
	BLtxt(URL url){
		root = new InputStreamReader(url.openStream()).withCloseable { parse(it) };
	}
	
	/**
	 * BLtxt記法の文字列を解析し結果を保持するコンストラクタです。
	 * @param text 文字列
	 * @exception ParseException 対象文字列がBLtxt記法に違反しています。
	 */
	BLtxt(String text){
		root = new StringReader(text).withCloseable { parse(it) };
	}
	
	/**
	 * リーダからBLtxt記法の文字列を読みこみ、解析結果を保持するコンストラクタです。
	 * @param reader リーダ
	 * @exception ParseException 対象文字列がBLtxt記法に違反しています。
	 */
	BLtxt(Reader reader){
		root = parse(reader);
	}
	
	/**
	 * 指定されたリーダからBLtxt記法の文字列を読みこみ、解析結果を返します。
	 * @param reader リーダ
	 * @return 宣言要素
	 * @exception ParseException 対象文字列がBLtxt記法に違反しています。
	 */
	protected BLRoot parse(Reader reader){
		BLRoot root = null;
		try {
			root = new BLtxtParser(new LeakedReader(reader, leakedWriter)).parse(new BLtxtMaker());
		} catch (TokenMgrError exc){
			throw new BLtxtParseException("字句誤りのためBLtxt文書の解析に失敗しました。${createErrorMessageDtail(exc)}", exc);
		} catch (ParseException exc){
			throw new BLtxtParseException("構文誤りのためBLtxt文書の解析に失敗しました。${createErrorMessageDtail(exc)}", exc);
		}
		return root;
	}
	
	/**
	 * エラーが発生した該当行を含むエラーメッセージを作成します。<br/>
	 * 与えられた例外のメッセージに "at line [行番号]"という文字列が含まれていたならば、
	 * その行から最大５行前までの行内容を例外メッセージに連結して返します。<br/>
	 * 含まれていなければ、例外メッセージをそのまま返します。<br/>
	 * どちらの場合も、例外のメッセージは16進数表記の文字を本来の文字に置換します。
	 * @return エラーが発生した該当行を含むエラーメッセージ
	 */
	String createErrorMessageDtail(Exception exc){
		Matcher matcher = (exc.message =~ /at line ([\d]+)/);
		if (matcher.size() == 0) return "詳細=${replaceByteCode(exc.message)}";
		int lineNum = Integer.parseInt(matcher[0][1]);
		List lines = leakedWriter.toString().split(/[\r\n]+/);
		String lastLines = (lines.take(lineNum).takeRight(constants.parser.lineNum) + '-----').join(System.getProperty('line.separator'));
		return "詳細=${replaceByteCode(exc.message)} エラー発生行付近=${lastLines}";
	}
	
	/**
	 * 16進数表記の文字を、本来の文字に置換します。
	 * @param message 16進数表記の文字を含む文字列
	 * @return 16進数表記の文字を本来の文字に置換した文字列
	 */
	static String replaceByteCode(String message){
		Pattern pattern = Pattern.compile("\\\\u([a-f\\d]{4})");
		Matcher matcher = pattern.matcher(message);
		while (matcher.find()){
			char[] chars = Character.toChars(Integer.parseInt(matcher.group(1), 16));
			message = message.replaceFirst(Pattern.quote(matcher.group()), String.valueOf(chars));
		}
		return message;
	}
	
	/**
	 * 文字列表現を返します。
	 * @return 文字列
	 */
	String toString(){
		return root.toString();
	}
	
	/**
	 * XML形式で表現した文字列を返します。
	 * @return XML形式で表現した文字列
	 */
	String toXml(){
		StringWriter writer = new StringWriter();
		MarkupBuilder builder = new MarkupBuilder(writer);
		builder.doubleQuotes = true;
		builder.mkp.xmlDeclaration(constants.xmlDec);
		root.writeXml(builder);
		return writer.toString();
	}
	
	/**
	 * XMLタグ名に対応する、タグ名別のノード一覧を返します。
	 * @param XMLタグ名
	 * @return タグ名別のノード一覧
	 */
	Map<String, List<BLNode>> grepNodes(String xmlTag){
		ArgmentChecker.checkNotNull('XMLタグ名', xmlTag);
		return root.index[xmlTag];
	}
	
	/**
	 * XMLタグ名とタグ名に対応する、ノード一覧を返します。
	 * @param XMLタグ名
	 * @param タグ名
	 * @return ノード一覧
	 */
	List<BLNode> grepNodes(String xmlTag, String tag){
		ArgmentChecker.checkNotNull('タグ名', tag);
		return grepNodes(xmlTag)?.get(tag);
	}
	
	/**
	 * BLtxt文書の解析失敗を表す例外クラスです。
	 */
	@InheritConstructors
	class BLtxtParseException extends Exception { }
}
