/*
 * main.gvy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.user;

@GrabResolver(name = 'longfish801 github repositry', root = 'https://longfish801.github.io/maven/')
@Grab('io.github.longfish801:yakumo:0.2.00')
@GrabExclude('org.codehaus.groovy:groovy-all')

import io.github.longfish801.yakumo.YmoDocument;

try {
	println 'BGN run';
	new YmoDocument(new File('txt')).run('_html');
	println 'END run';
} catch (exc){
	println '実行時にエラーが発生しました。';
	exc.printStackTrace();
}
