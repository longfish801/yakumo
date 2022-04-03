/*
 * FootprintsSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo

import groovy.util.logging.Slf4j
import spock.lang.Specification

/**
 * Footprintsのテスト。
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class FootprintsSpec extends Specification {
	def 'getLogs'(){
		given:
		Footprints fprints
		
		when:
		fprints = new Footprints()
		fprints.trace('trace message')
		fprints.debug('debug message')
		fprints.info('info message')
		fprints.warn('warn message')
		LOG.debug('fprints.logs=' + fprints.logs)
		then:
		fprints.logs.size() == 4
		fprints.getLogs(Footprints.LEVEL_TRACE).size() == 1
		fprints.getLogs(Footprints.LEVEL_DEBUG, Footprints.LEVEL_INFO).size() == 2
		fprints.getLogs(Footprints.LEVEL_WARN).first().endsWith(' [WARN] warn message') == true
	}
}
