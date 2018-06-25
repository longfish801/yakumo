/*
 * BLInlineSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo.bltxt.node;

import groovy.util.logging.Slf4j;
import spock.lang.Specification;

/**
 * BLInlineのテスト。
 * @version 1.0.00 2017/08/03
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class BLInlineSpec extends Specification {
	def 'コンストラクタ'(){
		when:
		new BLInline('５－３＝２', 1);
		then:
		thrown(IllegalArgumentException);
	}
	
	def '文字列表現を返します'(){
		given:
		BLInline inline;
		
		when:
		inline = new BLInline('タグ', 1);
		then:
		inline.toString() == '【タグ】';
		
		when:
		inline = new BLInline('タグ', 1);
		inline.attrs << '属性';
		then:
		inline.toString() == '【タグ：属性】';
		
		when:
		inline = new BLInline('タグ', 1);
		inline << new BLText('テキスト', 1);
		then:
		inline.toString() == '【｜タグ】テキスト【タグ｜】';
	}
}
