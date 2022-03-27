/*
 * ConvertTargets.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo

import groovy.util.logging.Slf4j

/**
 * 変換対象を管理します。
 * @version 0.3.00 2022/01/10
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ConvertTargets {
	/** 変換対象キーと変換対象とのマップ */
	Map map = [:]
	
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
	 * @param switemName switem宣言の名前
	 * @see #target(String, Reader, String)
	 */
	void target(String key, String str, String switemName){
		target(key, new StringReader(str), switemName)
	}
	
	/**
	 * 変換対象を追加します。
	 * @param key 変換対象キー
	 * @param file 変換対象ファイル
	 * @param switemName switem宣言の名前
	 * @see #target(String, Reader, String)
	 */
	void target(String key, File file, String switemName){
		target(key, new FileReader(file), switemName)
	}
	
	/**
	 * 変換対象を格納します。
	 * @param key 変換対象キー
	 * @param reader 入力子
	 * @param switemName switem宣言の名前
	 */
	void target(String key, Reader reader, String switemName){
		map[key] = new Target(key: key, reader: reader, switemName: switemName)
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
