/*
 * TemplateHandler.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo;

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.ArgmentChecker;
import io.github.longfish801.shared.ExchangeResource;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;

/**
 * テンプレートを操作します。
 * @version 1.0.00 2019/04/24
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class TemplateHandler {
	/** ConfigObject */
	static final ConfigObject cnstTemplateHandler = ExchangeResource.config(TemplateHandler.class);
	/** SimpleTemplateEngine */
	private static final SimpleTemplateEngine templateEngine = new SimpleTemplateEngine();
	/** テンプレートキーとテンプレートのマップ */
	Map<String, Template> templateMap = [:];
	
	/**
	 * テンプレートを読みこみます。
	 * @param templateKey テンプレートキー
	 * @param source テンプレート（Reader, String, File, URLのいずれか）
	 */
	void load(String templateKey, def source){
		ArgmentChecker.checkNotBlank('テンプレートキー', templateKey);
		ArgmentChecker.checkNotNull('出力先', source);
		Reader reader = null;
		switch (source){
			case Reader: reader = source; break;
			case String: reader = new StringReader(source); break;
			case File: reader = new FileReader(source); break;
			case URL: reader = new InputStreamReader(source.openStream()); break;
			default: throw new YmoConvertException("変換対象が未対応のクラスです。templateKey=${templateKey}, source=${source.class}");
		}
		templateMap[templateKey] = templateEngine.createTemplate(reader);
	}
	
	/**
	 * テンプレートを適用します。<br/>
	 * テンプレートは以下の順番で取得を試みます。</p>
	 * <ol>
	 * <li>テンプレートキーと一致するテンプレート。</li>
	 * <li>デフォルトのテンプレートキー（_def）と一致するテンプレート。</li>
	 * </ol>
	 * @param outKey 出力先キー
	 * @param out 出力先（Writer, String, Fileのいずれか）
	 * @param templateKey テンプレートキー
	 * @param binds バインド変数
	 * @return Writable
	 * @throws YmoConvertException 出力先が未対応のクラスです。
	 * @throws YmoConvertException 適用できるテンプレートがありません。
	 */
	Writable apply(String outKey, def out, String templateKey, Map binds){
		ArgmentChecker.checkNotBlank('出力先キー', outKey);
		ArgmentChecker.checkNotNull('出力先', out);
		ArgmentChecker.checkNotBlank('テンプレートキー', templateKey);
		ArgmentChecker.checkNotEmptyMap('バインド変数', binds);
		if (templateMap.empty) throw new IllegalStateException("テンプレートの読込がされていません。outKey=${outKey}");
		
		// 変換先から Writerインスタンスを生成します
		Writer writer = null;
		switch (out){
			case Writer: writer = out; break;
			case String: writer = new StringWriter(); break;
			case File: writer = new FileWriter(out); break;
			default: throw new YmoConvertException("出力先が未対応のクラスです。outKey=${outKey}, out=${out.class}");
		}
		
		// テンプレートを取得します
		Template template = templateMap[templateKey] ?: templateMap[cnstTemplateHandler.template.defaultKey];
		if (template == null) throw new YmoConvertException("適用できるテンプレートがありません。outKey=${outKey}, templateKey=${templateKey}, templateMap=${templateMap.keySet()}");
		
		// 変換元からバインド変数を取得し、テンプレートを適用します
		Writable writable = template.make(binds);
		writable.writeTo(writer);
		return writable;
	}
}
