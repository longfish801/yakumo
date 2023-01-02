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
	/** ClmapServer */
	ClmapServer clmapServer = new ClmapServer()
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
	 * clmapスクリプトを設定します。
	 * @param script clmapスクリプト（File、URL、String、BufferedReaderのいずれか）
	 */
	void clmap(def script){
		clmapServer.soak(script)
	}
	
	/**
	 * clmap宣言に大域変数をバインドします。
	 * @param name clmap宣言の名前
	 * @param varName 大域変数の変数名
	 * @param varVal 大域変数の値
	 * @throws IllegalArgumentException 指定されたクロージャパスに相当するClmap/ClmapMapがありません。
	 */
	void clmapProp(String name, String varName, def varVal){
		clmapProp(name, null, varName, varVal)
	}
	
	/**
	 * clmap宣言あるいはクロージャマップに大域変数をバインドします。<br/>
	 * clmap宣言に設定するときは、clpathLaterには nullを指定してください。
	 * clmap宣言に設定するときのクロージャパスは "/${name}"です。
	 * クロージャマップに設定するときのクロージャパスは "/${name}/${clpathLater}"です。
	 * @param name clmap宣言の名前
	 * @param clpathLater クロージャパスの宣言より後ろ（先頭にスラッシュは不要）
	 * @param varName 大域変数の変数名
	 * @param varVal 大域変数の値
	 * @throws IllegalArgumentException 指定されたクロージャパスに相当するClmap/ClmapMapがありません。
	 */
	void clmapProp(String name, String clpathLater, String varName, def varVal){
		String clpath = (clpathLater == null)? "/${name}" : "/${name}/${clpathLater}"
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
				String switemName = target.switemName ?: script.targets.baseSwitemName
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
		// すべてのclmapスクリプトに大域変数として足跡fprintを設定します
		clmapServer.decs.values().each { Clmap clmap ->
			clmap.properties[cnst.clmap.footprint] = script.fprint
		}
		GParsPool.withPool {
			script.results.map.values().eachParallel { def result ->
				// clmapスクリプトを参照し、そのクローンを取得します
				String clmapName = result.clmapName ?: script.results.baseClmapName
				if (clmapServer["clmap:${clmapName}"] == null) throw new IllegalStateException(String.format(msgs.exc.noClmapForResult, result.key, clmapName))
				result.clmap = clmapServer["clmap:${clmapName}"].clone()
				// 大域変数として変換結果キーを設定します
				result.clmap.properties[cnst.clmap.resultKey] = result.key
			}
		}
		GParsPool.withPool {
			script.results.map.values().eachParallel { def result ->
				// 事前準備のためのクロージャを実行します
				ClmapClosure cl = result.clmap.cl(cnst.clmap.clpathPrepare)
				cl?.call(bltxtMap, script.appendMap)
			}
		}
		GParsPool.withPool {
			script.results.map.values().eachParallel { def result ->
				// 戻り値としてバインド変数を受けとってテンプレートに適用します
				ClmapClosure cl = result.clmap.cl(cnst.clmap.clpathBind)
				if (cl == null) throw new IllegalStateException(String.format(msgs.exc.noClosure, result.key))
				Map binds = cl.call(bltxtMap, script.appendMap)
				templateHandler.apply(result.templateKey, result.writer, binds)
			}
		}
	}
}
