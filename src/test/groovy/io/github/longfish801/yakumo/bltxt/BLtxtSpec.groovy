/*
 * BLtxtSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo.bltxt;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.util.ClassDirectory;
import spock.lang.Specification;
import spock.lang.Unroll;

/**
 * BLtxtのテスト。
 * @version 1.0.00 2017/08/03
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class BLtxtSpec extends Specification {
	/** ファイル入出力のテスト用フォルダ */
	private static final File testDir = new ClassDirectory('src/test/resources').getDeepDir(BLtxtSpec.class);
	
	def 'ファイル内容をBLtxt文書とみなして解析結果を保持するコンストラクタです'(){
		given:
		BLtxt bltxt = null;
		
		when:
		bltxt = new BLtxt(new File(testDir, 'target.txt'));
		then:
		bltxt.toString() == 'これがテストです。';
	}
	
	def 'BLtxt記法の文字列を解析し結果を保持するコンストラクタです'(){
		given:
		BLtxt bltxt = null;
		
		when:
		bltxt = new BLtxt('これはテストです。');
		then:
		bltxt.toString() == 'これはテストです。';
	}
	
	@Unroll
	def 'XML形式で表現した文字列を返します'(){
		expect:
		new BLtxt(new File(testDir, "${dir}/${fname}.txt")).toXml() == new File(testDir, "${dir}/${fname}.xml").getText();
		
		where:
		dir			| fname
		'sample'	| '01'
		'sample'	| '02'
		'sample'	| '03'
		'sample'	| '04'
		'sample'	| '05'
		'sample'	| '06'
		'sample'	| '07'
		'valiation'	| '01'
		'valiation'	| '02'
		'valiation'	| '03'
	}
	
	def 'XML形式で表現した文字列を返します（複雑な場合）'(){
		// Unrollで実行すると OutOfMemoryErrorが発生するため、独立して実施します。
		given:
		String text;
		String result;
		String expected;
		
		when:
		text = new File(testDir, 'complex/blxml.txt').getText();
		result = new BLtxt(text).toXml();
		expected = new File(testDir, 'complex/blxml.xml').getText();
		LOG.debug('result=[{}]', result);
		
		then:
		result == expected;
	}
	
	def '直近まで解析していた行を返します'(){
		given:
		String expected = '''\
			構文誤りのためBLtxt文書の解析に失敗しました。詳細=Encountered " <NEWLINE> "\\n "" at line 6, column 2.
			Was expecting one of:
			    <SAFE_TXT> ...
			    <SAFE_TXT> ...
			    <SAFE_TXT> ...
			    <SAFE_TXT> ...
			    <SAFE_TXT> ...
			    <SAFE_TXT> ...
			     エラー発生行付近=bbb
			ccc
			ddd
			eee
			【
			-----'''.stripIndent().replaceAll("\n", System.getProperty('line.separator'));
		String target = '''\
			aaa
			bbb
			ccc
			ddd
			eee
			【
			fff
			ggg
			hhh
			'''.stripIndent();
		BLtxt bltxt = null;
		BLtxt.BLtxtParseException exc = null;
		
		when:
		bltxt = new BLtxt(target);
		then:
		exc = thrown(BLtxt.BLtxtParseException);
		exc.message == expected;
	}
}
