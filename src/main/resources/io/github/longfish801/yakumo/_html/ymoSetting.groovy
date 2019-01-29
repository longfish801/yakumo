/*
 * ymoSetting.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
import io.github.longfish801.shared.ExchangeResource;
import io.github.longfish801.yakumo.YmoScript;
import io.github.longfish801.yakumo.util.ResourceFinder;

yakumo.setting {
	engine.configureClmap(ExchangeResource.url(YmoScript.class, "${convName}/html.tpac"));
	engine.configureTemplate('default', ExchangeResource.url(YmoScript.class, "${convName}/template/default.html"));
	ResourceFinder finder = new ResourceFinder(YmoScript.class);
	assetHandler.gulp(convName, finder.find("${convName}/asset", [], []));
}
