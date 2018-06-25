/*
 * BLAttrsSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo.bltxt.node;

import groovy.util.logging.Slf4j;
import spock.lang.Specification;

/**
 * BLAttrsのテスト。
 * @version 1.0.00 2017/08/03
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class BLAttrsSpec extends Specification {
	def 'BLtxt記法上エスケープが必要な文字があればエスケープします'(){
		given:
		BLAttrs<String> attrs;
		
		when:
		attrs = new BLAttrs<String>();
		attrs << '【ここから';
		attrs << '：そして：';
		attrs << '￥';
		attrs << 'ここまで】';
		
		then:
		attrs.toString() == '：￥【ここから：￥：そして￥：：￥￥：ここまで￥】';
	}
}
