/*
 * material.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */

// HTML形式へ変換するための資材を設定します
yakumo.material {
	// clmapスクリプトを設定します
	clmap yakumo.grope("${convName}/thtml.tpac")
	
	// クロージャマップに大域変数をバインドします
	clmapProps convName, "/thtml/template", [ 'templateHandler': templateHandler ]
	
	// テンプレートを設定します
	template 'default', yakumo.grope("${convName}/default.html")
}
