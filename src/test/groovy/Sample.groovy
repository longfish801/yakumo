import io.github.longfish801.yakumo.Yakumo

try {
	def yakumo = new Yakumo()
	String converted = yakumo.run(new File('src/test/resources/convert.groovy'), null)
	assert converted.normalize() == new File('src/test/resources/result.html').text
	assert yakumo.script.fprint.warns.size() == 0
} catch (exc){
	println "Failed to convert: message=${exc.message}"
	throw exc
}
