/*
 * TemplateHandler.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo

import groovy.text.SimpleTemplateEngine
import io.github.longfish801.yakumo.YmoMsg as msgs

/**
 * テンプレートを利用します。<br/>
 * テンプレートの処理には {@link SimpleTemplateEngine}を用います。<br/>
 * あらかじめテンプレート文字列を特定のキーと紐づけて設定しておきます。<br/>
 * テンプレートキーとバインド変数を指定することでテンプレート適用結果を得ることができます。
 * @author io.github.longfish801
 */
class TemplateHandler {
	/** SimpleTemplateEngine */
	SimpleTemplateEngine templateEngine = new SimpleTemplateEngine()
	/** キーとテンプレートのマップ */
	Map map = [:]
	
	/**
	 * テンプレートを設定します。
	 * @param key キー
	 * @param source テンプレート文字列（Reader, String, File, URLのいずれか）
	 */
	void set(String key, def source){
		map[key] = templateEngine.createTemplate(source)
	}
	
	/**
	 * テンプレートを適用し、結果を文字列で返します。
	 * @param key キー
	 * @param binds バインド変数
	 * @return テンプレート適用結果文字列
	 * @see #apply(String,Writer,Map)
	 */
	String apply(String key, Map binds){
		return apply(key, new StringWriter(), binds).toString()
	}
	
	/**
	 * テンプレートを適用します。<br/>
	 * テンプレートは以下の順番で取得を試みます。</p>
	 * <ol>
	 * <li>キーと一致するテンプレート。</li>
	 * <li>デフォルトのキー（default）と一致するテンプレート。</li>
	 * </ol>
	 * @param key キー
	 * @param writer テンプレート適用結果の出力先
	 * @param binds バインド変数
	 * @return テンプレート適用結果
	 * @throws YmoConvertException 適用できるテンプレートがありません。
	 */
	Writer apply(String key, Writer writer, Map binds){
		def template = map[key]
		if (template == null){
			throw new YmoConvertException(String.format(msgs.exc.noTemplate, key, map.keySet()))
		}
		template.make(binds).writeTo(writer)
		return writer
	}
}
