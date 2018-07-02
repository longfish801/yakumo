/*
 * ConversionHTMLSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo;

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.lang.ExchangeResource;
import io.github.longfish801.shared.lang.PackageDirectory;
import io.github.longfish801.yakumo.bltxt.BLtxt;
import io.github.longfish801.yakumo.clmap.Clinfo;
import io.github.longfish801.yakumo.clmap.Clmap;
import io.github.longfish801.yakumo.tpac.Tpac;
import spock.lang.Specification;
import spock.lang.Timeout;
import spock.lang.Unroll;

/**
 * HTML変換のテスト。
 * @version 1.0.00 2017/09/28
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ConversionHTMLSpec extends Specification {
	/** ファイル入出力のテスト用フォルダ */
	private static final File testDir = PackageDirectory.deepDir(new File('src/test/resources'), ConversionHTMLSpec.class);
	/** SimpleTemplateEngine */
	private static final SimpleTemplateEngine templateEngine = new SimpleTemplateEngine();
	/** 変換対象のテキスト */
	private static final Tpac tpacTarget = new Tpac(new File(testDir, 'target.tpac'));
	/** 変換結果として期待するテキスト */
	private static final Tpac tpacExpect = new Tpac(new File(testDir, 'expect.tpac'));
	/** Clmap */
	private static final Clmap clmap = new Clmap(ExchangeResource.url(ConversionHTMLSpec.class, '_html/clmap/html.tpac'));
	
	@Timeout(10)
	@Unroll
	def 'テンプレートを適用し正しくHTML化されること'(){
		expect:
		getHtmlText(parentKey, childKey) == getTpacText(parentKey, childKey);
		
		where:
		parentKey	| childKey
		'html#'	| 'basic#'
	}
	
	@Timeout(10)
	@Unroll
	def 'ブロック要素が正しくHTML化されること'(){
		expect:
		getBodyText(parentKey, childKey) == getTpacText(parentKey, childKey);
		
		where:
		parentKey	| childKey
		'block#'	| 'head#'
		'block#'	| 'head#sub'
		'block#'	| 'figure#'
		'block#'	| 'head#'
		'block#'	| 'paragraph#'
		'block#'	| 'table#'
	}
	
	@Timeout(10)
	@Unroll
	def 'ブロック要素（コラム）が正しくHTML化されること'(){
		expect:
		getBodyText(parentKey, childKey) == getTpacText(parentKey, childKey);
		
		where:
		parentKey	| childKey
		'block#column'	| 'basic#'
		'block#column'	| 'basic#title'
		'block#column'	| 'attention#'
		'block#column'	| 'blockquote#'
		'block#column'	| 'code#'
		'block#column'	| 'pre#'
		'block#column'	| 'raw#'
	}
	
	@Timeout(10)
	@Unroll
	def 'ブロック要素（リスト）が正しくHTML化されること'(){
		expect:
		getBodyText(parentKey, childKey) == getTpacText(parentKey, childKey);
		
		where:
		parentKey	| childKey
		'block#list'	| 'basic#'
		'block#list'	| 'dl#'
	}
	
	@Timeout(10)
	@Unroll
	def 'インライン要素が正しくHTML化されること'(){
		expect:
		getBodyText(parentKey, childKey) == getTpacText(parentKey, childKey);
		
		where:
		parentKey	| childKey
		'inline#'	| 'link#'
		'inline#'	| 'em#'
		'inline#'	| 'strong#'
		'inline#'	| 'dot#'
		'inline#'	| 'small#'
		'inline#'	| 'strike#'
		'inline#'	| 'verinhori#'
		'inline#'	| 'note#'
		'inline#'	| 'ruby#'
	}
	
	private static String getHtmlText(String parentKey, String childKey){
		Map binding =  clmap.cl('').call(new BLtxt(tpacTarget.dec.lowers["${parentKey}"].lowers["${childKey}"].text));
		Template template = templateEngine.createTemplate(ExchangeResource.url(ConversionHTMLSpec.class, '_html/template/default.html'));
		return template.make(binding).toString() + "\n";
	}
	
	private static String getBodyText(String parentKey, String childKey){
		return clmap.cl('#body').call(new BLtxt(tpacTarget.dec.lowers["${parentKey}"].lowers["${childKey}"].text)).join("\n") + "\n\n";
	}
	
	private static String getTpacText(String parentKey, String childKey){
		return tpacExpect.dec.lowers["${parentKey}"].lowers["${childKey}"].text;
	}
}
