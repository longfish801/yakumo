/*
 * BLLineSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo.bltxt.node;

import groovy.util.logging.Slf4j;
import spock.lang.Specification;

/**
 * BLLineのテスト。
 * @version 1.0.00 2017/08/03
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class BLLineSpec extends Specification {
	def '属性リストを返します'(){
		given:
		BLLine line;
		
		when:
		line = new BLLine(1);
		then:
		line.attrs == null;
	}
	
	def '文字列表現を返します'(){
		given:
		BLLine line;
		
		when:
		line = new BLLine(1);
		line << new BLText('テキスト１', 1);
		line << new BLText('テキスト２', 1);
		then:
		line.toString() == 'テキスト１テキスト２';
	}
}
