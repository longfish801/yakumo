/*
 * ConvertResultsSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo

import io.github.longfish801.yakumo.YmoMsg as msgs
import spock.lang.Shared
import spock.lang.Specification

/**
 * ConvertResultsのテスト。
 * @author io.github.longfish801
 */
class ConvertResultsSpec extends Specification {
	/** ConvertResults */
	@Shared ConvertResults results
	
	def setup(){
		results = new ConvertResults()
	}
	
	def 'clmapName - exception'(){
		given:
		IllegalArgumentException exc
		
		when:
		results.clmapName('nosuch', 'thtml')
		then:
		exc = thrown(IllegalArgumentException)
		exc.message == String.format(msgs.exc.noResult, 'nosuch')
	}
	
	def 'templateKey - exception'(){
		given:
		IllegalArgumentException exc
		
		when:
		results.templateKey('nosuch', 'default')
		then:
		exc = thrown(IllegalArgumentException)
		exc.message == String.format(msgs.exc.noResult, 'nosuch')
	}
}
