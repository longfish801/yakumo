/*
 * convert.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */

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
