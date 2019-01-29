/*
 * ymoSetting.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
import io.github.longfish801.shared.ExchangeResource;
import io.github.longfish801.yakumo.YmoScript;
import io.github.longfish801.yakumo.util.FileFinder;

yakumo.setting {
	engine.configureTemplate('default', new File(convDir, "template/target.txt"));
	FileFinder finder = new FileFinder(convDir);
	assetHandler.gulp(convName, finder.find('asset', [], []));
}
