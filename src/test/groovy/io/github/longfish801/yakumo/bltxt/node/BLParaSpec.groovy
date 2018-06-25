/*
 * BLParaSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo.bltxt.node;

import groovy.util.logging.Slf4j;
import spock.lang.Specification;

/**
 * BLParaのテスト。
 * @version 1.0.00 2017/08/03
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class BLParaSpec extends Specification {
	def 'コンストラクタ'(){
		given:
		BLPara para;
		
		when:
		para = new BLPara(1);
		then:
		para.tag == '';
	}
	
	def '属性リストを返します'(){
		given:
		BLPara para;
		
		when:
		para = new BLPara(1);
		then:
		para.attrs == null;
	}
	
	def '文字列表現を返します'(){
		given:
		BLPara para;
		BLLine line1 = new BLLine(1);
		BLLine line2 = new BLLine(1);
		BLText text1 = new BLText('テキスト１', 1);
		BLText text2 = new BLText('テキスト２', 1);
		
		when:
		para = new BLPara(1);
		line1 << text1;
		line2 << text2;
		para << line1;
		para << line2;
		then:
		para.toString() == "テキスト１\nテキスト２";
	}
}
