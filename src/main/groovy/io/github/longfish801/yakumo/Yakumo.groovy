/*
 * Yakumo.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo

import groovy.util.logging.Slf4j
import io.github.longfish801.gonfig.GropedResource

/**
 * DSLに基づき、テキストを変換します。
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class Yakumo implements GropedResource {
	/** 自クラス */
	static final Class clazz = Yakumo.class
	/** MaterialLoader */
	MaterialLoader loader = new MaterialLoader(this)
	/** ConvertMaterial */
	ConvertMaterial material = new ConvertMaterial()
	/** ConvertScript */
	ConvertScript script = new ConvertScript()
	/** 関連ファイル操作 */
	RelatedSources relatedSources = new RelatedSources()
	
	/**
	 * 変換スクリプトを実行します。<br/>
	 * 本メソッド内で以下の変数をバインドします。
	 * <ul>
	 * <li>scriptFile：変換スクリプトファイル（java.io.File）</li>
	 * </ul>
	 * @param file 変換スクリプトファイル
	 * @param vars バインド変数の変数名と変数値のマップ
	 * @return 変換スクリプトの実行結果
	 */
	def run(File file, Map vars){
		DelegatingScript script = (DelegatingScript) loader.shell.parse(file)
		script.setDelegate(this)
		script.setProperty('scriptFile', file)
		vars?.each { script.setProperty(it.key, it.value) }
		return script.run()
	}
	
	/**
	 * 依存する変換資材を読みこみます。<br/>
	 * 本メソッドに渡したクロージャを実行します。<br/>
	 * 委任クラスは {@link MaterialLoader} です。
	 * @param closure 依存する変換資材を読みこむクロージャ
	 */
	void load(Closure closure){
		closure.delegate = loader
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		closure()
	}
	
	/**
	 * 変換資材を設定します。<br/>
	 * 本メソッドに渡したクロージャを実行します。<br/>
	 * 委任クラスは {@link ConvertMaterial} です。
	 * @param closure 変換資材を設定するクロージャ
	 */
	void material(Closure closure){
		closure.delegate = material
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		closure()
	}
	
	/**
	 * 関連ファイルを設定します。<br/>
	 * 本メソッドに渡したクロージャを実行します。<br/>
	 * 委任クラスは {@link RelatedSources} です。
	 * @param closure 関連ファイルを設定するクロージャ
	 */
	void related(Closure closure){
		closure.delegate = relatedSources
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		closure()
	}
	
	/**
	 * 変換スクリプトを実行します。<br/><br/>
	 * 委任クラスは {@link ConvertScript} です。
	 * 以下の処理を実行します。
	 * <ol>
	 * <li>本メソッドに渡したクロージャを実行します。</li>
	 * <li>解析工程を実行します。</li>
	 * <li>解析後処理を実行します。</li>
	 * <li>適用工程を実行します。</li>
	 * <li>適用後処理を実行します。</li>
	 * <li>関連ファイルのコピーを実行します。</li>
	 * </ol>
	 * @param closure 変換スクリプトのクロージャ
	 */
	void script(Closure closure){
		// 変換スクリプトを実行します
		closure.delegate = script
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		closure()
		// 変換前の処理を実行します
		script.doFirst?.call()
		// 変換対象を解析します
		Map bltxtMap = material.parse(script)
		// 変換中の処理を実行します
		script.doBetween?.call(bltxtMap)
		// 変換結果を生成します
		material.format(script, bltxtMap)
		// 関連ファイルをコピーします
		relatedSources.copy()
		// 変換後の処理を実行します
		script.doLast?.call()
	}
}
