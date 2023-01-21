/*
 * material.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */

// HTML形式へ変換するための資材を設定します
material {
	// clmapスクリプトを設定します
	clmap grope("${convName}/util.tpac")
	
	// テンプレート利用を大域変数としてバインドします
	clmapProp '/util', 'templateHandler', templateHandler
}
