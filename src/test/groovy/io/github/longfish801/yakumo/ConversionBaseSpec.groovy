/*
 * ConversionBaseSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.lang.ExistResource;
import io.github.longfish801.shared.util.ClassDirectory;
import io.github.longfish801.yakumo.tpac.Tpac;
import io.github.longfish801.yakumo.washscr.WashScr;
import spock.lang.Specification;
import spock.lang.Timeout;
import spock.lang.Unroll;

/**
 * BLtxt変換のテスト。
 * @version 1.0.00 2017/09/28
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ConversionBaseSpec extends Specification {
	/** ExistResource */
	private static final ExistResource existResource = new ExistResource(ConversionBaseSpec.class);
	/** ファイル入出力のテスト用フォルダ */
	private static final File testDir = new ClassDirectory('src/test/resources').getDeepDir(ConversionBaseSpec.class);
	/** 変換対象のテキスト */
	private static final Tpac tpacTarget = new Tpac(new File(testDir, 'target.tpac'));
	/** 変換結果として期待するテキスト */
	private static final Tpac tpacExpect = new Tpac(new File(testDir, 'expect.tpac'));
	/** WashScr */
	private static final WashScr washscr = new WashScr(existResource.get('_bltxt/washscr/bltxt.tpac'));
	
	@Timeout(10)
	@Unroll
	def 'ブロック要素が正しく整形されること'(){
		expect:
		getWashedText(parentKey, childKey) == getTpacText(parentKey, childKey);
		
		where:
		parentKey	| childKey
		'block#'	| 'head#'
		'block#'	| 'head#subhead'
		'block#'	| 'list#'
		'block#'	| 'list#dl'
		'block#'	| 'table#'
		'block#'	| 'table#complex'
		'block#'	| 'column#'
		'block#'	| 'column#blockquote'
		'block#'	| 'column#attention'
		'block#'	| 'escape#'
		'block#'	| 'raw#'
	}
	
	@Timeout(10)
	@Unroll
	def 'インライン要素が正しく整形されること'(){
		expect:
		getWashedText(parentKey, childKey) == getTpacText(parentKey, childKey);
		
		where:
		parentKey	| childKey
		'inline#'	| 'link#'
		'inline#'	| 'link#oneline'
		'inline#'	| 'link#simple'
		'inline#'	| 'strong#'
		'inline#'	| 'emphasis#'
		'inline#'	| 'small#'
		'inline#'	| 'strike#'
		'inline#'	| 'dot#'
		'inline#'	| 'rotate#'
		'inline#'	| 'note#'
		'inline#'	| 'ruby#'
	}
	
	private static String getWashedText(String parentKey, String childKey){
		return washscr.wash(tpacTarget.dec.lowers["${parentKey}"].lowers["${childKey}"].text);
	}
	
	private static String getTpacText(String parentKey, String childKey){
		return tpacExpect.dec.lowers["${parentKey}"].lowers["${childKey}"].text;
	}
}
