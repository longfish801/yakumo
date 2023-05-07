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
	 */
	void target(String key, String str){
		map[key] = new Target(key: key, source: str)
	}
	
	/**
	 * 変換対象を追加します。
	 * @param key 変換対象キー
	 * @param file 変換対象ファイル
	 */
	void target(String key, File file){
		map[key] = new Target(key: key, source: file)
	}
	
	/**
	 * 変換対象を格納します。
	 * @param key 変換対象キー
	 * @param reader 入力子
	 */
	void target(String key, Reader reader){
		map[key] = new Target(key: key, source: reader)
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
		def source
		/** switem宣言の名前 */
		String switemName
		/** bltxt文書（解析後に設定） */
		StringWriter bltxt
		
		/**
		 * 入力元となる Readerインスタンスを返します。<br/>
		 * @return Reader
		 */
		Reader getReader(){
			Reader reader
			switch (source){
				case String:
					reader = new StringReader(source)
					break
				case File:
					reader = new FileReader(source)
					break
				case Reader:
					reader = source
					break
			}
			return reader
		}
	}
}
