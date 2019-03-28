/*
 * ymoScript.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */

import org.apache.commons.io.FileUtils;

yakumo.script {
	println 'BGN ymoScript';
	
	// 設定を読みこむ
	['_bltxt', '_html'].each { configure(it) };
	configure(convDir);
	
	// 出力先フォルダを設定する
	File outDir = new File(targetDir, '../_out');
	setIO(outDir, '.html');
	
	// 固定ファイルをコピーする
	assetHandler.setup(outDir, 'overwrite');
	doFirst {
		println 'BGN doFirst';
		// FileUtils.cleanDirectory(outDir);
	}
	doLast {
		println 'BGN doLast';
		assetHandler.copy();
	}
	println 'END ymoScript';
}
