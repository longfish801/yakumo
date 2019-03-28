@GrabResolver(name = 'longfish801 github repositry', root = 'https://longfish801.github.io/maven/')
@Grab('io.github.longfish801:yakumo:0.1.00')
@GrabExclude('org.codehaus.groovy:groovy-all')

import io.github.longfish801.yakumo.YmoScript;
import io.github.longfish801.yakumo.YmoScript.ConvertException;

try {
	YmoScript ymoScript = new YmoScript('_html');
	ymoScript.convert(new File('sample.txt'), new File('sample.html'));
} catch (ConvertException exc){
	println "HTMLへの変換に失敗しました。exc=${exc}";
}
