/*
 * MaterialLoaderSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo

import io.github.longfish801.yakumo.YmoConst as cnst
import io.github.longfish801.yakumo.YmoMsg as msgs
import spock.lang.Shared
import spock.lang.Specification

/**
 * MaterialLoaderのテスト。
 * @author io.github.longfish801
 */
class MaterialLoaderSpec extends Specification {
	/** MaterialLoader */
	@Shared MaterialLoader loader
	
	def setup(){
		loader = new MaterialLoader()
	}
	
	def 'material - exception'(){
		given:
		YmoConvertException exc
		File convDir
		
		when:
		loader.material('noSuch')
		then:
		exc = thrown(YmoConvertException)
		exc.message == String.format(msgs.exc.noSuchMaterialResource, "noSuch/${cnst.material.fileName}")
		
		when:
		convDir = new File('noSuch')
		loader.material(convDir)
		then:
		exc = thrown(YmoConvertException)
		exc.message == String.format(msgs.exc.noSuchMaterialFile, new File(convDir, cnst.material.fileName).absolutePath)
	}
}
