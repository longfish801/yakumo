/*
 * material.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */

load {
	material 'tbase'
}

// テキスト形式へ変換するための資材を設定します
material {
	// clmapスクリプトを設定します
	clmap grope("${convName}/ttext.tpac")
}
