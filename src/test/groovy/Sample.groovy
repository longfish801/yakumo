/*
 * Sample.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */

import io.github.longfish801.yakumo.Yakumo

try {
	new Yakumo().run(new File('src/test/resources/sample/convert.groovy'), null)
} catch (exc){
	println "Failed to convert: message=${exc.message}"
	throw exc
}

File outDir = new File('build/sample')
File expectDir = new File('src/test/resources/sample/expected')
assert new File(outDir, 'index.html').text == new File(expectDir, 'index.html').text
assert new File(outDir, 'curry.html').text == new File(expectDir, 'curry.html').text
assert new File(outDir, 'img/curry.png').exists() == true
