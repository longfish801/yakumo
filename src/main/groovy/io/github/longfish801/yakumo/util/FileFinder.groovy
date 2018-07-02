/*
 * FileFinder.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo.util;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.lang.ArgmentChecker;

/**
 * ワイルドカードと名前が一致するリソースを参照します。
 * @version 1.0.00 2018/07/02
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class FileFinder extends UrlFinder {
	/** ファイルの参照に利用するルートフォルダ */
	File rootDir = null;
	
	/**
	 * コンストラクタ。
	 * @param rootDir ルートフォルダ
	 */
	FileFinder(File rootDir){
		ArgmentChecker.checkExistDirectory('ルートフォルダ', rootDir);
		this.rootDir = rootDir;
	}
	
	/**
	 * 指定したフォルダ配下から名前がパターンに一致するファイルのマップを返します。<br/>
	 * 対象フォルダが存在しない場合は、空マップを返します。
	 * @param path 対象フォルダ（ルートフォルダからの相対パス）
	 * @param includePatterns ファイル名の適合パターンリスト
	 * @param excludePatterns ファイル名の除外パターンリスト
	 * @return パターンに一致するファイルのマップ（キーは対象フォルダからの相対パス、値はファイルのURL）
	 */
	Map<String, URL> find(String path, List<String> includePatterns, List<String> excludePatterns){
		File targetDir = new File(rootDir, path);
		if (!targetDir.exists()) return [:];
		
		// 指定フォルダ配下のファイルについて、相対パスとファイルのURLとのマップを作成するクロージャです
		// サブフォルダも再帰的に探索します
		Closure collectFiles;
		collectFiles = { Map map, File curDir, String curPath ->
			curDir.listFiles().each { File elem ->
				switch (elem){
					case { it.isFile() }:
						map["${curPath}${elem.name}"] = elem.toURI().toURL();
						break;
					case { it.isDirectory() }:
						collectFiles(map, elem, "${curPath}${elem.name}/");
						break;
					default:
						LOG.debug('ファイルでもフォルダでもないため、無視します。elem={}', elem.path);
				}
			}
		}
		
		// 変換対象フォルダ直下のファイルおよびフォルダについて、コピー対象か確認します
		Map<String, URL> assetMap = [:];
		targetDir.listFiles().each { File elem ->
			switch (elem){
				case { it.isFile() }:
					if (FileFinder.wildcardMatch(elem.name, includePatterns, excludePatterns)){
						assetMap["${elem.name}"] = elem.toURI().toURL();
					}
					break;
				case { it.isDirectory() }:
					if (FileFinder.wildcardMatch(elem.name, includePatterns, excludePatterns)){
						collectFiles(assetMap, elem, "${elem.name}/");
					}
					break;
				default:
					LOG.debug('ファイルでもフォルダでもないため、無視します。elem={}', elem.path);
			}
		}
		return assetMap;
	}
}
