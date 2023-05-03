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
	clmap grope("${convName}/thtml.tpac")
	clmap grope("${convName}/htmlize.tpac")
	clmap grope("${convName}/textize.tpac")
	clmap grope("${convName}/meta.tpac")
	
	// テンプレートを設定します
	template 'default', grope("${convName}/default.html")
}
