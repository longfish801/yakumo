/*
 * YmoDocument.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.ArgmentChecker;
import io.github.longfish801.shared.ExchangeResource;
import io.github.longfish801.yakumo.util.FileFinder;
import org.apache.commons.io.FilenameUtils;

/**
 * 特定のフォルダ直下に存在するテキストファイルの一括変換を実現します。
 * @version 1.0.00 2017/08/07
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class YmoDocument extends YmoScript {
	/** ConfigObject */
	static final ConfigObject cnstYmoDocument = ExchangeResource.config(YmoDocument.class);
	/** 変換対象フォルダ */
	File targetDir = null;
	
	/**
	 * コンストラクタ。
	 * @param targetDir 変換対象フォルダ
	 */
	YmoDocument(File targetDir){
		ArgmentChecker.checkExistDirectory('変換対象フォルダ', targetDir);
		this.targetDir = targetDir;
	}
	
	/**
	 * 変換スクリプトを実行します。
	 * @param convName 変換名
	 */
	void run(String convName){
		ArgmentChecker.checkNotBlank('変換名', convName);
		File convDir = new File(targetDir, convName);
		ArgmentChecker.checkExistDirectory('設定フォルダ', convDir);
		File scriptFile = new File(convDir, cnstYmoDocument.script.fileName);
		super.run(scriptFile, [ 'targetDir': targetDir, 'convName': convName, 'convDir': convDir ]);
	}
	
	/**
	 * 変換元と出力先との対応を設定します。<br>
	 * 変換対象フォルダ直下のファイル（拡張子が txt）を変換元とします。<br>
	 * 変換元と同じファイル名で、引数に指定された拡張子で出力フォルダ直下に出力します。<br>
	 * 変換元キーは入力ファイルのファイル名（拡張子を除く）を使用します。
	 * @param outDir 出力フォルダ
	 * @param ext 出力ファイルの拡張子
	 */
	void setIO(File outDir, String ext){
		List files = targetDir.listFiles().findAll {
			it.isFile() && FileFinder.wildcardMatch(it.name, cnstYmoDocument.target.includePattern, cnstYmoDocument.target.excludePattern)
		};
		files.each { File inFile ->
			String sourceKey = FilenameUtils.getBaseName(inFile.name);
			engine.sourceMap[sourceKey] = inFile;
			File outFile = new File(outDir, "${sourceKey}${ext}");
			engine.outMap[sourceKey] = outFile;
		}
	}
}
