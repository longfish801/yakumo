/*
 * ConvertResults.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo

import groovy.util.logging.Slf4j
import io.github.longfish801.yakumo.YmoConst as cnst
import io.github.longfish801.yakumo.YmoMsg as msgs

/**
 * 変換結果を管理します。
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ConvertResults {
	/** 変換結果キーと変換結果とのマップ */
	Map map = [:]
	/** clmap宣言の名前の基底値 */
	String baseClmapName = cnst.results.baseClmapName
	
	/**
	 * 変換結果キーに対応する変換結果を参照します。
	 * @param key 変換結果キー
	 * @return 変換結果
	 */
	Result getAt(String key){
		return map.get(key)
	}
	
	/**
	 * 変換対象キーに対応する変換対象を参照します。
	 * @param key 変換対象キー
	 * @return 変換対象
	 */
	def propertyMissing(String key){
		return getAt(key)
	}
	
	/**
	 * 変換結果を追加します。
	 * @param key 変換結果キー
	 * @param file 出力ファイル
	 * @see #result(String, Writer, String)
	 */
	void result(String key, File file){
		result(key, new FileWriter(file))
	}
	
	/**
	 * 変換結果を追加します。
	 * @param key 変換結果キー
	 * @param writer 出力子
	 */
	void result(String key, Writer writer){
		map[key] = new Result(key: key, writer: writer)
	}
	
	/**
	 * clmap宣言の名前の基底値を設定します。<br/>
	 * 設定しない場合は "thtml"です。
	 * @param name clmap宣言の名前の基底値
	 */
	void baseClmapName(String clmapName){
		baseClmapName = clmapName
	}
	
	/**
	 * 特定の変換対象キーに対応する変換対象についてclmap宣言の名前を設定します。<br/>
	 * 設定しない場合は基底値が用いられます。
	 * @param key 変換対象キー
	 * @param clmapName clmap宣言の名前
	 * @throws IllegalArgumentException 指定された変換対象キーに相当する変換対象がありません。
	 */
	void clmapName(String key, String clmapName){
		if (!map.containsKey(key)) throw new IllegalArgumentException(String.format(msgs.exc.noResult, key))
		map[key].clmapName = clmapName
	}
	
	/**
	 * テンプレートキーを設定します。<br/>
	 * @param key 変換結果キー
	 * @param templateKey テンプレートキー
	 * @throws YmoConvertException 変換結果が未設定のためテンプレートキーを設定できません。
	 */
	void templateKey(String key, String templateKey){
		if (!map.containsKey(key)) throw new IllegalArgumentException(String.format(msgs.exc.noResult, key))
		map[key].templateKey = templateKey
	}
	
	/**
	 * 変換結果。
	 */
	class Result {
		/** 変換結果キー */
		String key
		/** 出力子 */
		Writer writer
		/** clmap宣言の名前 */
		String clmapName
		/** テンプレートキー */
		String templateKey = cnst.results.templateKey
		/** 足跡（整形時に設定） */
		Footprints fprint
	}
}
