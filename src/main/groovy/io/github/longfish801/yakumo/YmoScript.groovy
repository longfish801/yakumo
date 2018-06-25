/*
 * YmoScript.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.lang.ArgmentChecker;
import io.github.longfish801.shared.lang.ExistResource;
import io.github.longfish801.shared.util.ClassSlurper;
import org.apache.commons.io.FilenameUtils;

/**
 * DSLに基づき、テキストを変換します。
 * @version 1.0.00 2017/08/07
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class YmoScript {
	/** ConfigObject */
	protected static final ConfigObject constants = ClassSlurper.getConfig(YmoScript.class);
	/** ExistResource */
	static ExistResource existResource = new ExistResource(YmoDocument.class);
	/** GroovyShell */
	static GroovyShell shell = new GroovyShell(YmoScript.class.classLoader);
	/** 変換エンジン **/
	ConvertEngine engine = new ConvertEngine();
	/** 固定ファイル操作 **/
	AssetHandler assetHandler = new AssetHandler();
	/** 前処理 */
	Closure doFirstClosure = null;
	/** 後処理 */
	Closure doLastClosure = null;
	
	/**
	 * 文字列を変換し、結果を返します。
	 * @param conversionNames 変換名リスト
	 * @param text 変換対象の文字列
	 * @return 変換結果の文字列
	 */
	static String convert(List conversionNames, String text){
		ArgmentChecker.checkNotNull('文字列', text);
		Map writables = new YmoScript().script{
			configure(*conversionNames);
			engine.setIO('text', text, '');
		}
		return writables['text'].toString();
	}
	
	/**
	 * 入力ファイルに対して変換し、結果を出力ファイルに書きこみます。<br>
	 * 出力ファイルの親フォルダを出力フォルダとして、固定ファイルを上書きコピーします。
	 * @param conversionNames 変換名リスト
	 * @param inFile 入力ファイル
	 * @param outFile 出力ファイル
	 */
	static void convert(List conversionNames, File inFile, File outFile){
		String sourceKey = FilenameUtils.getBaseName(inFile.name);
		new YmoScript().script {
			configure(*conversionNames);
			engine.setIO('file', inFile, outFile);
			assetHandler.setup(outFile.canonicalFile.parentFile, 'overwrite');
			doLast {
				assetHandler.copy();
			}
		}
	}
	
	/**
	 * 変換スクリプトを実行します。
	 * @param closure 変換処理をするクロージャ
	 * @return 変換元キーと変換結果とのマップ
	 */
	Map<String, Writable> script(Closure closure){
		ArgmentChecker.checkNotNull('クロージャ', closure);
		closure.delegate = this;
		closure.resolveStrategy = Closure.DELEGATE_FIRST;
		closure();
		doFirstClosure?.call();
		engine.loggingCurrentConfig();
		Map writables = engine.convertAll();
		doLastClosure?.call();
		return writables;
	}
	
	/**
	 * 各変換名毎に、各種設定を参照します。<br>
	 * 変換名をリソース名とし、その配下にある事前整形スクリプト、クロージャマップ、テンプレート、メタ定義、固定ファイルを読みこみます。
	 * @param conversionNames 変換名リスト
	 */
	void configure(String... conversionNames){
		ArgmentChecker.checkNotEmptyList('変換名リスト', conversionNames as List);
		(conversionNames as List).each { String conversionName ->
			engine.configureWashscr(existResource.find("${conversionName}/${constants.washscr.dirName}", constants.washscr.includePattern, constants.washscr.excludePattern).values() as List);
			engine.configureClmap(existResource.find("${conversionName}/${constants.clmap.dirName}", constants.clmap.includePattern, constants.clmap.excludePattern).values() as List);
			engine.configureTemplate(existResource.find("${conversionName}/${constants.template.dirName}", constants.template.includePattern, constants.template.excludePattern));
			engine.configureMeta(existResource.find("${conversionName}/${constants.meta.dirName}", constants.meta.includePattern, constants.meta.excludePattern).values() as List);
			assetHandler.gulp(conversionName, existResource.find("${conversionName}/${constants.asset.dirName}", constants.asset.includePattern, constants.asset.excludePattern));
		}
	}
	
	/**
	 * 変換前に必要な前処理を設定します。
	 * @param closure 前処理をするクロージャ
	 */
	void doFirst(Closure closure){
		ArgmentChecker.checkNotNull('前処理をするクロージャ', closure);
		closure.delegate = this;
		closure.resolveStrategy = Closure.DELEGATE_FIRST;
		doFirstClosure = closure;
	}
	
	/**
	 * 変換後に必要な後処理を設定します。
	 * @param closure 後処理をするクロージャ
	 */
	void doLast(Closure closure){
		ArgmentChecker.checkNotNull('後処理をするクロージャ', closure);
		closure.delegate = this;
		closure.resolveStrategy = Closure.DELEGATE_FIRST;
		doLastClosure = closure;
	}
}
