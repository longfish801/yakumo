/*
 * MaterialLoader.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.yakumo

import groovy.util.logging.Slf4j
import io.github.longfish801.gonfig.GropedResource
import io.github.longfish801.yakumo.YmoConst as cnst
import io.github.longfish801.yakumo.YmoMsg as msgs

/**
 * 変換資材を読みこみます。
 * @version 0.3.00 2022/01/10
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class MaterialLoader implements GropedResource {
	/** 自クラス */
	static final Class clazz = MaterialLoader.class
	/** GroovyShell */
	GroovyShell shell = new GroovyShell(MaterialLoader.class.classLoader)
	/** Yakumo */
	Yakumo yakumo
	
	/**
	 * 変換資材を設定します。<br/>
	 * 可変長引数のため、ひとつ以上の変換資材を指定できます。</p>
	 * <ul>
	 * <li>リソースとしての変換資材を指定するときは、
	 * 資材スクリプトの格納フォルダへのリソースパスを指定してください。</li>
	 * <li>ファイルとしての変換資材を指定するときは、
	 * 資材スクリプトの格納フォルダを指定してください。</li>
	 * </ul>
	 * @param materials 変換資材
	 */
	void material(Object... materials){
		for (int idx = 0; idx < materials.size(); idx ++){
			this.material(materials[idx])
		}
	}
	
	/**
	 * 変換資材（リソース）を設定します。
	 * @param convName 資材スクリプトの格納フォルダへのリソースパス
	 * @throws YmoConvertException リソースパスに相当する変換資材がありません。
	 */
	void material(String convName){
		String path = "${convName}/${cnst.setting.fileName}"
		URL url = grope(path)
		if (url == null) throw new YmoConvertException(String.format(msgs.exc.noSuchMaterialResource, path))
		shell.setVariable('yakumo', this.yakumo)
		shell.setVariable('convName', convName)
		shell.run(url.toURI(), [])
	}
	
	/**
	 * 資材スクリプト（ファイル）を設定します。
	 * @param convDir 資材スクリプトの格納フォルダ
	 * @throws YmoConvertException 資材スクリプトの格納フォルダに相当する変換資材がありません。
	 */
	void material(File convDir){
		File file = new File(convDir, cnst.setting.fileName)
		if (!file.canRead()) throw new YmoConvertException(String.format(msgs.exc.noSuchMaterialFile, file.absolutePath))
		shell.setVariable('yakumo', this.yakumo)
		shell.setVariable('convDir', convDir)
		shell.run(file, [])
	}
}
