/*
 * ConvertEngine.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo;

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import groovy.util.logging.Slf4j;
import groovyx.gpars.GParsPool;
import io.github.longfish801.shared.ArgmentChecker;
import io.github.longfish801.shared.ExchangeResource;
import io.github.longfish801.bltxt.BLtxt;
import io.github.longfish801.clmap.Clmap;
import io.github.longfish801.clmap.ClmapServer;
import io.github.longfish801.washsh.WashServer;
import io.github.longfish801.washsh.Washsh;
import java.lang.Thread as JavaThread;
import org.apache.commons.io.FileUtils;

/**
 * テキストの変換を実行します。
 * @version 1.0.00 2018/02/24
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ConvertEngine {
	/** ConfigObject */
	static final ConfigObject cnstConvertEngine = ExchangeResource.config(ConvertEngine.class);
	/** WashServer */
	WashServer washServer = new WashServer();
	/** washshスクリプト */
	Washsh washsh = null;
	/** ClmapServer */
	ClmapServer clmapServer = new ClmapServer();
	/** クロージャマップ */
	Clmap clmap = null;
	/** テンプレートキーとテンプレートのマップ */
	Map<String, Template> templateMap = [:];
	/** SimpleTemplateEngine */
	private static final SimpleTemplateEngine templateEngine = new SimpleTemplateEngine();
	/** ログフォルダ */
	File logDir = null;
	/** 変換元キーと変換対象とのマップ */
	Map sourceMap = [:];
	/** 出力先キーと出力先とのマップ */
	Map outMap = [:];
	/** 出力先キーとテンプレートキーとのマップ */
	Map templateKeys = [:];
	
	/**
	 * コンストラクタ。<br/>
	 * デフォルトのログフォルダを設定します。
	 * @see #getDefaultLogDir()
	 */
	ConvertEngine(){
		this.logDir = getDefaultLogDir();
	}
	
	/**
	 * デフォルトのログフォルダ返します。<br/>
	 * デフォルトのログフォルダとして、一時フォルダ（システム変数java.io.tmpdir）配下の yakumoサブフォルダを返します。<br/>
	 * ログフォルダは再作成します。処理失敗時は WARNログを、成功時は INFOログを出力します。
	 * @return デフォルトのログフォルダ（ログフォルダの再作成失敗時は null）
	 */
	protected File getDefaultLogDir(){
		File defLogDir = new File(new File(System.getProperty('java.io.tmpdir')), cnstConvertEngine.logging.subpath);
		try {
			if (defLogDir.isDirectory()) FileUtils.deleteDirectory(defLogDir);
		} catch (exc){
			LOG.warn('ログフォルダの削除に失敗しました。path={}', defLogDir.absolutePath, exc);
		}
		if (defLogDir.mkdirs()){
			LOG.info('ログフォルダを作成しました。path={}', defLogDir.absolutePath);
		} else {
			LOG.warn('ログフォルダの作成に失敗しました。path={}', defLogDir.absolutePath);
			defLogDir = null;
		}
		return defLogDir;
	}
	
	/**
	 * washshスクリプトを読みこみます。
	 * @param source washshスクリプト（File、URL、String、BufferedReaderのいずれか）
	 */
	void configureWashsh(def source){
		Washsh soaked = washServer.soak(source).getAt('washsh:');
		washsh = washsh?.blend(soaked) ?: soaked;
	}
	
	/**
	 * クロージャマップを読みこみます。
	 * @param sources クロージャマップ（File、URL、String、BufferedReaderのいずれか）
	 */
	void configureClmap(def source){
		Clmap soaked = clmapServer.soak(source).getAt('clmap:');
		clmap = clmap?.blend(soaked) ?: soaked;
	}
	
	/**
	 * テンプレートを読みこみます。
	 * @param templateKey テンプレートキー
	 * @param source テンプレート対象（Reader, String, File, URLのいずれか）
	 */
	void configureTemplate(String templateKey, def source){
		Reader reader = convertToReader(templateKey, source);
		templateMap[templateKey] = templateEngine.createTemplate(reader);
	}
	
	/**
	 * 変換設定をログフォルダに出力します。
	 */
	void logging(){
		if (logDir != null){
			if (washsh != null) new File(logDir, cnstConvertEngine.logging.washsh).text = washsh.toString();
			if (clmap != null) new File(logDir, cnstConvertEngine.logging.clmap).text = clmap.toString();
		}
	}
	
	/**
	 * 一括変換します。<br/>
	 * テンプレートには、以下のバインド変数を設定します。</p>
	 * <dl>
	 * <dt>bltxtMap</dt>
	 *   <dd>変換元キーと BLtxtインスタンスとのマップ。</dd>
	 * <dt>clmap</dt>
	 *   <dd>クロージャマップ。</dd>
	 * <dt>bltxtMap</dt>
	 *   <dd>出力先キー。</dd>
	 * </dl>
	 * @return 出力先キーと Writableとのマップ
	 */
	Map converts(){
		if (clmap == null) throw new IllegalStateException("クロージャマップが設定されていません。");
		if (sourceMap.empty) throw new IllegalStateException("変換元キーと変換対象とのマップが設定されていません。");
		if (outMap.empty) throw new IllegalStateException("出力先キーと出力先とのマップが設定されていません。");
		
		// 変換設定をログフォルダに出力します
		logging();
		
		// 変換対象から BLtxtインスタンスを生成します
		Map bltxtMap = Collections.synchronizedMap([:]);
		GParsPool.withPool {
			sourceMap.eachParallel { String sourceKey, def target ->
				bltxtMap[sourceKey] = load(sourceKey, target);
			}
		}
		clmap.properties['bltxtMap'] = bltxtMap;
		
		// 警告メッセージを格納できるようリストを定義します
		clmap.properties['warnings'] = [].asSynchronized();
		
		// テンプレートを適用します
		Map writables = Collections.synchronizedMap([:]);
		GParsPool.withPool {
			outMap.eachParallel { String outKey, def out ->
				writables[outKey] = apply(outKey, out, templateKeys[outKey] ?: outKey, [ 'clmap': clmap, 'outKey': outKey ]);
			}
		}
		return writables;
	}
	
	/**
	 * 変換対象から BLtxtインスタンスを生成してメンバ変数のマップに保持します。<br>
	 * まず変換対象を Washshスクリプトで整形します。<br>
	 * 整形結果を BLtxt文書として構造化します。
	 * @param sourceKey 変換元キー
	 * @param source 変換対象（Reader, String, File, URLのいずれか）
	 * @return BLtxt
	 */
	BLtxt load(String sourceKey, def source){
		ArgmentChecker.checkNotBlank('変換元キー', sourceKey);
		ArgmentChecker.checkNotNull('変換対象', source);
		if (washsh == null) throw new IllegalStateException("Washshスクリプトが設定されていません。sourceKey=${sourceKey}");
		
		// 整形を実行します
		Reader reader = convertToReader(sourceKey, source);
		Writer pipedWriter = new PipedWriter();
		PipedReader pipedReader = new PipedReader(pipedWriter);
		Exception washExc = null;
		JavaThread washThread = Thread.start {
			try {
				washsh.wash(new BufferedReader(reader), new BufferedWriter(pipedWriter));
			} catch (exc){
				washExc = exc;
				LOG.warn("整形に失敗しました。sourceKey={} exc={}", sourceKey, exc);
			}
		}
		
		// 構造化します
		BLtxt bltxt = new BLtxt(pipedReader);
		
		// 整形の完了を待機します
		washThread.join();
		if (washExc != null) throw washExc;
		// 整形後のテキストを保存します
		String washedFname = String.format(cnstConvertEngine.logging.washed, sourceKey);
		if (logDir != null) new File(logDir, washedFname).text = bltxt.leakedWriter.toString();
		return bltxt;
	}
	
	/**
	 * テンプレートを適用します。<br/>
	 * テンプレートは以下の順番で取得を試みます。</p>
	 * <ol>
	 * <li>テンプレートキーと一致するテンプレート。</li>
	 * <li>デフォルトのテンプレートキー（default）と一致するテンプレート。</li>
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
		Template template = templateMap[templateKey] ?: templateMap[cnstConvertEngine.template.defaultKey];
		if (template == null) throw new YmoConvertException("適用できるテンプレートがありません。templateKey=${templateKey}, templateMap=${templateMap.keySet()}");
		
		// 変換元からバインド変数を取得し、テンプレートを適用します
		Writable writable = template.make(binds);
		writable.writeTo(writer);
		return writable;
	}
	
	/**
	 * Readerインスタンスを生成します。
	 * @param sourceKey 変換元キー
	 * @param source 変換対象（Reader, String, File, URLのいずれか）
	 * @return Reader
	 */
	private static Reader convertToReader(String sourceKey, def source){
		Reader reader = null;
		switch (source){
			case Reader: reader = source; break;
			case String: reader = new StringReader(source); break;
			case File: reader = new FileReader(source); break;
			case URL: reader = new InputStreamReader(source.openStream()); break;
			default: throw new YmoConvertException("変換対象が未対応のクラスです。sourceKey=${sourceKey}, source=${source.class}");
		}
		return reader;
	}
}
