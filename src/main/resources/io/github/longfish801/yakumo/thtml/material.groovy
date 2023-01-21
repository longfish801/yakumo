/*
 * material.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */

load {
	material 'tbase', 'ttext'
}

// HTML形式へ変換するための資材を設定します
material {
	// clmapスクリプトを設定します
	clmap grope("${convName}/htmltextize.tpac")
	clmap grope("${convName}/htmlize.tpac")
	clmap grope("${convName}/thtml.tpac")
	
	// テンプレートを設定します
	template 'default', grope("${convName}/default.html")
}
