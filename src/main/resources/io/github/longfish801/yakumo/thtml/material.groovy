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
	clmap grope("${convName}/clmap/thtml.tpac")
	clmap grope("${convName}/clmap/crosscut.tpac")
	clmap grope("${convName}/clmap/htmlize.tpac")
	clmap grope("${convName}/clmap/textize.tpac")
	clmap grope("${convName}/clmap/meta.tpac")
	baseClmapName 'thtml'
	prepare 'thtml', '/thtml#prepare'
	
	// テンプレートを設定します
	template 'default', grope("${convName}/template/default.html")
	template 'index', grope("${convName}/template/index.html")
}
