/*
 * ConvertEngine.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo;

import groovy.util.logging.Slf4j;
import groovyx.gpars.GParsPool;
import io.github.longfish801.shared.ArgmentChecker;
import io.github.longfish801.shared.ExchangeResource;
import io.github.longfish801.bltxt.BLtxt;
import io.github.longfish801.clmap.Clinfo;
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
	/** ClmapServer */
	ClmapServer clmapServer = new ClmapServer();
	/** ログフォルダ */
	File logDir = null;
	/** 解析対象のリスト */
	List targets = [];
	/** 適用結果のリスト */
	List outs = [];
	/** 解析対象キーと BLtxtインスタンスとのマップ */
	Map bltxtMap = Collections.synchronizedMap([:]);
	/** テンプレート操作 **/
	TemplateHandler templateHandler = new TemplateHandler();
	
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
	 * 解析対象クラス。
	 */
	class ParseTarget {
		/** 解析対象キー */
		String key;
		/** 解析対象 */
		def source;
		/** washshスクリプトの識別キー */
		String washKey;
	}
	
	/**
	 * 適用結果クラス。
	 */
	class ApplyOut {
		/** 出力先キー */
		String key;
		/** 出力先 */
		def out;
		/** clmapスクリプトの識別キー */
		String clmapKey;
		/** clmapスクリプトのコンビキー */
		String combiKey;
	}
	
	/**
	 * 解析対象を追加します。
	 * @param key 解析対象キー
	 * @param source 解析対象
	 * @param convName 変換名
	 */
	void appendTarget(String key, def source, String convName){
		targets << new ParseTarget(key: key, source: source, washKey: "washsh:${convName}");
	}
	
	/**
	 * 適用結果を追加します。
	 * @param key 出力先キー
	 * @param out 出力先
	 * @param convName 変換名
	 */
	void appendOut(String key, def out, String convName){
		outs << new ApplyOut(key: key, out: out, clmapKey: "clmap:${convName}", combiKey: '');
	}
	
	/**
	 * 適用結果を追加します。
	 * @param key 出力先キー
	 * @param out 出力先
	 * @param convName 変換名
	 * @param combiKey クロージャマップのコンビキー
	 * @param binds クロージャのバインド変数マップ
	 */
	void appendOut(String key, def out, String convName, String combiKey){
		outs << new ApplyOut(key: key, out: out, clmapKey: "clmap:${convName}", combiKey: combiKey);
	}
	
	/**
	 * クロージャマップに大域変数をバインドします。
	 * @param convName 変換名
	 * @param binds バインド変数マップ
	 */
	void bindClmap(String convName, Map binds){
		Clmap clmap = clmapServer.getAt("clmap:${convName}");
		if (clmap == null) throw new IllegalStateException("クロージャマップを参照できません。convName=${convName}");
		binds.each { clmap.properties[it.key] = it.value }
	}
	
	/**
	 * 複数の変換対象から並列処理で BLtxtインスタンスを生成しメンバ変数のマップに保持します。
	 * @return 解析対象キーと BLtxtインスタンスとのマップ
	 */
	Map parses(){
		if (targets.empty) throw new IllegalStateException("解析対象のリストが空です。");
		// 変換対象から BLtxtインスタンスを並列処理で生成します
		GParsPool.withPool {
			targets.eachParallel { ParseTarget target ->
				bltxtMap[target.key] = parse(target.key, target.source, target.washKey);
			}
		}
		return bltxtMap;
	}
	
	/**
	 * 変換対象から BLtxtインスタンスを生成してメンバ変数のマップに保持します。<br>
	 * まず変換対象を Washshスクリプトで整形します。<br>
	 * 整形結果を BLtxt文書として構造化します。
	 * @param targetKey 解析対象キー
	 * @param source 変換対象（Reader, String, File, URLのいずれか）
	 * @param washKey washshスクリプトの識別キー
	 * @return BLtxt
	 */
	BLtxt parse(String targetKey, def source, String washKey){
		ArgmentChecker.checkNotBlank('解析対象キー', targetKey);
		ArgmentChecker.checkNotNull('変換対象', source);
		ArgmentChecker.checkNotBlank('washshスクリプトの識別キー', washKey);
		
		// 整形を実行します
		Washsh washsh = washServer.getAt(washKey);
		if (washsh == null) throw new IllegalStateException("washshスクリプトを参照できません。targetKey=${targetKey}, washKey=${washKey}");
		Reader reader = null;
		switch (source){
			case Reader: reader = source; break;
			case String: reader = new StringReader(source); break;
			case File: reader = new FileReader(source); break;
			case URL: reader = new InputStreamReader(source.openStream()); break;
			default: throw new YmoConvertException("変換対象が未対応のクラスです。targetKey=${targetKey}, source=${source.class}");
		}
		Writer pipedWriter = new PipedWriter();
		PipedReader pipedReader = new PipedReader(pipedWriter);
		Exception washExc = null;
		JavaThread washThread = Thread.start {
			try {
				washsh.wash(new BufferedReader(reader), new BufferedWriter(pipedWriter));
			} catch (exc){
				washExc = exc;
				pipedWriter.close();
				pipedReader.close();
				LOG.warn("整形に失敗しました。targetKey={} exc={}", targetKey, exc);
			}
		}
		
		// 構造化します
		BLtxt bltxt = new BLtxt(pipedReader);
		
		// 整形の完了を待機します
		washThread.join();
		if (washExc != null) throw washExc;
		// 整形後のテキストを保存します
		String washedFname = String.format(cnstConvertEngine.logging.washed, targetKey);
		if (logDir != null) new File(logDir, washedFname).text = bltxt.leakedWriter.toString();
		return bltxt;
	}
	
	/**
	 * 適用工程を一括実行します。<br/>
	 * テンプレートには、以下のバインド変数を設定します。</p>
	 * <dl>
	 * <dt>bltxtMap</dt>
	 *   <dd>解析対象キーと BLtxtインスタンスとのマップ。</dd>
	 * <dt>clmap</dt>
	 *   <dd>クロージャマップ。</dd>
	 * <dt>bltxtMap</dt>
	 *   <dd>出力先キー。</dd>
	 * </dl>
	 * @return 出力先キーと Writableとのマップ
	 */
	Map applys(){
		if (outs.empty) throw new IllegalStateException("出力先キーと出力先とのマップが設定されていません。");
		Map writables = Collections.synchronizedMap([:]);
		GParsPool.withPool {
			outs.eachParallel { ApplyOut out ->
				writables[out.key] = apply(out.key, out.out, out.clmapKey, out.combiKey);
			}
		}
		return writables;
	}
	
	/**
	 * 適用工程を実行します。<br/>
	 * クロージャには第一引数として出力先キー、第二引数として出力先を渡します。<br/>
	 * @param outKey 出力先キー
	 * @param out 出力先（Writer, String, Fileのいずれか）
	 * @param clmapKey clmapの識別キー
	 * @param combiKey clmapのコンビキー
	 * @return Writable
	 * @throws IllegalStateException クロージャマップを参照できません。
	 * @throws IllegalStateException クロージャを参照できません。
	 */
	Writable apply(String outKey, def out, String clmapKey, String combiKey){
		Clmap clmap = clmapServer.getAt(clmapKey);
		if (clmap == null) throw new IllegalStateException("クロージャマップを参照できません。outKey=${outKey}, clmapKey=${clmapKey}");
		Clinfo clinfo = clmap.cl(combiKey);
		if (clinfo == null) throw new IllegalStateException("クロージャを参照できません。outKey=${outKey}, clmapKey=${clmapKey}, combiKey=${combiKey}");
		return clinfo.call(outKey, out);
	}
	
	/**
	 * Readerインスタンスを生成します。
	 * @param targetKey 解析対象キー
	 * @param source 変換対象（Reader, String, File, URLのいずれか）
	 * @return Reader
	 */
	private static Reader convertToReader(String targetKey, def source){
		Reader reader = null;
		switch (source){
			case Reader: reader = source; break;
			case String: reader = new StringReader(source); break;
			case File: reader = new FileReader(source); break;
			case URL: reader = new InputStreamReader(source.openStream()); break;
			default: throw new YmoConvertException("変換対象が未対応のクラスです。targetKey=${targetKey}, source=${source.class}");
		}
		return reader;
	}
}
