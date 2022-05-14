/*
 * material.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */

// HTML形式へ変換するための資材を設定します
material {
	// clmapスクリプトを設定します
	clmap grope("${convName}/thtml.tpac")
	
	// クロージャマップに大域変数をバインドします
	clmapProp convName, 'template', 'templateHandler', templateHandler
	
	// テンプレートを設定します
	template 'default', grope("${convName}/default.html")
}
