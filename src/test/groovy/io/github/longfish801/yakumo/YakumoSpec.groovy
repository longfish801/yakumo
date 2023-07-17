/*
 * YakumoSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo

import io.github.longfish801.gonfig.GropedResource
import io.github.longfish801.yakumo.YmoConst as cnst
import io.github.longfish801.yakumo.YmoMsg as msgs
import spock.lang.Shared
import spock.lang.Specification

/**
 * Yakumoのテスト。
 * @author io.github.longfish801
 */
class YakumoSpec extends Specification implements GropedResource {
	/** 自クラス */
	static final Class clazz = YakumoSpec.class
	/** Yakumo */
	@Shared Yakumo yakumo
	
	def setup(){
		yakumo = new Yakumo()
	}
	
	def 'script'(){
		given:
		Writer writer1
		Writer writer2
		
		when:
		writer1 = new StringWriter()
		writer2 = new StringWriter()
		yakumo.material {
			switem('''\
				#! switem:sampleSwitem
				#> format
				#>> replace:doReplace
				World	Groovy
				Bye	Hi
				'''.stripIndent())
			clmap('''\
				#! clmap:sampleClmap
				#> closure
					fprint.info("resultKey=${resultKey}")
					if (resultKey == 'key2') fprint.warn("resultKey=${resultKey}")
					binds = [
						bodytext: "[${appendMap[resultKey]}] " + bltxtMap[resultKey].root.find { it.xmlTag == 'text' }?.text
					]
				#-args
					String resultKey
					Map bltxtMap
					Map appendMap
				#-return
					Map binds
				#> closure:prepare
					appendMap = ['key1': 'KEY1', 'key2': 'KEY2' ]
				#-dec
					import io.github.longfish801.yakumo.ConvertScript
				#-args
					Map bltxtMap
					ConvertScript script
				#-return
					Map appendMap
				'''.stripIndent())
			baseSwitemName 'sampleSwitem'
			baseClmapName 'sampleClmap'
			prepare 'sampleClmap', '/sampleClmap#prepare'
			template('default', '<h1>${bodytext}</h1>')
		}
		yakumo.script {
			targets {
				target 'key1', 'Hello, World.'
				target 'key2', 'Bye, World.'
			}
			results {
				result 'key1', writer1
				result 'key2', writer2
			}
		}
		yakumo.convert()
		then:
		writer1.toString() == '<h1>[KEY1] Hello, Groovy.</h1>'
		writer2.toString() == '<h1>[KEY2] Hi, Groovy.</h1>'
		yakumo.script.fprint.logs.size() == 3
		yakumo.script.fprint.warns.size() == 1
	}
	
	def 'script - material'(){
		given:
		Writer writer
		File parentDir
		
		when:
		setBaseDir('src/test/resources')
		parentDir = new File(deepDir, 'YakumoSpec')
		writer = new StringWriter()
		yakumo.load {
			material 'fyakumo', 'thtml'
		}
		yakumo.script {
			targets {
				target 'scriptMaterial', new File(parentDir, 'target.txt').text
			}
			results {
				result 'scriptMaterial', writer
			}
		}
		yakumo.convert()
		then:
		writer.toString().normalize() == new File(parentDir, 'result.html').text
	}
}
