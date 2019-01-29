/*
 * ymoSetting.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
import io.github.longfish801.yakumo.YmoScript;
import io.github.longfish801.shared.ExchangeResource;

yakumo.setting {
	engine.configureWashsh(ExchangeResource.url(YmoScript.class, "${convName}/bltxt.tpac"));
}
