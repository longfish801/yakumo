load {
	material 'fyakumo', 'thtml'
}

def writer = new StringWriter()
script {
	targets {
		target 'target', new File('src/test/resources/target.txt')
	}
	results {
		result 'target', writer
	}
	doLast {
		fprint.logs.each { println it }
	}
}
return writer.toString()
