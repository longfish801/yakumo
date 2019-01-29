/*
 * YmoDocumentSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.PackageDirectory;
import org.apache.commons.io.FileUtils;
import spock.lang.Specification;
import spock.lang.Timeout;

/**
 * YmoDocumentのテスト。
 * @version 1.0.00 2017/09/07
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class YmoDocumentSpec extends Specification {
	/** ファイル入出力のテスト用フォルダ */
	static final File testDir = PackageDirectory.deepDir('src/test/resources', YmoDocumentSpec.class);
	
	@Timeout(10)
	def '特定のフォルダ直下に存在するテキストファイルの変換を実現します'(){
		given:
		String expected = '''\
			これはテスト。
			【＝見出し：2】カレーの作り方
			'''.stripIndent();
		File targetDir = new File(testDir, 'input');
		File outDir = new File(targetDir, '_out');
		
		when:
		new YmoDocument(targetDir).run('_test2');
		
		then:
		new File(outDir, 'target.txt').text == expected;
		new File(outDir, 'asset.txt').exists() == true;
		new File(outDir, 'asset2.txt').exists() == true;
	}
}
