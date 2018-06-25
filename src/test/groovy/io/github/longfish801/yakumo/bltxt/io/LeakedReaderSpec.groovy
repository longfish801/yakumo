/*
 * LeakedReaderSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo.bltxt.io;

import groovy.util.logging.Slf4j;
import spock.lang.Specification;

/**
 * LeakedReaderのテスト。
 * @version 1.0.00 2018/01/21
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class LeakedReaderSpec extends Specification {
	def 'Readerから読込をすると、その内容を Writerへ書きこむこと'(){
		given:
		String result;
		String expected = '''\
			aaa
			bbb
			ccc'''.stripIndent();
		Writer writer = new StringWriter();
		Reader reader = new LeakedReader(new StringReader(expected), writer);
		
		when:
		int character;
		List chars = [];
		while ((character = reader.read()) != -1){
			chars << character;
		}
		result = new String(chars as char[]);
		
		then:
		result == expected;
		writer.toString() == expected;
	}
	
	def '長い文字列やマルチバイト文字も書きこまれること'(){
		given:
		String result;
		String expected = 'あ' * 10000;
		Writer writer = new StringWriter();
		Reader reader = new LeakedReader(new StringReader(expected), writer);
		
		when:
		int character;
		List chars = [];
		while ((character = reader.read()) != -1){
			chars << character;
		}
		result = new String(chars as char[]);
		
		then:
		result == expected;
		writer.toString() == expected;
	}
}
