# yakumo

## Overview

Convert text format.  
You can use your original format for the source text.

This is individual development, for self-learning.  
No support such as troubleshooting, answering inquiries, and so on.

## Features

* First, convert text in a original format into a document with a hierarchical structure.  
  Then, convert and output to the desired format, such as HTML.  
  Conversion scripts use the Groovy DSL.
* Sample materials are prepared for converting text to HTML.

Convert text in a original format into a document with a hierarchical structure.

## Sample Code

Here is a sample script which convert from text with yakumo notation to HTML (src/test/groovy/Sample.groovy).

```
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
```

Here is the conversion script running above (src/test/resources/sample/convert.groovy).

```
File inputDir = new File(scriptFile.parentFile, 'target')
File outputDir = new File('build/sample')
File relateDir = new File(scriptFile.parentFile, 'related')

load {
	material 'fyakumo', 'thtml'
}

script {
	doFirst {
		if (!inputDir.exists()){
			throw new IOException("No input directory. path=${inputDir.absolutePath}")
		}
		if (!outputDir.exists() && !outputDir.mkdirs()){
			throw new IOException("Failed to create output directory. path=${outputDir.absolutePath}")
		}
	}
	
	def scanner = new AntBuilder().fileScanner {
		fileset(dir: inputDir.path) { include(name: '*.txt') }
	}
	List keys = []
	for (File file in scanner){
		keys << file.name.take(file.name.lastIndexOf('.'))
	}
	
	targets {
		keys.each { String key ->
			target key, new File(inputDir, "${key}.txt")
		}
	}
	
	results {
		keys.each { String key ->
			result key, new File(outputDir, "${key}.html")
		}
		templateKey 'index', 'index'
	}
	
	doLast {
		fprint.logs.each { println it }
		assert fprint.warns.size() == 0
	}
}

related {
	outDir outputDir
	def scanner = new AntBuilder().fileScanner {
		fileset(dir: relateDir.path) { include(name: '**/*') }
	}
	for (File file in scanner){
		source 'sample', file.absolutePath.substring(relateDir.absolutePath.length() + 1), file
	}
}
```

Convert target is [index.txt](https://github.com/longfish801/yakumo/tree/master/src/test/resources/sample/target/index.txt), [curry.txt](https://github.com/longfish801/yakumo/tree/master/src/test/resources/sample/target/curry.txt).  
Convert result is [index.html](https://github.com/longfish801/yakumo/tree/master/src/test/resources/sample/expected/index.html), [curry.html](https://github.com/longfish801/yakumo/tree/master/src/test/resources/sample/expected/curry.html).

This sample code is executed in the execSamples task, see build.gradle.

## Next Step

Please see the [documents](https://longfish801.github.io/maven/yakumo/) for more detail.
