/*
 * ConvertMaterialSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo

import groovy.util.logging.Slf4j
import io.github.longfish801.bltxt.BLtxt
import io.github.longfish801.gonfig.GropedResource
import io.github.longfish801.yakumo.YmoMsg as msgs
import java.lang.reflect.UndeclaredThrowableException
import spock.lang.Shared
import spock.lang.Specification

/**
 * ConvertMaterialのテスト。
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ConvertMaterialSpec extends Specification implements GropedResource {
	/** 自クラス */
	static final Class clazz = ConvertMaterialSpec.class
	/** ConvertMaterial */
	@Shared ConvertMaterial material
	
	def setup(){
		material = new ConvertMaterial()
	}
	
	def 'clmapProp'(){
		given:
		String script = '''\
			#! clmap:thtml
			#> map:some
			'''.stripIndent()
		
		when:
		material.clmap(script)
		material.clmapProp('/thtml', 'boo', 'foo')
		material.clmapProp('/thtml/some', 'goo', 'gaa')
		then:
		material.clmapServer['clmap:thtml'].properties.boo == 'foo'
		material.clmapServer['clmap:thtml'].cl('some').properties.goo == 'gaa'
	}
	
	def 'clmapProp - exception'(){
		given:
		IllegalArgumentException exc
		
		when:
		material.clmapProp('/thtml/nosuch', 'boo', 'foo')
		then:
		exc = thrown(IllegalArgumentException)
		exc.message == String.format(msgs.exc.noClmap, '/thtml/nosuch')
	}
	
	def 'parse'(){
		given:
		ConvertScript script
		Map bltxtMap
		
		when:
		material.switem(grope('fyakumo/fyakumo.tpac'))
		script = new ConvertScript()
		script.targets {
			target 'key1', '■こんにちは。'
			target 'key2', '□さようなら。'
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
			target 'key1', '■こんにちは。'
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
		material.clmap('''\
			#! clmap:thtml
			#> closure
				binds = [ text: bltxtMap[resultKey].toString() ]
			#-args
				String resultKey
				Map bltxtMap
				Map appendMap
			#-return
				Map binds
			'''.stripIndent())
		
		material.template('default', '${text}')
		writer1 = new StringWriter()
		writer2 = new StringWriter()
		script = new ConvertScript()
		script.results {
			result 'key1', writer1
			result 'key2', writer2
		}
		bltxtMap = [
			'key1': new BLtxt('【＝見出し】こんにちは。'),
			'key2': new BLtxt('【＝見出し：2】さようなら。')
		]
		material.format(script, bltxtMap)
		then:
		writer1.toString() == '【＝見出し】こんにちは。'
		writer2.toString() == '【＝見出し：2】さようなら。'
	}
	
	def 'format  - exception'(){
		given:
		ConvertScript script
		IllegalStateException exc
		
		when:
		material.clmap('#! clmap:thtml')
		script = new ConvertScript()
		script.results {
			baseClmapName 'noSuch'
			result 'key1', new StringWriter()
		}
		material.format(script, [:])
		then:
		exc = thrown(IllegalStateException)
		exc.message == String.format(msgs.exc.noClmapForResult, 'key1', 'noSuch')
		
		when:
		material.clmap('#! clmap:thtml')
		script = new ConvertScript()
		script.results {
			result 'key1', new StringWriter()
		}
		material.format(script, [:])
		then:
		exc = thrown(IllegalStateException)
		exc.message == 'java.lang.IllegalStateException: ' + String.format(msgs.exc.noClosure, 'key1', 'thtml')
	}
}
