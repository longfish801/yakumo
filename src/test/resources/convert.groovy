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
