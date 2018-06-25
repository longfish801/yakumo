/*
 * YmoDocument.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.lang.ArgmentChecker;
import io.github.longfish801.shared.util.ClassSlurper;
import io.github.longfish801.shared.util.TextUtil;
import org.apache.commons.io.FilenameUtils;

/**
 * 特定のフォルダ直下に存在するテキストファイルの一括変換を実現します。
 * @version 1.0.00 2017/08/07
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class YmoDocument extends YmoScript {
	/** ConfigObject */
	protected static final ConfigObject constants = ClassSlurper.getConfig(YmoDocument.class);
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
	 * @param conversionName 変換名
	 */
	void run(String conversionName){
		ArgmentChecker.checkNotBlank('変換名', conversionName);
		File conversionDir = new File(targetDir, conversionName);
		ArgmentChecker.checkExistDirectory('設定フォルダ', conversionDir);
		File scriptFile = new File(conversionDir, constants.configure.fileName);
		ArgmentChecker.checkExistFile('変換スクリプト', scriptFile);
		shell.setVariable('ymoDocument', this);
		shell.setVariable('engine', engine);
		shell.setVariable('assetHandler', assetHandler);
		shell.setVariable('conversionName', conversionName);
		shell.setVariable('targetDir', targetDir);
		shell.setVariable('conversionDir', conversionDir);
		shell.run(scriptFile, []);
	}
	
	/**
	 * 設定フォルダから、各種設定を参照します。<br>
	 * 設定フォルダ配下にある事前整形スクリプト、クロージャマップ、テンプレート、メタ定義、固定ファイルを読みこみます。
	 * @param conversionDirs ひとつ以上の設定フォルダ
	 */
	void configure(File... conversionDirs){
		ArgmentChecker.checkNotEmptyList('変換対象フォルダリスト', conversionDirs as List);
		(conversionDirs as List).each { File conversionDir ->
			if (!conversionDir.isDirectory()) LOG.warn('変換対象フォルダが存在しません。conversionDir={}', conversionDir.path);
			engine.configureWashscr(find(new File(conversionDir, constants.washscr.dirName), constants.washscr.includePattern, constants.washscr.excludePattern).values() as List);
			engine.configureClmap(find(new File(conversionDir, constants.clmap.dirName), constants.clmap.includePattern, constants.clmap.excludePattern).values() as List);
			engine.configureTemplate(find(new File(conversionDir, constants.template.dirName), constants.template.includePattern, constants.template.excludePattern));
			engine.configureMeta(find(new File(conversionDir, constants.meta.dirName), constants.meta.includePattern, constants.meta.excludePattern).values() as List);
			assetHandler.gulp(conversionDir.name, find(new File(conversionDir, constants.asset.dirName), constants.asset.includePattern, constants.asset.excludePattern));
		}
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
		List files = targetDir.listFiles().findAll { it.isFile() && TextUtil.wildcardMatch(it.name, constants.target.includePattern, constants.target.excludePattern) };
		files.each { File inFile ->
			String sourceKey = FilenameUtils.getBaseName(inFile.name);
			File outFile = new File(outDir, "${sourceKey}${ext}");
			engine.setIO(sourceKey, inFile, outFile);
		}
	}
	
	/**
	 * 特定のフォルダ配下から名前がパターンに一致するファイルのマップを返します。<br/>
	 * 対象フォルダが存在しない場合は、空マップを返します。
	 * @param targetDir 対象フォルダ
	 * @param includePatterns ファイル名の適合パターンリスト
	 * @param excludePatterns ファイル名の除外パターンリスト
	 * @return パターンに一致するファイルのマップ（キーは対象フォルダからの相対パス、値はファイルのURL）
	 */
	protected Map<String, URL> find(File targetDir, List includePatterns, List excludePatterns){
		if (!targetDir.exists()) return [:];
		
		// 指定フォルダ配下のファイルについて、相対パスとファイルのURLとのマップを作成するクロージャです
		// サブフォルダも再帰的に探索します
		Closure collectFiles;
		collectFiles = { Map map, File curDir, String curPath ->
			curDir.listFiles().each { File elem ->
				switch (elem){
					case { it.isFile() }:
						map["${curPath}${elem.name}"] = elem.toURI().toURL();
						break;
					case { it.isDirectory() }:
						collectFiles(map, elem, "${curPath}${elem.name}/");
						break;
					default:
						LOG.debug('ファイルでもフォルダでもないため、無視します。elem={}', elem.path);
				}
			}
		}
		
		// 変換対象フォルダ直下のファイルおよびフォルダについて、コピー対象か確認します
		Map<String, URL> assetMap = [:];
		targetDir.listFiles().each { File elem ->
			switch (elem){
				case { it.isFile() }:
					if (TextUtil.wildcardMatch(elem.name, includePatterns, excludePatterns)){
						assetMap["${elem.name}"] = elem.toURI().toURL();
					}
					break;
				case { it.isDirectory() }:
					if (TextUtil.wildcardMatch(elem.name, includePatterns, excludePatterns)){
						collectFiles(assetMap, elem, "${elem.name}/");
					}
					break;
				default:
					LOG.debug('ファイルでもフォルダでもないため、無視します。elem={}', elem.path);
			}
		}
		return assetMap;
	}
}
