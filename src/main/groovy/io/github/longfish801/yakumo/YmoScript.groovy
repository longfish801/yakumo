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
	/** 解析後処理 */
	Closure afterParseClosure = null;
	/** 適用後処理 */
	Closure afterApplyClosure = null;
	/** ResourceFinder */
	ResourceFinder resourceFinder = new ResourceFinder(YmoScript.class);
	
	/**
	 * 文字列を変換し、結果を返します。
	 * @param convNameTarget 解析に用いる変換名
	 * @param convNameOut 適用に用いる変換名
	 * @param text 変換対象の文字列
	 * @return 変換結果の文字列
	 */
	static String convert(String convNameTarget, String convNameOut, String text){
		ArgmentChecker.checkNotNull('文字列', text);
		Map writables = new YmoScript().script {
			[convNameTarget, convNameOut].each { configure(it) }
			engine.appendTarget('text', text, convNameTarget);
			engine.appendOut('text', '', convNameOut);
		}
		return writables['text'].toString();
	}
	
	/**
	 * 入力ファイルを変換し、出力ファイルに書きこみます。<br>
	 * 出力ファイルの親フォルダを出力フォルダとして、固定ファイルを上書きコピーします。
	 * @param convNameTarget 解析に用いる変換名
	 * @param convNameOut 適用に用いる変換名
	 * @param inFile 入力ファイル
	 * @param outFile 出力ファイル
	 */
	static void convert(String convNameTarget, String convNameOut, File inFile, File outFile){
		String sourceKey = FilenameUtils.getBaseName(inFile.name);
		new YmoScript().script {
			[convNameTarget, convNameOut].each { configure(it) }
			engine.appendTarget(sourceKey, inFile, convNameTarget);
			engine.appendOut(sourceKey, outFile, convNameOut);
			assetHandler.outDir(outFile.canonicalFile.parentFile);
		}
	}
	
	/**
	 * リソース上の変換設定スクリプトを実行します。<br/>
	 * 変換名をリソース名とし、その配下にある変換設定スクリプト（setting.groovy）を実行します。<br/>
	 * 以下の変数をバインドします。
	 * <ul>
	 * <li>yakumo：自インスタンス</li>
	 * <li>convName：変換名</li>
	 * </ul>
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
	 * フォルダ上の変換設定スクリプトを実行します。<br/>
	 * 指定されたフォルダ内の変換設定スクリプト（setting.groovy）を実行します。<br/>
	 * 以下の変数をバインドします。
	 * <ul>
	 * <li>yakumo：自インスタンス</li>
	 * <li>convDir：変換資材格納フォルダ（java.io.File）</li>
	 * </ul>
	 * @param convDir 変換資材格納フォルダ
	 */
	void configure(File convDir){
		ArgmentChecker.checkExistDirectory('変換設定スクリプトを格納したフォルダ', convDir);
		File scritFile = new File(convDir, cnstYmoScript.setting.fileName);
		shell.setVariable('yakumo', this);
		shell.setVariable('convDir', convDir);
		shell.run(scritFile, []);
	}
	
	/**
	 * 変換スクリプトを実行します。<br/>
	 * 以下の変数は必ずバインドします。
	 * <ul>
	 * <li>yakumo：自インスタンス</li>
	 * <li>scriptFile：変換スクリプト（java.io.File）</li>
	 * </ul>
	 * @param scriptFile 変換スクリプト
	 * @param vars バインド変数の変数名と変数値のマップ
	 */
	void run(File scriptFile, Map vars){
		ArgmentChecker.checkExistFile('変換スクリプト', scriptFile);
		ArgmentChecker.checkNotNull('バインド変数のマップ', vars);
		vars['yakumo'] = this;
		vars['scriptFile'] = scriptFile;
		vars.each { String key, def val -> shell.setVariable(key, val)}
		shell.run(scriptFile, []);
	}
	
	/**
	 * 変換資材を設定します。<br/>
	 * 本メソッドに渡したクロージャを実行します。
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
	 * 変換スクリプトを実行します。<br/>
	 * 以下の処理を実行します。
	 * <ol>
	 * <li>本メソッドに渡したクロージャを実行します。</li>
	 * <li>解析工程を実行します。</li>
	 * <li>解析後処理を実行します。</li>
	 * <li>適用工程を実行します。</li>
	 * <li>適用後処理を実行します。</li>
	 * <li>固定ファイルのコピーを実行します。</li>
	 * </ol>
	 * @param closure 変換スクリプトのクロージャ
	 * @return 変換元キーと変換結果とのマップ
	 */
	Map<String, Writable> script(Closure closure){
		ArgmentChecker.checkNotNull('クロージャ', closure);
		closure.delegate = this;
		closure.resolveStrategy = Closure.DELEGATE_FIRST;
		closure();
		engine.parses();
		afterParseClosure?.call();
		Map writables = engine.applys();
		afterApplyClosure?.call();
		assetHandler.copy();
		return writables;
	}
	
	/**
	 * 解析工程の後で実行する処理を設定します。
	 * @param closure 解析後処理のクロージャ
	 */
	void afterParse(Closure closure){
		ArgmentChecker.checkNotNull('解析後処理をするクロージャ', closure);
		closure.delegate = this;
		closure.resolveStrategy = Closure.DELEGATE_FIRST;
		afterParseClosure = closure;
	}
	
	/**
	 * 適用工程の後で実行する処理を設定します。
	 * @param closure 適用後処理のクロージャ
	 */
	void afterApply(Closure closure){
		ArgmentChecker.checkNotNull('適用後処理をするクロージャ', closure);
		closure.delegate = this;
		closure.resolveStrategy = Closure.DELEGATE_FIRST;
		afterApplyClosure = closure;
	}
}
