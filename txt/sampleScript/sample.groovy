@GrabResolver(name = 'longfish801 github repositry', root = 'https://longfish801.github.io/maven/')
@Grab('io.github.longfish801:yakumo:0.2.00')
@GrabExclude('org.codehaus.groovy:groovy-all')

import io.github.longfish801.yakumo.YmoScript;

try {
	new YmoScript().convert('_bltxt', '_html', new File('sample.txt'), new File('sample.html'));
} catch (exc){
	println "HTMLへの変換に失敗しました。exc=${exc}";
}
