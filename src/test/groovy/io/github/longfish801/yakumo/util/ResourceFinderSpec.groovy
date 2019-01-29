/*
 * ResourceFinderSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo.util;

import groovy.util.logging.Slf4j;
import spock.lang.Specification;
import spock.lang.Unroll;

/**
 * ResourceFinderのテスト。
 * @version 1.0.00 2018/07/02
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ResourceFinderSpec extends Specification {
	def 'find'(){
		given:
		ResourceFinder resourceRefer = new ResourceFinder(ResourceFinderSpec.class);
		Map<String, URL> map;
		String commonPath = new File('build/resources/test').canonicalFile.toURI().toURL().toString();
		
		when:
		map = resourceRefer.find('ResourceFinderSpec', [], []);
		then:
		map.sort() == [
			'a.txt': new URL("${commonPath}ResourceFinderSpec/a.txt"),
			'b.txt': new URL("${commonPath}ResourceFinderSpec/b.txt"),
			'c.txt': new URL("${commonPath}io/github/longfish801/yakumo/util/ResourceFinderSpec/c.txt"),
		];
		
		when:
		map = resourceRefer.find('ResourceFinderSpec', [ 'a.*', 'c.txt' ], []);
		then:
		map.sort() == [
			'a.txt': new URL("${commonPath}ResourceFinderSpec/a.txt"),
			'c.txt': new URL("${commonPath}io/github/longfish801/yakumo/util/ResourceFinderSpec/c.txt"),
		];
		
		when:
		map = resourceRefer.find('ResourceFinderSpec', [], [ 'c.*' ]);
		then:
		map.sort() == [
			'a.txt': new URL("${commonPath}ResourceFinderSpec/a.txt"),
			'b.txt': new URL("${commonPath}ResourceFinderSpec/b.txt")
		];
		
		when:
		resourceRefer.find('', [], []);
		then:
		thrown(IllegalArgumentException);
		
		when:
		resourceRefer.find('dummy', null, []);
		then:
		thrown(IllegalArgumentException);
		
		when:
		resourceRefer.find('dummy', [], null);
		then:
		thrown(IllegalArgumentException);
	}
}
