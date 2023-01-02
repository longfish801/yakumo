/*
 * ConvertTargetsSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo

import io.github.longfish801.yakumo.YmoMsg as msgs
import spock.lang.Shared
import spock.lang.Specification

/**
 * ConvertTargetsのテスト。
 * @author io.github.longfish801
 */
class ConvertTargetsSpec extends Specification {
	/** ConvertTargets */
	@Shared ConvertTargets targets
	
	def setup(){
		targets = new ConvertTargets()
	}
	
	def 'switemName - exception'(){
		given:
		IllegalArgumentException exc
		
		when:
		targets.switemName('nosuch', 'thtml')
		then:
		exc = thrown(IllegalArgumentException)
		exc.message == String.format(msgs.exc.noTarget, 'nosuch')
	}
}
