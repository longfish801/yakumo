/*
 * ymoScript.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */

import org.apache.commons.io.FileUtils;

yakumo.setting {
	// 設定を読みこむ
	['_bltxt', '_test'].each { configure(it) };
	configure(convDir);
	
	// 出力先フォルダを設定する
	File outDir = new File(targetDir, '_out');
	setIO(outDir, '.txt');
	
	// 固定ファイルをコピーする
	assetHandler.setup(outDir, 'overwrite');
	doFirst {
		FileUtils.cleanDirectory(outDir);
	}
	doLast {
		assetHandler.copy();
	}
}
