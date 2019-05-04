/*
 * ymoScript.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */

// 出力先フォルダ
File outDir = new File(targetDir, '_out');

import org.apache.commons.io.FileUtils;

yakumo.script {
	// 変換資材を読みこむ
	['_bltxt', '_test', convDir].each { configure(it) };
	// 出力先フォルダなどを設定する
	setup(outDir, '.txt', '_bltxt', '_test');
	// 出力先フォルダを空にしておく
	FileUtils.cleanDirectory(outDir);
}
