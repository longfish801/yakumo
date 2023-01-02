/*
 * ConvertTargets.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo

import groovy.util.logging.Slf4j
import io.github.longfish801.yakumo.YmoConst as cnst
import io.github.longfish801.yakumo.YmoMsg as msgs

/**
 * 変換対象を管理します。
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ConvertTargets {
	/** 変換対象キーと変換対象とのマップ */
	Map map = [:]
	/** switem宣言の名前の基底値 */
	String baseSwitemName = cnst.target.baseSwitemName
	
	/**
	 * 変換対象キーに対応する変換対象を参照します。
	 * @param key 変換対象キー
	 * @return 変換対象
	 */
	Target getAt(String key){
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
	 * 変換対象を追加します。
	 * @param key 変換対象キー
	 * @param str 入力子
	 * @see #target(String, Reader, String)
	 */
	void target(String key, String str){
		target(key, new StringReader(str))
	}
	
	/**
	 * 変換対象を追加します。
	 * @param key 変換対象キー
	 * @param file 変換対象ファイル
	 * @see #target(String, Reader, String)
	 */
	void target(String key, File file){
		target(key, new FileReader(file))
	}
	
	/**
	 * 変換対象を格納します。
	 * @param key 変換対象キー
	 * @param reader 入力子
	 */
	void target(String key, Reader reader){
		map[key] = new Target(key: key, reader: reader)
	}
	
	/**
	 * switem宣言の名前の基底値を設定します。<br/>
	 * 設定しない場合は "fyakumo"です。
	 * @param name switem宣言の名前の基底値
	 */
	void baseSwitemName(String switemName){
		baseSwitemName = switemName
	}
	
	/**
	 * 特定の変換対象キーに対応する変換対象についてswitem宣言の名前を設定します。<br/>
	 * 設定しない場合は基底値が用いられます。
	 * @param key 変換対象キー
	 * @param switemName switem宣言の名前
	 * @throws IllegalArgumentException 指定された変換対象キーに相当する変換対象がありません。
	 */
	void switemName(String key, String switemName){
		if (!map.containsKey(key)) throw new IllegalArgumentException(String.format(msgs.exc.noTarget, key))
		map[key].switemName = switemName
	}
	
	/**
	 * 変換対象。
	 */
	class Target {
		/** 変換対象キー */
		String key
		/** 入力子 */
		Reader reader
		/** switem宣言の名前 */
		String switemName
		/** bltxt文書（解析後に設定） */
		StringWriter bltxt
	}
}
