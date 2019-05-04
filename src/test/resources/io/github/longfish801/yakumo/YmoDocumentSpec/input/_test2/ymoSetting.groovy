/*
 * ymoSetting.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
import org.apache.commons.io.FilenameUtils;

yakumo.setting {
	// templateフォルダ内にある拡張子htmlのファイルをすべてテンプレートとして読みこみます
	fileFinder.find("${convDir.name}/template", ['*.txt'], []).each {
		engine.templateHandler.load(FilenameUtils.getBaseName(it.key), it.value)
	}
	// assetフォルダ配下を固定ファイルとして読みこみます
	assetHandler.gulp(convDir.name, fileFinder.find("${convDir.name}/asset", [], []));
}
