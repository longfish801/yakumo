/*
 * ConversionHTMLSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo;

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.ExchangeResource;
import io.github.longfish801.shared.PackageDirectory;
import io.github.longfish801.bltxt.BLtxt;
import io.github.longfish801.clmap.Clinfo;
import io.github.longfish801.clmap.Clmap;
import io.github.longfish801.clmap.ClmapServer;
import io.github.longfish801.tpac.TeaServer;
import io.github.longfish801.tpac.TpacServer;
import io.github.longfish801.tpac.element.TeaDec;
import io.github.longfish801.washsh.WashServer;
import io.github.longfish801.washsh.Washsh;
import spock.lang.Shared;
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
	static final File testDir = PackageDirectory.deepDir('src/test/resources', ConversionHTMLSpec.class);
	/** SimpleTemplateEngine */
	private static final SimpleTemplateEngine templateEngine = new SimpleTemplateEngine();
	/** 変換対象のテキスト */
	@Shared TeaDec decTarget;
	/** 変換結果として期待するテキスト */
	@Shared TeaDec decExpect;
	/** Clmap */
	@Shared Clmap clmap;
	/** HTML化を実行するクロージャ */
	@Shared Closure doHTMLize;
	/** bodyタグ内の文字列を取得するクロージャ */
	@Shared Closure doBody;
	/** 期待する結果を取得するクロージャ */
	@Shared Closure doExpect;
	
	def setup(){
		TeaServer teaServer = new TpacServer();
		teaServer.soak(new File(testDir, 'target.tpac'));
		teaServer.soak(new File(testDir, 'expect.tpac'));
		decTarget = teaServer['dec:target'];
		decExpect = teaServer['dec:expect'];
		ClmapServer clmapServer = new ClmapServer();
		clmapServer.soak(ExchangeResource.url(ConversionHTMLSpec.class, '_html/html.tpac'));
		clmap = clmapServer['clmap:_html'];
		doBody = { String parentKey, String childKey ->
			String text = decTarget.lowers["${parentKey}"].lowers["${childKey}"].text.toString();
			return clmap.cl('bltxt#body').call(new BLtxt(text)).join(System.lineSeparator()) + System.lineSeparator() + System.lineSeparator();
		}
		doExpect = { String parentKey, String childKey ->
			return decExpect.lowers["${parentKey}"].lowers["${childKey}"].text.toString();
		}
	}
	
	@Timeout(10)
	@Unroll
	def 'ブロック要素が正しくHTML化されること'(){
		expect:
		doBody(parentKey, childKey) == doExpect(parentKey, childKey);
		
		where:
		parentKey	| childKey
		'block:'	| 'head:'
		'block:'	| 'figure:'
		'block:'	| 'paragraph:'
		'block:'	| 'table:'
	}
	
	@Timeout(10)
	@Unroll
	def 'ブロック要素（コラム）が正しくHTML化されること'(){
		expect:
		doBody(parentKey, childKey) == doExpect(parentKey, childKey);
		
		where:
		parentKey	| childKey
		'block:column'	| 'basic:'
		'block:column'	| 'basic:title'
		'block:column'	| 'attention:'
		'block:column'	| 'blockquote:'
		'block:column'	| 'code:'
		'block:column'	| 'pre:'
		'block:column'	| 'raw:'
	}
	
	@Timeout(10)
	@Unroll
	def 'ブロック要素（リスト）が正しくHTML化されること'(){
		expect:
		doBody(parentKey, childKey) == doExpect(parentKey, childKey);
		
		where:
		parentKey	| childKey
		'block:list'	| 'basic:'
		'block:list'	| 'dl:'
	}
	
	@Timeout(10)
	@Unroll
	def 'インライン要素が正しくHTML化されること'(){
		expect:
		doBody(parentKey, childKey) == doExpect(parentKey, childKey);
		
		where:
		parentKey	| childKey
		'inline:'	| 'link:'
		'inline:'	| 'em:'
		'inline:'	| 'strong:'
		'inline:'	| 'dot:'
		'inline:'	| 'small:'
		'inline:'	| 'strike:'
		'inline:'	| 'verinhori:'
		'inline:'	| 'ruby:'
	}
}
