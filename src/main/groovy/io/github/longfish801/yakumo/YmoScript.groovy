/*
 * YmoScript.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.ArgmentChecker;
import io.github.longfish801.shared.ExchangeResource;
import io.github.longfish801.yakumo.util.ResourceFinder;
import org.apache.commons.io.FilenameUtils;

/**
 * DSLに基づき、テキストを変換します。
 * @version 1.0.00 2017/08/07
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class YmoScript {
	/** ConfigObject */
	static final ConfigObject cnstYmoScript = ExchangeResource.config(YmoScript.class);
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
	 * @param convNames 変換名リスト
	 * @param text 変換対象の文字列
	 * @return 変換結果の文字列
	 */
	static String convert(List convNames, String text){
		ArgmentChecker.checkNotNull('文字列', text);
		Map writables = new YmoScript().script {
			convNames.each { configure(it) }
			engine.sourceMap['text'] = text;
			engine.outMap['text'] = '';
		}
		return writables['text'].toString();
	}
	
	/**
	 * 入力ファイルを変換し、出力ファイルに書きこみます。<br>
	 * 出力ファイルの親フォルダを出力フォルダとして、固定ファイルを上書きコピーします。
	 * @param convNames 変換名リスト
	 * @param inFile 入力ファイル
	 * @param outFile 出力ファイル
	 */
	static void convert(List convNames, File inFile, File outFile){
		String sourceKey = FilenameUtils.getBaseName(inFile.name);
		new YmoScript().script {
			convNames.each { configure(it) }
			engine.sourceMap[sourceKey] = inFile;
			engine.outMap[sourceKey] = outFile;
			assetHandler.setup(outFile.canonicalFile.parentFile, 'overwrite');
			doLast {
				assetHandler.copy();
			}
		}
	}
	
	/**
	 * 変換スクリプトを実行します。
	 * @param scriptFile 変換スクリプト
	 * @param vars 変換スクリプト内で使用する変数名と変数値のマップ
	 */
	void run(File scriptFile, Map vars){
		ArgmentChecker.checkExistFile('変換スクリプト', scriptFile);
		ArgmentChecker.checkNotNull('変数名と変数値のマップ', vars);
		shell.setVariable('yakumo', this);
		shell.setVariable('scriptFile', scriptFile);
		vars.each { String key, def val -> shell.setVariable(key, val)}
		shell.run(scriptFile, []);
	}
	
	/**
	 * リソース上の変換設定スクリプトを実行します。<br>
	 * 変換名をリソース名とし、その配下にある変換設定スクリプト（setting.groovy）を実行します。
	 * @param convName 変換名
	 */
	void configure(String convName){
		ArgmentChecker.checkNotBlank('変換名', convName);
		URL scriptURL = ExchangeResource.url(YmoScript.class, "${convName}/${cnstYmoScript.setting.fileName}");
		shell.setVariable('yakumo', this);
		shell.setVariable('convName', convName);
		shell.run(scriptURL.toURI(), []);
	}
	
	/**
	 * フォルダ上の変換設定スクリプトを実行します。<br>
	 * 指定されたフォルダ内の変換設定スクリプト（setting.groovy）を実行します。
	 * @param convDir 変換設定スクリプトを格納したフォルダ
	 */
	void configure(File convDir){
		ArgmentChecker.checkExistDirectory('変換設定スクリプトを格納したフォルダ', convDir);
		File scritFile = new File(convDir, cnstYmoScript.setting.fileName);
		shell.setVariable('yakumo', this);
		shell.setVariable('convDir', convDir);
		shell.run(scritFile, []);
	}
	
	/**
	 * 変換スクリプトを実行します。
	 * @param closure 変換処理をするクロージャ
	 * @return 変換元キーと変換結果とのマップ
	 */
	void setting(Closure closure){
		ArgmentChecker.checkNotNull('クロージャ', closure);
		closure.delegate = this;
		closure.resolveStrategy = Closure.DELEGATE_FIRST;
		closure();
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
		Map writables = engine.converts();
		doLastClosure?.call();
		return writables;
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
