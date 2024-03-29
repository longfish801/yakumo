/*
 * RelatedSourcesSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo

import io.github.longfish801.yakumo.YmoConst as cnst
import io.github.longfish801.yakumo.YmoMsg as msgs
import spock.lang.Shared
import spock.lang.Specification

/**
 * RelatedSourcesのテスト。
 * @author io.github.longfish801
 */
class RelatedSourcesSpec extends Specification {
	/** RelatedSources */
	@Shared RelatedSources related
	
	def setup(){
		related = new RelatedSources()
	}
	
	def 'mode - exception'(){
		given:
		YmoConvertException exc
		
		when:
		related.copyMode('noSuch')
		then:
		exc = thrown(YmoConvertException)
		exc.message == String.format(msgs.exc.invalidCopyMode, 'noSuch')
	}
	
	def 'copy - exception'(){
		given:
		YmoConvertException exc
		
		when:
		related.outDir('noSuch')
		related.copy()
		then:
		exc = thrown(YmoConvertException)
		exc.message == String.format(msgs.exc.invalidOutDir, 'noSuch')
	}
}
