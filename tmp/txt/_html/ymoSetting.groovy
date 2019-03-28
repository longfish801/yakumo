/*
 * ymoSetting.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
import io.github.longfish801.yakumo.YmoScript;

yakumo.setting {
	println 'BGN ymoSetting';
	engine.configureTemplate('default', new File(convDir, "template/default.html"));
}
