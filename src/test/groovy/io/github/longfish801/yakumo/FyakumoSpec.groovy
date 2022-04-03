/*
 * FyakumoSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo

import io.github.longfish801.gonfig.GropedResource
import io.github.longfish801.tpac.TpacServer
import io.github.longfish801.switem.SwitemServer
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Timeout
import spock.lang.Unroll

/**
 * fyakumo変換資材のテスト。
 * @author io.github.longfish801
 */
class FyakumoSpec extends Specification implements GropedResource {
	/** 自クラス */
	static final Class clazz = FyakumoSpec.class
	/** switemスクリプトを実行するクロージャ */
	@Shared Closure doRun
	/** 期待する結果を取得するクロージャ */
	@Shared Closure getExpect
	
	def setupSpec(){
		// テスト用フォルダを設定します
		setBaseDir('src/test/resources')
		File testDir = new File(deepDir, 'FyakumoSpec')
		
		// 変換対象の文字列と、期待する変換結果を読みこみます
		def teaServer = new TpacServer()
		teaServer.soak(new File(testDir, 'target.tpac'))
		def decTarget = teaServer['dec:target']
		teaServer.soak(new File(testDir, 'expect.tpac'))
		def decExpect = teaServer['dec:expect']
		
		// switemスクリプトを読みこみます
		def switem = new SwitemServer().soak(grope('fyakumo/fyakumo.tpac'))['switem:fyakumo']
		
		// 変換のためクロージャです
		doRun = { String parentKey, String childKey ->
			String text = decTarget.solve("${parentKey}/${childKey}").dflt.join(System.lineSeparator())
			return switem.run(text)
		}
		// 期待する変換結果を返すクロージャです
		getExpect = { String parentKey, String childKey ->
			return decExpect.solve("${parentKey}/${childKey}").dflt.join(System.lineSeparator())
		}
	}
	
	@Timeout(10)
	@Unroll
	def 'block'(){
		expect:
		doRun(parentKey, childKey) == getExpect(parentKey, childKey)
		
		where:
		parentKey | childKey
		'block' | 'head'
		'block' | 'list'
		'block' | 'list:misc'
		'block' | 'blockquote'
		'block' | 'code'
		'block' | 'masking'
	}
	
	@Timeout(10)
	@Unroll
	def 'inline'(){
		expect:
		doRun(parentKey, childKey) == getExpect(parentKey, childKey)
		
		where:
		parentKey | childKey
		'inline' | 'link'
		'inline' | 'link:oneline'
		'inline' | 'strong'
		'inline' | 'small'
		'inline' | 'strike'
		'inline' | 'rotate'
		'inline' | 'dot'
		'inline' | 'ruby'
	}
}
