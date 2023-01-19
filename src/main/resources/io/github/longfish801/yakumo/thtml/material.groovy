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
	clmap grope('thtml/htmltextize.tpac')
	clmap grope('thtml/htmlize.tpac')
	clmap grope('thtml/thtml.tpac')
	
	// テンプレートを設定します
	template 'default', grope('thtml/default.html')
}
