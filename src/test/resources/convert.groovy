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
}
return writer.toString()
