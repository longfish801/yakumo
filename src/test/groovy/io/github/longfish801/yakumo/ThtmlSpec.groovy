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
	/** ひとつの BLtxt文書に基づく HTML部品取得のためクロージャ */
	@Shared Closure getOnedoc
	/** ナビゲーションリンク取得のためクロージャ */
	@Shared Closure getNavi
	/** 複数の変換結果に関する横断的な処理結果取得のためクロージャ */
	@Shared Closure getCross
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
		def clmap = loader.yakumo.material.clmapServer['clmap:thtml']
		
		ConvertScript script = new ConvertScript()
		script.results {
			result 'some', new StringWriter()
		}
		clmap.cl('/thtml#prepare').call(['some': new BLtxt('')], script)
		
		// HTML化のためクロージャです
		getHtmlized = { String parentKey, String childKey ->
			String text = decTarget.solve("${parentKey}/${childKey}").dflt.join(System.lineSeparator())
			BLtxt bltxt = new BLtxt(text)
			if (childKey == '参照'){
				Map bltxtMap = [ 'some': bltxt ]
				clmap.cl('/thtml.htmlize/inline').properties['targetMap'] = bltxtMap.collectEntries { [ it.value.root, it.key ] }
				clmap.cl('/thtml.crosscut/headline').properties['headerMap'] = clmap.cl('/thtml.crosscut/headline#headermap').call(bltxtMap)
			}
			return clmap.cl('/thtml.htmlize').call(bltxt.root).denormalize()
		}
		// ひとつの BLtxt文書に基づく HTML部品取得のためクロージャです
		getOnedoc = { String parentKey, String childKey ->
			String text = decTarget.solve("${parentKey}/${childKey}").dflt.join(System.lineSeparator())
			return clmap.cl("/thtml.meta/onedoc#${childKey}").call(new BLtxt(text)).denormalize()
		}
		// ナビゲーションリンク取得のためクロージャです
		getNavi = { String childKey, String resultKey, List resultKeys ->
			clmap.cl('/thtml.crosscut/navi').properties['resultKeys'] = resultKeys
			clmap.cl('/thtml.crosscut/navi#').closure = null
			return clmap.cl('/thtml.crosscut/navi#').call(resultKey).denormalize()
		}
		// 複数の変換結果に関する横断的な処理結果のためクロージャです
		getCross = { String parentKey, String childKey, String clname ->
			String text = decTarget.solve("${parentKey}/${childKey}-${clname}").dflt.join(System.lineSeparator())
			return clmap.cl("/thtml.crosscut/${childKey}#${clname}").call([ 'some': new BLtxt(text) ]).denormalize()
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
		'inline'	| '参照'
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
		getNavi(childKey, resultKey, resultKeys) == getExpect('navi', childKey)
		
		where:
		childKey			| resultKey	| resultKeys
		'onefile'			| 'some'	| [ 'some' ]
		'onefile-index'		| 'index'	| [ 'index' ]
		'twofile'			| 'some'	| [ 'index', 'some' ]
		'twofile-index'		| 'index'	| [ 'index', 'some' ]
		'moreFile-index'	| 'index'	| [ 'index', 'some1', 'some2', 'some3' ]
		'moreFile-first'	| 'some1'	| [ 'index', 'some1', 'some2', 'some3' ]
		'moreFile-middle'	| 'some2'	| [ 'index', 'some1', 'some2', 'some3' ]
		'moreFile-last'		| 'some3'	| [ 'index', 'some1', 'some2', 'some3' ]
	}
	
	@Timeout(10)
	@Unroll
	def 'cross'(){
		expect:
		getCross(parentKey, childKey, clname) == getExpect(parentKey, "${childKey}-${clname}")
		
		where:
		parentKey	| childKey	| clname
		'cross'		| 'toc'		| ''
		'cross'		| 'toc'		| 'h1'
	}
}
