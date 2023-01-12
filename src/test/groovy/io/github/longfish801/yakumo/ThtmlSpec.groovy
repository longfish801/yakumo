/*
 * ThtmlSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo

import groovy.util.logging.Slf4j
import io.github.longfish801.bltxt.BLtxt
import io.github.longfish801.clmap.ClmapServer
import io.github.longfish801.gonfig.GropedResource
import io.github.longfish801.tpac.TpacServer
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Timeout
import spock.lang.Unroll

/**
 * thtml変換資材のテスト。
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ThtmlSpec extends Specification implements GropedResource {
	/** 自クラス */
	static final Class clazz = ThtmlSpec.class
	/** HTML化のためのクロージャ */
	@Shared Closure getHtmlized
	/** bind変数取得のためクロージャ */
	@Shared Closure getBind
	/** 期待する結果を取得するクロージャ */
	@Shared Closure getExpect
	
	def setupSpec(){
		// テスト用フォルダを設定します
		setBaseDir('src/test/resources')
		File testDir = new File(deepDir, 'ThtmlSpec')
		
		// 変換対象の文字列と、期待する変換結果を読みこみます
		def teaServer = new TpacServer()
		teaServer.soak(new File(testDir, 'target.tpac'))
		def decTarget = teaServer['dec:target']
		teaServer.soak(new File(testDir, 'expect.tpac'))
		def decExpect = teaServer['dec:expect']
		
		// clmap文書を読みこみます
		def clmapServer = new ClmapServer()
		[	'tbase/util.tpac',
			'ttext/textize.tpac',
			'thtml/htmlize.tpac',
			'thtml/thtml.tpac',
		].each { clmapServer.soak(grope(it)) }
		def clmap = clmapServer['clmap:thtml']
		
		// テンプレートを読みこみます
		TemplateHandler templateHandler = new TemplateHandler()
		templateHandler.set('default', grope('thtml/default.html'))
		
		// 大域変数を設定します
		clmap.cl('/util').properties['templateHandler'] = templateHandler
		clmap.cl('/util').properties['fprint'] = new Footprints()
		clmap.cl('/util').properties['resultKey'] = 'someresult'
		
		// HTML化のためクロージャです
		getHtmlized = { String parentKey, String childKey ->
			String text = decTarget.solve("${parentKey}/${childKey}").dflt.join(System.lineSeparator())
			return clmap.cl('/htmlize').call(new BLtxt(text).root).denormalize()
		}
		// bind変数取得のためクロージャです
		getBind = { String parentKey, String childKey ->
			String text = decTarget.solve("${parentKey}/${childKey}").dflt.join(System.lineSeparator())
			return clmap.cl("bind#${childKey}").call(new BLtxt(text)).denormalize()
		}
		// 期待する変換結果を返すクロージャです
		getExpect = { String parentKey, String childKey ->
			return decExpect.solve("${parentKey}/${childKey}").dflt.join(System.lineSeparator())
		}
	}
	
	@Timeout(10)
	@Unroll
	def 'htmlize'(){
		expect:
		getHtmlized(parentKey, childKey) == getExpect(parentKey, childKey)
		
		where:
		parentKey	| childKey
		'block'	| 'paragraph'
		'block'	| 'spechar'
		'block'	| 'head'
		'block'	| 'list'
		'block'	| 'column'
		'block'	| 'column:header'
		'block'	| 'attention'
		'block'	| 'blockquote'
		'block'	| 'code'
		'block'	| 'figure'
		'block'	| 'note'
		'inline'	| 'link'
		'inline'	| 'strong'
		'inline'	| 'small'
		'inline'	| 'strike'
		'inline'	| 'verinhori'
		'inline'	| 'dot'
		'inline'	| 'ruby'
		'inline'	| 'nosuch'
	}
	
	@Timeout(10)
	@Unroll
	def 'bind'(){
		expect:
		getBind(parentKey, childKey) == getExpect(parentKey, childKey)
		
		where:
		parentKey	| childKey
		'bind'	| 'header'
		'bind'	| 'toc'
		'bind'	| 'note'
	}
}
