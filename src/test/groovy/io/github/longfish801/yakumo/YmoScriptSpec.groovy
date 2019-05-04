/*
 * YmoScriptSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.PackageDirectory;
import spock.lang.Specification;
import spock.lang.Timeout;
import spock.lang.Unroll;

/**
 * YmoScriptのテスト。
 * @version 1.0.00 2017/08/03
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class YmoScriptSpec extends Specification {
	/** ファイル入出力のテスト用フォルダ */
	static final File testDir = PackageDirectory.deepDir('src/test/resources', YmoScriptSpec.class);
	
	@Timeout(10)
	def '文字列を変換し、結果を返します'(){
		given:
		String target = '''\
			■カレーの作り方
			'''.stripIndent();
		String expected = '''\
			これはテストです。
			【＝見出し】カレーの作り方
			'''.stripIndent();
		
		expect:
		YmoScript.convert('_bltxt', '_test', target) == expected;
	}
	
	@Timeout(10)
	def '文字列をHTMLに変換し、結果を返します'(){
		given:
		String target = new File(testDir, 'target2.txt').getText('UTF-8');
		String expect = new File(testDir, 'expect2.txt').getText('UTF-8');
		
		expect:
		YmoScript.convert('_bltxt', '_html', target).denormalize() == expect;
	}
	
	@Timeout(10)
	def '入力ファイルに対して変換し、結果を出力ファイルに書きこみます'(){
		given:
		File outFile = new File(testDir, 'out.txt');
		File assetFile = new File(testDir, 'asset.txt');
		outFile.delete();
		assetFile.delete();
		String expected = '''\
			これはテストです。
			【＝見出し】カレーの作り方
			'''.stripIndent();
		
		when:
		YmoScript.convert('_bltxt', '_test', new File(testDir, 'target.txt'), outFile);
		
		then:
		outFile.text == expected;
		assetFile.exists() == true;
	}
}
