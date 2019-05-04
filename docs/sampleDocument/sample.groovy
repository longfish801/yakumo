@GrabResolver(name = 'longfish801 github repositry', root = 'https://longfish801.github.io/maven/')
@Grab('io.github.longfish801:yakumo:0.2.00')
@Grab('org.slf4j:slf4j-api:1.7.25')
@Grab('ch.qos.logback:logback-classic:1.2.3')
@GrabExclude('org.codehaus.groovy:groovy-all')

import io.github.longfish801.yakumo.YmoDocument;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

Logger LOG = LoggerFactory.getLogger("sample");

try {
	new YmoDocument(new File('..')).run(new File('../_html'));
} catch (exc){
	LOG.error('HTMLへの変換に失敗しました', exc);
}
