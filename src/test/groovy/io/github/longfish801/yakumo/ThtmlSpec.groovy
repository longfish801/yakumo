/*
 * ThtmlSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo

import groovy.util.logging.Slf4j
import io.github.longfish801.bltxt.BLtxt
import io.github.longfish801.clmap.Clmap
import io.github.longfish801.clmap.ClmapServer
import io.github.longfish801.gonfig.GropedResource
import io.github.longfish801.tpac.TpacServer
import io.github.longfish801.yakumo.YmoConst as cnst
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
	@Shared Closure getOnedoc
	/** ナビゲーションリンク取得のためクロージャ */
	@Shared Closure getNavi
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
		
		// 変換資材を読み込みます
		MaterialLoader loader = new MaterialLoader(new Yakumo())
		loader.material('thtml')
		Footprints fprint = new Footprints()
		loader.yakumo.material.clmapServer.decs.values().each { Clmap clmap ->
			clmap.properties[cnst.clmap.footprint] = fprint
		}
		def clmap = loader.yakumo.material.clmapServer['clmap:thtml']
		clmap.cl('/tbase.logging').properties['resultKey'] = 'ThtmlSpec'
		
		// HTML化のためクロージャです
		getHtmlized = { String parentKey, String childKey ->
			String text = decTarget.solve("${parentKey}/${childKey}").dflt.join(System.lineSeparator())
			return clmap.cl('/thtml.htmlize').call(new BLtxt(text).root).denormalize()
		}
		// onedoc変数取得のためクロージャです
		getOnedoc = { String parentKey, String childKey ->
			String text = decTarget.solve("${parentKey}/${childKey}").dflt.join(System.lineSeparator())
			return clmap.cl("/thtml.meta/onedoc#${childKey}").call(new BLtxt(text)).denormalize()
		}
		// ナビゲーションリンク取得のためクロージャです
		getNavi = { String childKey, String resultKey, List order ->
			return clmap.cl("/thtml.meta/navi#${resultKey}").call(resultKey, order).denormalize()
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
		'other'	| '平文'
		'other'	| '段落'
		'block'	| '見出し'
		'block'	| '箇条書き'
		'block'	| '用語説明'
		'block'	| '画像'
		'block'	| 'コラム'
		'block'	| 'コラム:小見出し'
		'block'	| '注意'
		'block'	| '引用'
		'block'	| 'コード'
		'block'	| '変換済'
		'block'	| '行範囲'
		'inline'	| '註'
		'inline'	| 'リンク'
		'inline'	| '注目'
		'inline'	| '重要'
		'inline'	| '補足'
		'inline'	| '特記'
		'inline'	| '訂正'
		'inline'	| '上付き'
		'inline'	| '下付き'
		'inline'	| '縦中横'
		'inline'	| '傍点'
		'inline'	| 'ルビ'
		'inline'	| '範囲'
		'inline'	| 'nosuch'
	}
	
	@Timeout(10)
	@Unroll
	def 'onedoc'(){
		expect:
		getOnedoc(parentKey, childKey) == getExpect(parentKey, childKey)
		
		where:
		parentKey	| childKey
		'onedoc'	| 'subtitle'
		'onedoc'	| 'extra'
		'onedoc'	| 'toc'
		'onedoc'	| 'note'
	}
	
	@Timeout(10)
	@Unroll
	def 'navi'(){
		expect:
		getNavi(childKey, resultKey, order) == getExpect('navi', childKey)
		
		where:
		childKey		| resultKey	| order
		'index-noorder'	| 'index'	| null
		'some-noorder'	| 'some'	| null
		'index-order'	| 'index'	| [ 'some1.html', 'some2.html', 'some3.html' ]
		'bgn-order'		| 'some1'	| [ 'some1.html', 'some2.html', 'some3.html' ]
		'mdl-order'		| 'some2'	| [ 'some1.html', 'some2.html', 'some3.html' ]
		'end-order'		| 'some3'	| [ 'some1.html', 'some2.html', 'some3.html' ]
	}
}
