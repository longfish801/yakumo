/*
 * ConvertMaterial.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo

import groovy.util.logging.Slf4j
import groovyx.gpars.GParsPool
import io.github.longfish801.bltxt.BLtxt
import io.github.longfish801.clmap.ClmapClosure
import io.github.longfish801.clmap.Clmap
import io.github.longfish801.clmap.ClmapMap
import io.github.longfish801.clmap.ClmapServer
import io.github.longfish801.switem.Switem
import io.github.longfish801.switem.SwitemServer
import io.github.longfish801.yakumo.YmoConst as cnst
import io.github.longfish801.yakumo.YmoMsg as msgs
import java.lang.Thread as JavaThread

/**
 * 変換資材です。
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ConvertMaterial {
	/** SwitemServer */
	SwitemServer switemServer = new SwitemServer()
	/** switem宣言の名前の基底値 */
	String baseSwitemName
	/** ClmapServer */
	ClmapServer clmapServer = new ClmapServer()
	/** clmap宣言の名前の基底値 */
	String baseClmapName
	/** clmap宣言の名前と事前準備のクロージャのパスとのマップ */
	Map prepareMap = [:]
	/** テンプレート操作 */
	TemplateHandler templateHandler = new TemplateHandler()
	
	/**
	 * switemスクリプトを設定します。
	 * @param script switemスクリプト（File、URL、String、BufferedReaderのいずれか）
	 */
	void switem(def script){
		switemServer.soak(script)
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
	 * clmapスクリプトを設定します。
	 * @param script clmapスクリプト（File、URL、String、BufferedReaderのいずれか）
	 */
	void clmap(def script){
		clmapServer.soak(script)
	}
	
	/**
	 * clmap宣言の名前の基底値を設定します。
	 * @param name clmap宣言の名前の基底値
	 */
	void baseClmapName(String clmapName){
		baseClmapName = clmapName
	}
	
	/**
	 * 事前準備として実行するクロージャのパスを設定します。
	 * @param clmapName clmap宣言の名前
	 * @param clpath 事前準備のクロージャのパス
	 */
	void prepare(String clmapName, String clpath){
		prepareMap[clmapName] = clpath
	}
	
	/**
	 * clmap宣言あるいはクロージャマップに大域変数をバインドします。<br/>
	 * @param clpath バインド対象を指定するクロージャパス
	 * @param varName 大域変数の変数名
	 * @param varVal 大域変数の値
	 * @throws IllegalArgumentException 指定されたクロージャパスに相当するClmap/ClmapMapがありません。
	 */
	void clmapProp(String clpath, String varName, def varVal){
		def clmap = clmapServer.cl(clpath)
		if (clmap == null) throw new IllegalArgumentException(String.format(msgs.exc.noClmap, clpath))
		clmap.properties[varName] = varVal
	}
	
	/**
	 * テンプレートを設定します。
	 * @param key キー
	 * @param source テンプレート（Reader, String, File, URLのいずれか）
	 */
	void template(String key, def source){
		templateHandler.set(key, source)
	}
	
	/**
	 * 複数の変換対象から並列処理で BLtxtインスタンスを生成して返します。
	 * @param script 変換スクリプト
	 * @return 変換対象キーとBLtxtインスタンスとのマップ
	 * @throws YmoConvertException switem宣言の名前に相当するswitemスクリプトがありません。
	 */
	Map parse(ConvertScript script){
		Map bltxtMap = [:].asSynchronized()
		GParsPool.withPool {
			script.targets.map.values().eachParallel { def target ->
				// switemスクリプトを参照します
				String switemName = target.switemName ?: baseSwitemName
				Switem switem = switemServer["switem:${switemName}"]
				if (switem == null) throw new YmoConvertException(String.format(msgs.exc.noSwitem, target.key, switemName))
				
				// 対象のテキストをswitemスクリプトで整形します
				Writer pipedWriter = new PipedWriter()
				PipedReader pipedReader = new PipedReader(pipedWriter)
				Exception switemExc = null
				JavaThread switemThread = Thread.start {
					try {
						switem.run(new BufferedReader(target.reader), new BufferedWriter(pipedWriter))
					} catch (exc){
						switemExc = exc
						pipedWriter.close()
						pipedReader.close()
					}
				}
				
				// 整形されたテキストをbltxt文書とみなして読みこみます
				BLtxt bltxt = new BLtxt(pipedReader)
				// 整形の完了を待機します
				switemThread.join()
				if (switemExc != null) throw switemExc
				// bltxt文書を格納します
				target.bltxt = bltxt.leakedWriter
				bltxtMap[target.key] = bltxt
			}
		}
		return bltxtMap
	}
	
	/**
	 * 変換結果毎に並列処理でclmapのクロージャを呼び、テンプレートを適用して出力します。<br/>
	 * 並列処理の前に、すべてのclmapスクリプトに大域変数として足跡fprintを設定します。<br/>
	 * 変換結果毎に以下の並列処理を実行します。</p>
	 * <ul>
	 * <li>clmapスクリプトのクローンを取得し、大域変数として変換結果キーを設定します。</li>
	 * <li>事前準備のためのクロージャを実行し、戻り値を補足情報とします。</li>
	 * <li>バインド変数を取得するためのクロージャを実行します。</li>
	 * </ul>
	 * <p>事前準備のためのクロージャが存在しない場合は実行しません。
	 * @param script 変換スクリプト
	 * @param bltxtMap 変換対象キーとBLtxtインスタンスとのマップ
	 * @throws IllegalArgumentException clmap宣言の名前に相当するclmapスクリプトがありません。
	 * @throws IllegalArgumentException クロージャパスに相当するクロージャがありません。
	 */
	void format(ConvertScript script, Map bltxtMap){
		// すべてのclmapスクリプトに大域変数として足跡を設定します
		clmapServer.decs.values().each { Clmap clmap ->
			clmap.properties[cnst.clmap.footprint] = script.fprint
		}
		
		// 事前準備のためのクロージャを実行します
		Map appendMapCl = [:]
		prepareMap.each { String clmapName, String clpath ->
			ClmapClosure prepareCl = clmapServer.cl(clpath)
			if (prepareCl == null){
				throw new YmoConvertException(String.format(msgs.exc.noClmapForPrepare, clmapName, clpath))
			}
			try {
				appendMapCl[clmapName] = prepareCl.call(bltxtMap, script)
			} catch (exc){
				throw new YmoConvertException(msgs.exc.errorCallPrepare, exc)
			}
		}
		
		GParsPool.withPool {
			script.results.map.values().eachParallel { def result ->
				// clmapスクリプトを参照し、変換結果毎にクローンを格納します
				String clmapName = result.clmapName ?: baseClmapName
				if (clmapServer["clmap:${clmapName}"] == null){
					throw new IllegalStateException(String.format(msgs.exc.noClmapForResult, result.key, clmapName))
				}
				result.clmap = clmapServer["clmap:${clmapName}"].clone()
				
				// クロージャを実行してバインド変数を取得します
				Map binds
				Map appendMap = (appendMapCl[clmapName] == null)? script.appendMap : appendMapCl[clmapName] + script.appendMap
				try {
					binds = result.clmap.call(result.key, bltxtMap, appendMap)
				} catch (exc){
					throw new YmoConvertException(String.format(msgs.exc.errorCallClmap, result.key), exc)
				}
				
				// バインド変数をテンプレートに適用します
				try {
					templateHandler.apply(result.templateKey, result.writer, binds)
				} catch (exc){
					throw new YmoConvertException(String.format(msgs.exc.failApplyTemplate, result.key, result.templateKey), exc)
				}
			}
		}
	}
}
