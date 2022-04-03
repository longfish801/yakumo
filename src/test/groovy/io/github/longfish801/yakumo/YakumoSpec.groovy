/*
 * YakumoSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo

import io.github.longfish801.yakumo.YmoConst as cnst
import io.github.longfish801.yakumo.YmoMsg as msgs
import spock.lang.Shared
import spock.lang.Specification

/**
 * Yakumoのテスト。
 * @author io.github.longfish801
 */
class YakumoSpec extends Specification {
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
				#> map
				#>> args
					String outKey, Map bltxtMap
				#>> return
					Map binds
				#>> closure
					Closure findText
					findText = { def node ->
						if (node.xmlTag == 'text') return node.text
						return node.nodes.collect { findText(it) }.join('')
					}
					binds = [
						bodytext: findText.call(bltxtMap[outKey].root)
					]
				'''.stripIndent())
			template('default', '<h1>${bodytext}</h1>')
		}
		yakumo.script {
			targets {
				target 'key1', 'Hello, World.', 'sampleSwitem'
				target 'key2', 'Bye, World.', 'sampleSwitem'
			}
			results {
				result 'key1', writer1, 'sampleClmap'
				result 'key2', writer2, 'sampleClmap'
			}
		}
		then:
		writer1.toString() == '<h1>Hello, Groovy.</h1>'
		writer2.toString() == '<h1>Hi, Groovy.</h1>'
	}
}
