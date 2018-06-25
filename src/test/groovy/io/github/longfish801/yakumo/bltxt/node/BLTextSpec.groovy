/*
 * BLTextSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo.bltxt.node;

import groovy.util.logging.Slf4j;
import spock.lang.Specification;

/**
 * BLTextのテスト。
 * @version 1.0.00 2017/08/03
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class BLTextSpec extends Specification {
	def 'コンストラクタ'(){
		when:
		new BLText('', 1);
		then:
		thrown(IllegalArgumentException);
	}
	
	def '属性リストを返します'(){
		given:
		BLText text;
		
		when:
		text = new BLText('テスト', 1);
		then:
		text.attrs == null;
	}
	
	def '下位ノードを返します'(){
		given:
		BLText text;
		
		when:
		text = new BLText('テスト', 1);
		then:
		text.nodes == null;
	}
	
	def '下位ノードを追加します'(){
		given:
		BLText text;
		
		when:
		text = new BLText('テスト', 1);
		text << new BLText('テスト２', 1);
		then:
		thrown(UnsupportedOperationException);
	}
	
	def 'BLtxt記法上エスケープが必要な文字があればエスケープします'(){
		given:
		BLText text;
		
		when:
		text = new BLText('【ここから：そして：￥ここまで】', 1);
		then:
		text.toString() == '￥【ここから：そして：￥￥ここまで￥】';
	}
}
