@GrabResolver(name = 'longfish801 github repositry', root = 'https://longfish801.github.io/maven/')
@Grab('io.github.longfish801:bltxt:0.2.00')
@GrabExclude('org.codehaus.groovy:groovy-all')

import io.github.longfish801.bltxt.BLtxt;
import io.github.longfish801.bltxt.parser.ParseException;

BLtxt bltxt = null;
try {
	bltxt = new BLtxt(new File('sample.txt'));
	assert bltxt.toXml() == new File('sample.xml').getText('UTF-8');
} catch (ParseException exc){
	println "文法誤りがあるため BLtxt文書を解析できません。exc=${exc}";
}
