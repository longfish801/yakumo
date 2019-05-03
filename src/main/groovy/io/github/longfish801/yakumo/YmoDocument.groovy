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
	/** FileFinder */
	FileFinder fileFinder;
	
	/**
	 * コンストラクタ。
	 * @param targetDir 変換対象フォルダ
	 */
	YmoDocument(File targetDir){
		ArgmentChecker.checkExistDirectory('変換対象フォルダ', targetDir);
		this.targetDir = targetDir;
		fileFinder = new FileFinder(targetDir);
	}
	
	/**
	 * 変換スクリプトを実行します。
	 * @param convDir 変換資材格納フォルダ
	 */
	void run(File convDir){
		ArgmentChecker.checkExistDirectory('変換資材格納フォルダ', convDir);
		File scriptFile = new File(convDir, cnstYmoDocument.script.fileName);
		super.run(scriptFile, [ 'targetDir': targetDir, 'convDir': convDir ]);
	}
	
	/**
	 * 解析対象と出力先およびコピーする固定ファイルを設定します。<br>
	 * 変換対象フォルダ直下のファイル（拡張子が txt）すべてを変換元とします。<br>
	 * 変換元と同じファイル名で、引数に指定された拡張子で出力フォルダ直下に出力します。<br>
	 * 解析対象キー、出力先キーは入力ファイルのファイル名（拡張子を除く）を使用します。
	 * @param outDir 出力フォルダ
	 * @param ext 出力ファイルの拡張子（半角ドット始まり）
	 * @param convNameTarget 解析工程に使用する変換名
	 * @param convNameOut 適用工程に使用する変換名
	 */
	void setup(File outDir, String ext, String convNameTarget, String convNameOut){
		// 解析対象、出力先を設定する
		fileFinder.find('.', cnstYmoDocument.target.includePattern, cnstYmoDocument.target.excludePattern).each {
			URL inFile = it.value;
			String key = FilenameUtils.getBaseName(inFile.path)
			engine.appendTarget(key, inFile, convNameTarget);
			engine.appendOut(key, new File(outDir, "${key}${ext}"), convNameOut);
		}
		// 固定ファイルの出力フォルダ、コピー対象を設定する
		assetHandler.outDir(outDir);
		assetHandler.gulp('asset', fileFinder.find('.', cnstYmoDocument.asset.includePattern, cnstYmoDocument.asset.excludePattern));
	}
}
