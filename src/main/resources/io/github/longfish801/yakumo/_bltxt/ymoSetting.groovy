/*
 * ymoSetting.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */

yakumo.setting {
	// 拡張子tpacのファイルすべてを washスクリプトとして読みこみます
	resourceFinder.find(convName, ['*.tpac'], []).each { engine.washServer.soak(it.value) }
}
