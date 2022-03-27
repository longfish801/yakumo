/*
 * Footprints.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo

import groovy.text.SimpleTemplateEngine
import io.github.longfish801.yakumo.YmoConst as cnst
import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * 簡単なロガーです。<br/>
 * clmap文書での処理の記録に利用します。
 * @version 0.3.00 2022/01/10
 * @author io.github.longfish801
 */
class Footprints {
	/** 足跡 */
	List prints = []
	/** SimpleDateFormat */
	def dateFormat = new SimpleDateFormat(cnst.footprints.format.timestamp)
	/** ログ文字列のテンプレート */
	def template = new SimpleTemplateEngine().createTemplate(cnst.footprints.format.message)
	/** トレースレベル */
	static final int LEVEL_TRACE = 1
	/** デバッグレベル */
	static final int LEVEL_DEBUG = 2
	/** 情報レベル */
	static final int LEVEL_INFO = 3
	/** 警告レベル */
	static final int LEVEL_WARN = 4
	/** レベル名 */
	static final Map LEVEL_NAMES = [
		(LEVEL_TRACE as int) : 'TRACE',
		(LEVEL_DEBUG as int) : 'DEBUG',
		(LEVEL_INFO as int) : 'INFO',
		(LEVEL_WARN as int) : 'WARN'
	]
	
	/**
	 * トレースレベルの足跡を残します。
	 * @param message メッセージ
	 */
	void trace(String message){
		print(LEVEL_TRACE, message)
	}
	
	/**
	 * デバッグレベルの足跡を残します。
	 * @param message メッセージ
	 */
	void debug(String message){
		print(LEVEL_DEBUG, message)
	}
	
	/**
	 * 情報レベルの足跡を残します。
	 * @param message メッセージ
	 */
	void info(String message){
		print(LEVEL_INFO, message)
	}
	
	/**
	 * 警告レベルの足跡を残します。
	 * @param message メッセージ
	 */
	void warn(String message){
		print(LEVEL_WARN, message)
	}
	
	/**
	 * 足跡を残します。
	 * @param level レベル
	 * @param message メッセージ
	 */
	void print(int level, String message){
		prints << new Footprint(level: level, date: new Date(), message: message)
	}
	
	/**
	 * すべてのレベルについてログ文字列のリストを取得します。
	 * @return ログ文字列のリスト
	 */
	List<String> getLogs(){
		return getLogs(LEVEL_TRACE, LEVEL_DEBUG, LEVEL_INFO, LEVEL_WARN)
	}
	
	/**
	 * 警告レベルのログ文字列のリストを取得します。
	 * @return ログ文字列のリスト
	 */
	List<String> getWarns(){
		return getLogs(LEVEL_WARN)
	}
	
	/**
	 * 指定したレベルについてログ文字列のリストを取得します。
	 * @param levels 取得対象のレベル
	 * @return ログ文字列のリスト
	 */
	List<String> getLogs(int ... levels){
		return prints.findAll { def footprint ->
			levels.contains(footprint.level)
		}.collect { def footprint ->
			template.make([
				timestamp: dateFormat.format(footprint.date),
				level: LEVEL_NAMES[footprint.level],
				messeage: footprint.message
			]).toString()
		}
	}
	
	/**
	 * 足跡。
	 */
	class Footprint {
		/** レベル */
		int level
		/** 記録日時 */
		Date date
		/** メッセージ */
		String message
	}
}
