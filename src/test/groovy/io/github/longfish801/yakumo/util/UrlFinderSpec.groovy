/*
 * UrlFinderSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo.util;

import groovy.util.logging.Slf4j;
import spock.lang.Specification;
import spock.lang.Unroll;

/**
 * UrlFinderのテスト。
 * @version 1.0.00 2018/07/02
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class UrlFinderSpec extends Specification {
	@Unroll
	def '指定された文字列が、ワイルドカードを用いたパターンを満たすか判定します'(){
		expect:
		UrlFinder.wildcardMatch(str, includePatterns, excludePatterns) == expect;
		
		where:
		str				| includePatterns		| excludePatterns		|| expect
		'sample.txt'	| []					| []					|| true
		'sample.txt'	| [ '*.txt' ]			| []					|| true
		'sample.txt'	| [ '*.txt', '*.xml' ]	| []					|| true
		'sample.txt'	| [ '*.xml' ]			| []					|| false
		'sample.txt'	| [ ]					| [ '*.txt' ]			|| false
		'sample.txt'	| [ ]					| [ '*.txt', '*.xml' ]	|| false
		'sample.txt'	| [ ]					| [ '*.xml' ]			|| true
		'sample.txt'	| [ '*.txt' ]			| [ '*.xml' ]			|| true
		'sample.txt'	| [ '*.txt', '*.xml' ]	| [ 'sample.*' ]		|| false
		'sample.txt'	| [ 'sample.*' ]		| [ '*.txt' ]			|| false
		'sample.txt'	| [ 'sample.*' ]		| [ '*.txt', '*.xml' ]	|| false
	}
}
