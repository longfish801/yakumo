/*
 * ResourceFinder.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo.util;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.lang.ArgmentChecker;
import java.nio.file.Files;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes
import org.apache.commons.io.FilenameUtils;

/**
 * ワイルドカードと名前が一致するリソースを参照します。
 * @version 1.0.00 2018/07/02
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ResourceFinder extends UrlFinder {
	/** リソースの参照に利用するクラス */
	Class clazz = null;
	
	/**
	 * コンストラクタ。
	 * @param clazz リソースの参照に利用するクラス
	 */
	ResourceFinder(Class clazz){
		ArgmentChecker.checkNotNull('clazz', clazz);
		this.clazz = clazz;
	}
	
	/**
	 * リソース名に対応するフォルダ配下のすべてのリソースをパッケージルートおよびパッケージから返します。<br>
	 * フォルダからの相対パスが同じリソースが存在した場合は、パッケージルート配下を優先します。<br>
	 * リソース名に対応するリソースがまったく存在しない場合は空のマップを返します。<br>
	 * リソース名に対応するリソースがファイルだった場合は、そのファイルのリソースのみ返します。<br>
	 * ファイル名のパターンリストについてはクラス{@link CollectFileVisitor}にて利用します。
	 * @param path リソース名
	 * @param includePatterns ファイル名の適合パターンリスト
	 * @param excludePatterns ファイル名の除外パターンリスト
	 * @return 指定したリソースからの相対パスと、リソースを読みこむためのURLとのマップ
	 */
	Map<String, URL> find(String path, List<String> includePatterns, List<String> excludePatterns){
		ArgmentChecker.checkNotBlank('path', path);
		ArgmentChecker.checkNotNull('includePatterns', includePatterns);
		ArgmentChecker.checkNotNull('excludePatterns', excludePatterns);
		
		// 指定Pathインスタンス配下のリソースを探索し、ファイル名がパターンに適合するリソースのマップを返します
		Closure collectResourcesFromPath = { Path curpath ->
			CollectFileVisitor visitor = new CollectFileVisitor(curpath);
			visitor.includePatterns = includePatterns;
			visitor.excludePatterns = excludePatterns;
			Files.walkFileTree(curpath, visitor);
			return visitor.map;
		}
		
		// 指定URIが JARファイルを指すか否かによって Pathインスタンスに変換し、Path配下からリソースを収集します
		Closure collectResources = { URI uri ->
			Map<String, URL> map = [:];
			if (uri.scheme == 'jar'){
				FileSystems.newFileSystem(uri, [:]).withCloseable { FileSystem fileSystem ->
					Path curpath = fileSystem.getPath(uri.toURL().path.replaceFirst(/^file\:.+\!(\/.+)$/, '$1'));
					map = collectResourcesFromPath(curpath);
				}
			} else {
				map = collectResourcesFromPath(Paths.get(uri));
			}
			return map;
		}
		
		// パッケージ配下のリソース名に対応するフォルダからリソースを収集します
		URL url = clazz.getResource(path);
		Map resourceMap = [:];
		if (url != null) resourceMap += collectResources(url.toURI());
		
		// パッケージルート配下のリソース名に対応するフォルダからリソースを収集します
		ClassLoader loader = clazz.classLoader ?: ClassLoader.systemClassLoader;
		url = loader.getResource(path);
		if (url != null) resourceMap += collectResources(url.toURI());
		return resourceMap;
	}
	
	/**
	 * ファイル名がパターンに適合するファイルについて URLをマップに格納するクラスです。<br>
	 * マップのキーは、起点となる Pathからの相対パスです。
	 */
	class CollectFileVisitor extends SimpleFileVisitor<Path> {
		/** 起点となる Path */
		Path rootPath = null;
		/** ファイル名の適合パターンリスト */
		List<String> includePatterns = [];
		/** ファイル名の除外パターンリスト */
		List<String> excludePatterns = [];
		/** リソース名と URLのマップ */
		Map<String, URL> map = [:];
		
		/**
		 * コンストラクタ。
		 * @param rootPath 起点となる Path
		 */
		CollectFileVisitor(Path rootPath){
			ArgmentChecker.checkNotNull('rootPath', rootPath);
			this.rootPath = rootPath;
		}
		
		/**
		 * 探索しているファイルの名前がパターンリストを満たすならばマップに追加します。
		 * @param path 探索しているファイル
		 * @param attrs BasicFileAttributes
		 * @return FileVisitResult
		 * @see TextUtil#wildcardMatch(String,List<String>,List<String>)
		 */
		@Override
		FileVisitResult visitFile(Path path, BasicFileAttributes attrs){
			if (wildcardMatch(path.fileName.toString(), includePatterns, excludePatterns)){
				map[rootPath.relativize(path).toString()] = path.toUri().toURL();
			}
			return super.visitFile(path, attrs);
		}
	}
}
