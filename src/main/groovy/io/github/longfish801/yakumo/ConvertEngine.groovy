/*
 * ConvertEngine.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo;

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.lang.ArgmentChecker;
import io.github.longfish801.shared.lang.ExistResource;
import io.github.longfish801.shared.util.ClassConfig;
import io.github.longfish801.yakumo.bltxt.BLtxt;
import io.github.longfish801.yakumo.clmap.Clmap;
import io.github.longfish801.yakumo.washscr.WashScr;
import java.lang.Thread as JavaThread;
import org.apache.commons.io.FilenameUtils;

/**
 * テキストの変換を実行します。
 * @version 1.0.00 2018/02/24
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ConvertEngine {
	/** 設定値 */
	static ClassConfig config = ClassConfig.newInstance(ConvertEngine.class);
	/** ExistResource */
	static ExistResource existResource = new ExistResource(ConvertEngine.class);
	/** WashScr文書 */
	WashScr washscr = null;
	/** クロージャマップ */
	Clmap clmap = null;
	/** テンプレートマップ */
	Map<String, Template> templateMap = [:];
	/** SimpleTemplateEngine */
	private static final SimpleTemplateEngine templateEngine = new SimpleTemplateEngine();
	/** 共通バインド変数 */
	Map commonBindings = [:];
	/** 入力元キー一覧 */
	List sourceKeys = [];
	/** 入力先マップ */
	Map targets = [:];
	/** 出力先マップ */
	Map outs = [:];
	
	/**
	 * 事前整形スクリプトを読みこみます。
	 * @param sources 事前整形スクリプトのリスト
	 */
	void configureWashscr(List<URL> sources){
		sources.each { URL source ->
			WashScr cur = new WashScr(source);
			if (washscr == null){
				washscr = cur;
			} else {
				washscr.blend(cur);
			}
		}
	}
	
	/**
	 * クロージャマップを読みこみます。
	 * @param sources クロージャマップのリスト
	 */
	void configureClmap(List<URL> sources){
		sources.each { URL source ->
			Clmap cur = new Clmap(source);
			if (clmap == null){
				clmap = cur;
			} else {
				clmap.blend(cur);
			}
		}
	}
	
	/**
	 * テンプレートを読みこみます。
	 * @param sources テンプレートファイル名とテンプレートとのマップ
	 */
	void configureTemplate(Map<String, URL> sources){
		sources.each { String fileName, URL source ->
			Reader reader = new InputStreamReader(source.openStream());
			templateMap[ FilenameUtils.getBaseName(fileName) ] = templateEngine.createTemplate(reader);
		}
	}
	
	/**
	 * メタ定義を読みこみます。
	 * @param sources メタ定義のリスト
	 */
	void configureMeta(List<URL> sources){
		sources.each { URL source ->
			Reader reader = new InputStreamReader(source.openStream());
			commonBindings += createBindings('meta', reader);
		}
	}
	
	/**
	 * 読みこんだ内容を記録としてデータフォルダに出力します。
	 */
	void loggingCurrentConfig(){
		config.deleteDataDir();
		config.saveConfig();
		if (washscr != null) new File(config.getDataDir(), config.current.washscr).text = washscr.toString();
		if (clmap != null) new File(config.getDataDir(), config.current.clmap).text = clmap.toString();
		if (!commonBindings.empty) new File(config.getDataDir(), config.current.meta).text = commonBindings.toString();
	}
	
	/**
	 * 変換元と出力先との対応を設定します。
	 * @param sourceKey 変換元キー
	 * @param target 変換元（Reader, String, File, URLのいずれか）
	 * @param out 変換先（Writer, String, Fileのいずれか）
	 */
	void setIO(String sourceKey, def target, def out){
		ArgmentChecker.checkNotBlank('変換元キー', sourceKey);
		ArgmentChecker.checkNotNull('変換元', target);
		ArgmentChecker.checkNotNull('出力先', out);
		sourceKeys << sourceKey;
		targets[sourceKey] = target;
		outs[sourceKey] = out;
	}
	
	/**
	 * 変換します。
	 * @return 変換元キーと変換結果とのマップ
	 * @see #convert(String, def)
	 */
	Map<String, Writable> convertAll(){
		Map writables = [:];
		sourceKeys.each { String sourceKey ->
			writables[sourceKey] = convert(sourceKey, targets[sourceKey], outs[sourceKey]);
		}
		return writables;
	}
	
	/**
	 * 変換します。</p>
	 * <p>テンプレートは以下の順番で取得を試みます。</p>
	 * <ol>
	 * <li>テンプレートファイル名が、変換元キーと一致する。</li>
	 * <li>テンプレートファイル名が、テンプレートキーと一致する。</li>
	 * </ol>
	 * <p>変換元キーに対応する出力ファイルを out.fileMapから参照できれば、ファイル出力をします。<br>
	 * 参照できなければ、out.writablesに変換結果の groovy.lang.Writableインスタンスを変換元キーと関連付けて格納します。
	 * @param sourceKey 変換元キー
	 * @param target 変換元（Reader, String, File, URLのいずれか）
	 * @param out 変換先（Writer, String, Fileのいずれか）
	 * @return 変換結果
	 * @see #createBindings(String, Reader)
	 * @throws YmoConvertException 変換元／変換先が未対応あるいは適用できるテンプレートがありません。
	 */
	protected Writable convert(String sourceKey, def target, def out){
		ArgmentChecker.checkNotBlank('変換元キー', sourceKey);
		
		// 変換元から Readerインスタンスを生成します
		Reader reader = null;
		switch (target){
			case Reader: reader = target; break;
			case String: reader = new StringReader(target); break;
			case File: reader = new FileReader(target); break;
			case URL: reader = new InputStreamReader(target.openStream()); break;
			default: throw new YmoConvertException("変換元が未対応のクラスです。sourceKey=${sourceKey}, target=${target.class}");
		}

		// 変換先から Writerインスタンスを生成します
		Writer writer = null;
		switch (out){
			case Writer: writer = out; break;
			case String: writer = new StringWriter(); break;
			case File: writer = new FileWriter(out); break;
			default: throw new YmoConvertException("変換先が未対応のクラスです。sourceKey=${sourceKey}, out=${out.class}");
		}
		
		// テンプレートを取得します
		Template template = templateMap[sourceKey] ?: templateMap[config.template.defaultKey];
		if (template == null) throw new YmoConvertException("適用できるテンプレートがありません。sourceKey=${sourceKey},templateMap=${templateMap}");
		
		// 変換元からバインド変数を取得し、テンプレートを適用します
		Writable writable = template.make(commonBindings + createBindings(sourceKey, reader))
		writable.writeTo(writer);
		return writable;
	}
	
	/**
	 * 変換対象からバインド変数を生成します。<br>
	 * Washスクリプトで事前整形をします。<br>
	 * 事前整形結果を BLtxt文書として構造化し、クロージャマップでバインド変数へ加工します。
	 * @param sourceKey 変換元キー
	 * @param reader 変換対象
	 * @return バインド変数
	 */
	protected Map createBindings(String sourceKey, Reader reader){
		ArgmentChecker.checkNotBlank('変換元キー', sourceKey);
		ArgmentChecker.checkNotNull('変換対象', reader);
		if (washscr == null) throw new IllegalStateException("WashScr文書が設定されていません。sourceKey=${sourceKey}");
		if (clmap == null) throw new IllegalStateException("クロージャマップが設定されていません。sourceKey=${sourceKey}");
		
		// 事前整形を実行します
		Writer pipedWriter = new PipedWriter();
		PipedReader pipedReader = new PipedReader(pipedWriter);
		Exception washExc = null;
		JavaThread washThread = Thread.start {
			try {
				washscr.wash(new BufferedReader(reader), new BufferedWriter(pipedWriter));
			} catch (exc){
				washExc = exc;
				LOG.warn("事前整形に失敗しました。sourceKey={} exc={}", sourceKey, exc);
			}
		}
		
		// 構造化します
		BLtxt bltxt = new BLtxt(pipedReader);
		
		// 事前整形の完了を待機し、事前整形後のテキストを保存します
		washThread.join();
		if (washExc != null) throw washExc;
		new File(config.getDataDir(), "washed_${sourceKey}.txt").text = bltxt.leakedWriter.toString();
		
		// バインド変数を取得します
		return clmap.cl(config.clmap.combiKey).call(bltxt);
	}
}
