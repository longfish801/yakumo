/*
 * ymoSetting.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
import org.apache.commons.io.FilenameUtils;

yakumo.setting {
	// 拡張子tpacのファイルすべてを clmapスクリプトとして読みこみます
	resourceFinder.find(convName, ['*.tpac'], []).each { engine.clmapServer.soak(it.value) }
	// クロージャマップ内で利用する大域変数をバインドします
	Map binds = [:];
	binds['engine'] = engine;	// ConvertEngineインスタンス
	binds['warnings'] = [].asSynchronized();	// 警告ログ格納用のリスト
	engine.bindClmap(convName, binds);
	// templateフォルダ内にある拡張子htmlのファイルをすべてテンプレートとして読みこみます
	resourceFinder.find("${convName}/template", ['*.html'], []).each {
		engine.templateHandler.load(FilenameUtils.getBaseName(it.key), it.value)
	}
	// assetフォルダ配下を固定ファイルとして読みこみます
	assetHandler.gulp(convName, resourceFinder.find("${convName}/asset", [], []));
}
