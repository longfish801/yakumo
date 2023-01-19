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
import org.codehaus.groovy.control.CompilerConfiguration

/**
 * 変換資材を読みこみます。
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class MaterialLoader implements GropedResource {
	/** 自クラス */
	static final Class clazz = MaterialLoader.class
	/** GroovyShell */
	GroovyShell shell = initShell()
	/** Yakumo */
	Yakumo yakumo
	/**  ロード済み資材 */
	List loaded = []
	
	/**
	 * コンストラクタ。
	 * @param yakumo Yakumo
	 * @return 自インスタンス
	 */
	MaterialLoader(Yakumo yakumo){
		this.yakumo = yakumo
	}
	
	/**
	 * GroovyShellのインスタンスを取得します。<br/>
	 * スクリプト基底クラスとして {@link DelegatingScript} を設定します。
	 * @return GroovyShellのインスタンス
	 */
	GroovyShell initShell(){
		CompilerConfiguration config = new CompilerConfiguration()
		config.setScriptBaseClass(DelegatingScript.class.name)
		return new GroovyShell(MaterialLoader.class.classLoader, new Binding(), config)
	}
	
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
			if (!loaded.contains(materials[idx])){
				this.material(materials[idx])
				loaded << materials[idx]
			}
		}
	}
	
	/**
	 * 変換資材（リソース）を設定します。
	 * @param convName 資材スクリプトの格納フォルダへのリソースパス
	 * @throws YmoConvertException リソースパスに相当する変換資材がありません。
	 */
	void material(String convName){
		String path = "${convName}/${cnst.material.fileName}"
		URL url = grope(path)
		if (url == null) throw new YmoConvertException(String.format(msgs.exc.noSuchMaterialResource, path))
		DelegatingScript script = (DelegatingScript) shell.parse(url.toURI())
		script.setDelegate(this.yakumo)
		script.run()
	}
	
	/**
	 * 資材スクリプト（ファイル）を設定します。
	 * @param convDir 資材スクリプトの格納フォルダ
	 * @throws YmoConvertException 資材スクリプトの格納フォルダに相当する変換資材がありません。
	 */
	void material(File convDir){
		File file = new File(convDir, cnst.material.fileName)
		if (!file.canRead()) throw new YmoConvertException(String.format(msgs.exc.noSuchMaterialFile, file.absolutePath))
		DelegatingScript script = (DelegatingScript) shell.parse(file)
		script.setDelegate(this.yakumo)
		script.setProperty(convDir.name + cnst.material.dirSuffix, convDir)
		script.run()
	}
}
