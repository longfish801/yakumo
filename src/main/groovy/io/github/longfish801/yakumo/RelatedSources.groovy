/*
 * RelatedSources.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo

import groovy.util.logging.Slf4j
import io.github.longfish801.yakumo.YmoMsg as msgs
import org.apache.commons.io.FileUtils

/**
 * 関連ファイルを操作します。
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class RelatedSources {
	/** 出力フォルダ */
	File dir
	/** 関連ファイル一覧 */
	Map<String, Map<String, URL>> relateds = [:]
	/** コピーモード */
	String mode = 'overwrite'
	/**
	 * コピーモードとコピー処理のクロージャとのマップです。<br/>
	 * クロージャは第一引数にコピー元URLを、第二引数にコピー先ファイルを指定します。<br/>
	 * コピーモードに応じて、以下のとおり動作します。</p>
	 * <dl>
	 * <dt>overwrite</dt>
	 *   <dd>常に上書きコピーします。</dd>
	 * <dt>modified</dt>
	 *   <dd>コピー先が存在しないか、コピー元ファイルの更新日時が
	 *   コピー先ファイルより新しいときだけコピーします。</dd>
	 * <dt>notexists</dt>
	 *   <dd>コピー先が存在しないときのみコピーします。</dd>
	 * </dl>
	 */
	static Map copyMethods = [
		'overwrite' : { File srcFile, File destFile ->
			FileUtils.copyFile(srcFile, destFile)
		},
		'modified' : { File srcFile, File destFile ->
			if (!destFile.exists() || destFile.lastModified() < srcFile.lastModified()){ 
				FileUtils.copyFile(srcFile, destFile)
			}
		},
		'notexists' : { File srcFile, File destFile ->
			if (!destFile.exists()) FileUtils.copyFile(srcFile, destFile)
		}
	]
	
	/**
	 * 関連ファイルの出力フォルダを設定します。
	 * @param path 出力フォルダへのパス
	 * @see #outDir(File)
	 */
	void outDir(String path){
		outDir(new File(path))
	}
	
	/**
	 * 関連ファイルの出力フォルダを設定します。
	 * @param dir 出力フォルダ
	 * @throws YmoConvertException 出力フォルダが存在しないかディレクトリではありません。
	 */
	void outDir(File dir){
		if (!dir.exists() || !dir.directory){
			throw new YmoConvertException(String.format(msgs.exc.invalidOutDir, dir.path))
		}
		this.dir = dir
	}
	
	/**
	 * コピーモードを設定します。
	 * @param mode コピーモード
	 * @throws IllegalArgumentException コピーモードが不正です。
	 */
	void copyMode(String mode){
		if (!copyMethods.containsKey(mode)){
			throw new YmoConvertException(String.format(msgs.exc.invalidCopyMode, mode))
		}
		this.mode = mode
	}
	
	/**
	 * 関連ファイルを設定します。
	 * @param setName 関連ファイルのセット名
	 * @param path コピー先のパス（出力フォルダからの相対パス）
	 * @param file コピー元のファイル
	 */
	void source(String setName, String path, File file){
		if (!relateds.containsKey(setName)) relateds[setName] = [:]
		relateds[setName][path] = file
	}
	
	/**
	 * セット名に対応する関連ファイルをすべてコピー対象から除外します。
	 * @param setName 関連ファイルのセット名
	 * @see #gulp(String,Map)
	 */
	void removeSet(String setName){
		relateds.remove(setName)
	}
	
	/**
	 * 関連ファイルを出力フォルダにコピーします。<br/>
	 * 出力フォルダが未指定のときはコピーをしません。<br/>
	 * コピー元の関連ファイルはアセット一覧から参照します。<br/>
	 */
	void copy(){
		if (dir == null) return
		LOG.debug(msgs.logstr.copyRelatedBegin, dir.absolutePath)
		relateds.each { String setName, Map map ->
			map.each { String path, File file ->
				LOG.debug(msgs.logstr.copyRelatedEach, setName, path)
				copyMethods[mode].call(file, new File(dir, path))
			}
		}
	}
}
