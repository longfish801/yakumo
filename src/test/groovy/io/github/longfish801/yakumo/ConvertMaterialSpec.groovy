/*
 * ConvertMaterialSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo

import io.github.longfish801.bltxt.BLtxt
import io.github.longfish801.gonfig.GropedResource
import io.github.longfish801.yakumo.YmoMsg as msgs
import java.lang.reflect.UndeclaredThrowableException
import spock.lang.Shared
import spock.lang.Specification

/**
 * ConvertMaterialのテスト。
 * @version 0.3.00 2022/01/10
 * @author io.github.longfish801
 */
class ConvertMaterialSpec extends Specification implements GropedResource {
	/** 自クラス */
	static final Class clazz = ConvertMaterialSpec.class
	/** ConvertMaterial */
	@Shared ConvertMaterial material
	
	def setup(){
		material = new ConvertMaterial()
	}
	
	def 'clmapProps'(){
		given:
		String script = '''\
			#! clmap:thtml
			#> map:some
			'''.stripIndent()
		
		when:
		material.clmap(script)
		material.clmapProps('thtml', null, [ boo: 'foo' ])
		material.clmapProps('thtml', '/thtml/some', [ goo: 'gaa' ])
		then:
		material.clmapServer['clmap:thtml'].properties.boo == 'foo'
		material.clmapServer['clmap:thtml'].cl('some').properties.goo == 'gaa'
	}
	
	def 'clmapProps - exception'(){
		given:
		IllegalArgumentException exc
		
		when:
		material.clmapProps('thtml', '/thtml/nosuch', [:])
		then:
		exc = thrown(IllegalArgumentException)
		exc.message == String.format(msgs.exc.noClmap, 'thtml')
		
		when:
		material.clmap('#! clmap:thtml')
		material.clmapProps('thtml', '/thtml/nosuch', [:])
		then:
		exc = thrown(IllegalArgumentException)
		exc.message == String.format(msgs.exc.noClmapForClpath, 'thtml', '/thtml/nosuch')
	}
	
	def 'parse'(){
		given:
		ConvertScript script
		Map bltxtMap
		
		when:
		material.switem(grope('fyakumo/fyakumo.tpac'))
		script = new ConvertScript()
		script.targets {
			target 'key1', '■こんにちは。', 'fyakumo'
			target 'key2', '□さようなら。', 'fyakumo'
		}
		bltxtMap = material.parse(script)
		then:
		bltxtMap.key1.toString() == '【＝見出し】こんにちは。'
		bltxtMap.key2.toString() == '【＝見出し：2】さようなら。'
		script.targets.key1.bltxt.toString() == '【＝見出し】こんにちは。' + System.lineSeparator()
		script.targets.key2.bltxt.toString() == '【＝見出し：2】さようなら。' + System.lineSeparator()
	}
	
	def 'parse - exception'(){
		given:
		ConvertScript script
		UndeclaredThrowableException exc
		
		when:
		script = new ConvertScript()
		script.targets {
			target 'key1', '■こんにちは。', 'fyakumo'
		}
		material.parse(script)
		then:
		exc = thrown(UndeclaredThrowableException)
		exc.cause.cause.message == String.format(msgs.exc.noSwitem, 'key1', 'fyakumo')
	}
	
	def 'format'(){
		given:
		StringWriter writer1
		StringWriter writer2
		ConvertScript script
		Map bltxtMap
		
		when:
		material.template('default', grope('thtml/default.html'))
		material.clmap(grope('thtml/thtml.tpac'))
		material.clmapProps('thtml', '/thtml/template', [ 'templateHandler': material.templateHandler ])
		writer1 = new StringWriter()
		writer2 = new StringWriter()
		script = new ConvertScript()
		script.results {
			result 'key1', writer1, 'thtml'
			result 'key2', writer2, 'thtml'
		}
		bltxtMap = [
			'key1': new BLtxt('【＝見出し】こんにちは。'),
			'key2': new BLtxt('【＝見出し：2】さようなら。')
		]
		material.format(script, bltxtMap)
		then:
		writer1.toString().indexOf('<h2><a name="id2_1"></a>こんにちは。</h2>') > 0
		writer2.toString().indexOf('<h3><a name="id3_1"></a>さようなら。</h3>') > 0
	}
	
	def 'format  - exception'(){
		given:
		ConvertScript script
		IllegalStateException exc
		
		when:
		material.clmap('#! clmap:thtml')
		script = new ConvertScript()
		script.results {
			result 'key1', new StringWriter(), 'noSuch'
		}
		material.format(script, [:])
		then:
		exc = thrown(IllegalStateException)
		exc.message == 'java.lang.IllegalStateException: ' + String.format(msgs.exc.noClmapForOutput, 'key1', 'noSuch')
		
		when:
		material.clmap('#! clmap:thtml')
		script = new ConvertScript()
		script.results {
			result 'key1', new StringWriter(), 'thtml'
		}
		material.format(script, [:])
		then:
		exc = thrown(IllegalStateException)
		exc.message == 'java.lang.IllegalStateException: ' + String.format(msgs.exc.noClosure, 'key1', 'thtml')
	}
}
