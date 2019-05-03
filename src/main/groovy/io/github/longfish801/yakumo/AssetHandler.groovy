/*
 * AssetHandler.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.ArgmentChecker;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;

/**
 * 固定ファイルを操作します。
 * @version 1.0.00 2018/02/24
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class AssetHandler {
	/** 出力フォルダ */
	File outDir = null;
	/** アセット一覧 */
	Map<String, Map<String, URL>> assets = [:];
	/** コピーモード */
	String copyMode = 'overwrite';
	
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
	Map copyMethods = [
		'overwrite' : { URL url, File file ->
			FileUtils.copyURLToFile(url, file);
		},
		'modified' : { URL url, File file ->
			if (!file.exists() || file.lastModified() < this.uriToFile(url.toURI()).lastModified()){ 
				FileUtils.copyURLToFile(url, file);
			}
		},
		'notexists' : { URL url, File file ->
			if (!file.exists()) FileUtils.copyURLToFile(url, file);
		}
	];
	
	/**
	 * 固定ファイルの出力フォルダを設定します。
	 * @param outDir 
	 */
	void outDir(File outDir){
		ArgmentChecker.checkExistDirectory('固定ファイルの出力フォルダ', outDir);
		this.outDir = outDir;
	}
	
	/**
	 * コピーモードを設定します。
	 * @param copyMode コピーモード
	 */
	void mode(String copyMode){
		ArgmentChecker.checkNotBlank('コピーモード指定文字列', copyMode);
		if (copyMethods[copyMode] != null) this.copyMode = copyMode;
	}
	
	/**
	 * コピー対象とする固定ファイルを一括指定します。<br>
	 * コピー対象はマップで指定します。<br>
	 * キーにはコピー先のパス（出力フォルダからの相対パス）を指定してください。<br>
	 * 値にはコピー元のURLを指定してください。<br>
	 * @param setName 固定ファイルのセット名
	 * @param sources 固定ファイルのセット
	 * @see #remove(String)
	 */
	void gulp(String setName, Map<String, URL> sources){
		ArgmentChecker.checkNotBlank('固定ファイルのセット名', setName);
		assets[setName] = sources;
	}
	
	/**
	 * セット名に対応する固定ファイルをコピー対象から除外します。
	 * @param setName 固定ファイルのセット名
	 * @see #gulp(String,Map)
	 */
	void remove(String setName){
		ArgmentChecker.checkNotBlank('固定ファイルのセット名', setName);
		assets.remove(setName);
	}
	
	/**
	 * 固定ファイルを出力フォルダにコピーします。<br>
	 * 出力フォルダが未指定のときはコピーをしません。<br>
	 * コピー元の固定ファイルはアセット一覧から参照します。<br>
	 */
	void copy(){
		if (assets.size() > 0 && outDir == null){
			LOG.debug('出力先フォルダが未設定のためコピーしません。');
			return;
		}
		LOG.debug('copy assets outDir={}', outDir.path);
		assets.each { String setName, Map<String, URL> assetMap ->
			assetMap.each { String path, URL url ->
				LOG.debug('copy assets setName={} path={}', setName, path);
				copyMethods[copyMode].call(url, new File(outDir, path));
			}
		}
	}
	
	/**
	 * URIインスタンスを Fileインスタンスに変換します。
	 * @param uri URI
	 * @return File
	 */
	protected File uriToFile(URI uri){
		File file = null;
		if (uri.scheme == 'jar'){
			FileSystems.newFileSystem(uri, [:]).withCloseable { FileSystem fileSystem ->
				file = fileSystem.getPath(uri.toURL().path.replaceFirst(/^file\:.+\!(\/.+)$/, '$1')).toFile();
			}
		} else {
			file = Paths.get(uri).toFile();
		}
		return file;
	}
}
