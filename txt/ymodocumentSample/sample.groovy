@GrabResolver(name = 'longfish801 github repositry', root = 'https://longfish801.github.io/maven/')
@Grab('io.github.longfish801:yakumo:0.1.00')
@GrabExclude('org.codehaus.groovy:groovy-all')

import io.github.longfish801.yakumo.YmoDocument;
import io.github.longfish801.yakumo.YmoConvertException;

try {
	new YmoDocument(new File('..').getCanonicalFile()).run('_html');
} catch (YmoConvertException exc){
	println "変換に失敗しました。exc=${exc}";
}
