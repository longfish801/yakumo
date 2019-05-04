/*
 * ConversionBaseSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.ExchangeResource;
import io.github.longfish801.shared.PackageDirectory;
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
 * BLtxt変換のテスト。
 * @version 1.0.00 2017/09/28
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ConversionBaseSpec extends Specification {
	/** ファイル入出力のテスト用フォルダ */
	static final File testDir = PackageDirectory.deepDir('src/test/resources', ConversionBaseSpec.class);
	/** Washsh */
	@Shared Washsh washsh;
	/** 変換対象のテキスト */
	@Shared TeaDec decTarget;
	/** 変換結果として期待するテキスト */
	@Shared TeaDec decExpect;
	/** washスクリプトを実行するクロージャ */
	@Shared Closure doWash;
	/** 期待する結果を取得するクロージャ */
	@Shared Closure doExpect;
	
	def setup(){
		TeaServer teaServer = new TpacServer();
		teaServer.soak(new File(testDir, 'target.tpac'));
		teaServer.soak(new File(testDir, 'expect.tpac'));
		decTarget = teaServer['dec:target'];
		decExpect = teaServer['dec:expect'];
		WashServer washServer = new WashServer();
		washServer.soak(ExchangeResource.url(ConversionBaseSpec.class, '_bltxt/bltxt.tpac'));
		washsh = washServer["washsh:_bltxt"];
		doWash = { String parentKey, String childKey ->
			String text = decTarget.lowers["${parentKey}"].lowers["${childKey}"].text.toString()
			return washsh.wash(text);
		}
		doExpect = { String parentKey, String childKey ->
			return decExpect.lowers["${parentKey}"].lowers["${childKey}"].text.toString();
		}
	}
	
	@Timeout(10)
	@Unroll
	def 'ブロック要素が正しく整形されること'(){
		expect:
		doWash(parentKey, childKey) == doExpect(parentKey, childKey);
		
		where:
		parentKey	| childKey
		'block:'	| 'head:'
		'block:'	| 'list:'
		'block:'	| 'list:ol'
		'block:'	| 'list:misc'
		'block:'	| 'list:dl'
		'block:'	| 'table:'
		'block:'	| 'table:complex'
		'block:'	| 'code:'
		'block:'	| 'pre:'
		'block:'	| 'column:range'
		'block:'	| 'column:blockquote'
		'block:'	| 'column:attention'
		'block:'	| 'masking:'
		'block:'	| 'blescape:'
	}
	
	@Timeout(10)
	@Unroll
	def 'インライン要素が正しく整形されること'(){
		expect:
		doWash(parentKey, childKey) == doExpect(parentKey, childKey);
		
		where:
		parentKey	| childKey
		'inline:'	| 'link:'
		'inline:'	| 'link:oneline'
		'inline:'	| 'link:simple'
		'inline:'	| 'strong:'
		'inline:'	| 'emphasis:'
		'inline:'	| 'small:'
		'inline:'	| 'strike:'
		'inline:'	| 'dot:'
		'inline:'	| 'rotate:'
		'inline:'	| 'ruby:'
	}
}
