/*
 * ymoscript.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */

import org.apache.commons.io.FileUtils;

ymoDocument.script {
	// 出力先フォルダ
	File outDir = new File(targetDir, '_out');
	
	// 設定を読みこむ
	configure('_bltxt', '_test');
	configure(conversionDir);
	
	// 出力先フォルダを設定する
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
