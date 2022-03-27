/*
 * ConvertScript.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo

import groovy.util.logging.Slf4j

/**
 * 変換スクリプトです。
 * @version 0.3.00 2022/01/10
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ConvertScript {
	/** 変換対象 */
	ConvertTargets targets = new ConvertTargets()
	/** 変換結果 */
	ConvertResults results = new ConvertResults()
	/** 変換前に実行する処理 */
	Closure doFirst
	/** 変換中に実行する処理 */
	Closure doBetween
	/** 変換後に実行する処理 */
	Closure doLast
	
	/**
	 * 変換対象を設定します。<br/>
	 * 本メソッドに渡したクロージャを実行します。<br/>
	 * 委任クラスは {@link ConvertTargets} です。
	 * @param closure 変換対象を設定するクロージャ
	 */
	void targets(Closure closure){
		closure.delegate = targets
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		closure()
	}
	
	/**
	 * 変換結果を設定します。<br/>
	 * 本メソッドに渡したクロージャを実行します。<br/>
	 * 委任クラスは {@link ConvertResults} です。
	 * @param closure 変換結果を設定するクロージャ
	 */
	void results(Closure closure){
		closure.delegate = results
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		closure()
	}
	
	/**
	 * 解析前に実行する処理を設定します。
	 * @param closure 解析前に実行する処理
	 */
	void doFirst(Closure closure){
		this.doFirst = closure
	}
	
	/**
	 * 解析後、整形前に実行する処理を設定します。
	 * @param closure 解析後、整形前に実行する処理
	 */
	void doBetween(Closure closure){
		this.doBetween = closure
	}
	
	/**
	 * 整形および関連ファイルのコピー後に実行する処理を設定します。
	 * @param closure 整形および関連ファイルのコピー後に実行する処理
	 */
	void doLast(Closure closure){
		this.doLast = closure
	}
}
