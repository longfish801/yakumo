/*
 * BLBlockSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo.bltxt.node;

import groovy.util.logging.Slf4j;
import spock.lang.Specification;

/**
 * BLBlockのテスト。
 * @version 1.0.00 2017/08/03
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class BLBlockSpec extends Specification {
	def 'コンストラクタ'(){
		when:
		new BLBlock('５－３＝２', 1);
		then:
		thrown(IllegalArgumentException);
	}
	
	def '単一か否か返します'(){
		given:
		BLBlock block;
		
		when:
		block = new BLBlock('タグ', 1);
		then:
		block.single == true;
		
		when:
		BLPara para = new BLPara(1);
		para << new BLLine(1);
		block = new BLBlock('タグ', 1);
		block << para;
		then:
		block.single == true;
		
		when:
		block = new BLBlock('タグ', 1);
		block << new BLBlock('タグ２', 1);
		then:
		block.single == false;
	}
	
	def '文字列表現を返します'(){
		given:
		BLBlock block;
		BLPara para = new BLPara(1);
		BLLine line = new BLLine(1);
		BLText text = new BLText('テキスト', 1);
		line << text;
		para << line;
		
		when:
		block = new BLBlock('タグ', 1);
		block.attrs << '属性';
		then:
		block.toString() == '【＝タグ：属性】';
		
		when:
		block = new BLBlock('タグ', 1);
		block << para;
		then:
		block.toString() == '【＝タグ】テキスト';
		
		when:
		block = new BLBlock('タグ', 1);
		block << para;
		block.attrs << '属性'
		block << new BLBlock('タグ２', 1);
		then:
		block.toString() == '''\
			【－タグ：属性】
			テキスト
			【＝タグ２】
			【タグ－】'''.stripIndent();
	}
}
