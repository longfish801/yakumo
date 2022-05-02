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
	def yakumo = new Yakumo()
	String converted = yakumo.run(new File('src/test/resources/convert.groovy'), null)
	assert converted.normalize() == new File('src/test/resources/result.html').text
	assert yakumo.script.results.target.fprint.warns.size() == 0
} catch (exc){
	println "Failed to convert: message=${exc.message}"
	throw exc
}
```

Here is the conversion script running above (src/test/resources/convert.groovy).

```
load {
	material 'fyakumo', 'thtml'
}

def writer = new StringWriter()
script {
	targets {
		target 'target', new File('src/test/resources/target.txt'), 'fyakumo'
	}
	results {
		result 'target', writer, 'thtml'
	}
}
return writer.toString()
```

Convert target is [target.txt](https://github.com/longfish801/yakumo/tree/master/src/test/resources/target.txt).  
Convert result is [result.html](https://github.com/longfish801/yakumo/tree/master/src/test/resources/result.html).

This sample code is executed in the execSamples task, see build.gradle.

## Next Step

Please see the [documents](https://longfish801.github.io/maven/yakumo/) for more detail.
