import io.github.longfish801.yakumo.Yakumo

try {
	def writer = new StringWriter()
	def yakumo = new Yakumo()
	yakumo.load { material 'fyakumo', 'thtml' }
	yakumo.script {
		targets {
			target 'target', new File('src/test/resources/target.txt'), 'fyakumo'
		}
		results {
			result 'target', writer, 'thtml'
		}
	}
	assert writer.toString().normalize() == new File('src/test/resources/result.html').text
	assert yakumo.script.results.target.fprint.warns.size() == 0
} catch (exc){
	println "Failed to convert: message=${exc.message}"
	throw exc
}
