/*
 * ymoSetting.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
import org.apache.commons.io.FilenameUtils;

yakumo.setting {
	resourceFinder.find(convName, ['*.tpac'], []).each { engine.clmapServer.soak(it.value) }
	engine.bindClmap(convName, ['engine': engine]);
	resourceFinder.find("${convName}/template", ['*.txt'], []).each {
		engine.templateHandler.load(FilenameUtils.getBaseName(it.key), it.value)
	}
	assetHandler.gulp(convName, resourceFinder.find("${convName}/asset", [], []));
}
