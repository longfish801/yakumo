/*
 * UrlFinder.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo.util;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.ArgmentChecker;
import org.apache.commons.io.FilenameUtils;

/**
 * ワイルドカードと名前が一致するリソースを参照します。
 * @version 1.0.00 2018/07/02
 * @author io.github.longfish801
 */
@Slf4j('LOG')
abstract class UrlFinder {
	/**
	 * パスで指定された配下からパターンリストと適合する対象をすべて探します。
	 * @param path パス
	 * @param includePatterns 適合パターンリスト
	 * @param excludePatterns 除外パターンリスト
	 * @return 指定したパスからの相対パスと、パターンリストと適合した対象のURLとのマップ
	 */
	abstract Map<String, URL> find(String path, List<String> includePatterns, List<String> excludePatterns);
	
	/**
	 * 指定された文字列が、ワイルドカードを用いたパターンを満たすか判定します。<br>
	 * 以下の条件を両方とも満たすときに trueを返します。</p>
	 * <ul>
	 * <li>パターンリストincludePatternsが空、あるいはパターンのいずれかひとつでも適合する。</li>
	 * <li>パターンリストexcludePatternsが空、あるいはパターンのどれとも適合しない。</li>
	 * </ul>
	 * @param str 文字列
	 * @param includePatterns 適合パターンリスト（ワイルドカードを使用できます）
	 * @param excludePatterns 除外パターンリスト（ワイルドカードを使用できます）
	 * @return 指定された文字列がパターンを満たすか
	 * @see FilenameUtils#wildcardMatch(String,String)
	 */
	static boolean wildcardMatch(String str, List<String> includePatterns, List<String> excludePatterns){
		ArgmentChecker.checkNotNull('str', str);
		ArgmentChecker.checkNotNull('includePatterns', includePatterns);
		ArgmentChecker.checkNotNull('excludePatterns', excludePatterns);
		return ((includePatterns.isEmpty() || includePatterns.any { FilenameUtils.wildcardMatch(str, it) })
			&& (excludePatterns.isEmpty() || excludePatterns.every { !FilenameUtils.wildcardMatch(str, it) }));
	}
}
