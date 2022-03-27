/*
 * ConvertResults.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo

import groovy.util.logging.Slf4j
import io.github.longfish801.yakumo.YmoConst as cnst

/**
 * 解析結果を管理します。
 * @version 0.3.00 2022/01/10
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ConvertResults {
	/** 変換結果キーと変換結果とのマップ */
	Map map = [:]
	
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
	 * @param clmapName clmap宣言の名前
	 * @see #result(String, Writer, String)
	 */
	void result(String key, File file, String clmapName, String templateKey){
		result(key, new FileWriter(file), clmapName)
	}
	
	/**
	 * 変換結果を追加します。
	 * @param key 変換結果キー
	 * @param writer 出力子
	 * @param clmapName clmap宣言の名前
	 */
	void result(String key, Writer writer, String clmapName){
		map[key] = new Result(key: key, writer: writer, clmapName: clmapName)
	}
	
	/**
	 * テンプレートキーを設定します。<br/>
	 * @param key 変換結果キー
	 * @param templateKey テンプレートキー
	 * @throws YmoConvertException 変換結果が未設定のためテンプレートキーを設定できません。
	 */
	void templateKey(String key, String templateKey){
		if (!map.containsKey(key)) throw new YmoConvertException(String.format(msgs.exc.cannotSetTemplateKey, key, templateKey))
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
		String templateKey = cnst.template.defaultKey
		/** 足跡（整形時に設定） */
		Footprints fprint
	}
}
