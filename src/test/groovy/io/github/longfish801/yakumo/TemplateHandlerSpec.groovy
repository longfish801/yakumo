/*
 * TemplateHandlerSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo

import io.github.longfish801.yakumo.YmoConst as cnst
import io.github.longfish801.yakumo.YmoMsg as msgs
import spock.lang.Shared
import spock.lang.Specification

/**
 * TemplateHandlerのテスト。
 * @version 0.3.00 2022/01/10
 * @author io.github.longfish801
 */
class TemplateHandlerSpec extends Specification {
	/** TemplateHandler */
	@Shared TemplateHandler handler
	
	def setup(){
		handler = new TemplateHandler()
	}
	
	def 'apply'(){
		given:
		Writer writer
		
		when:
		writer = new StringWriter()
		handler.set('hello', 'Hello, ${name}!')
		handler.apply('hello', writer, [name: 'Taro'])
		then:
		writer.toString() == 'Hello, Taro!'
	}
	
	def 'apply - exception'(){
		given:
		YmoConvertException exc
		
		when:
		handler.apply('noSuch', new StringWriter(), [:])
		then:
		exc = thrown(YmoConvertException)
		exc.message == String.format(msgs.exc.noTemplate, 'noSuch', [:].keySet())
	}
}
