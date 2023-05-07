
// clmap文書関連
clmap {
	// 足跡の変数名
	footprint = 'fprint'
}

// 資材スクリプト
material {
	// ファイル名
	fileName = 'material.groovy'
	// 変換資材名の変数名
	convName = 'convName'
	// 資材スクリプトがあるフォルダの変数名
	convDir = 'convDir'
}

// 変換スクリプト
convert {
	// 変換スクリプトファイルの変数名
	scriptFile = 'scriptFile'
}

// 変換結果
results {
	// デフォルトのテンプレートキー
	templateKey = 'default'
}

footprints {
	format {
		// タイムスタンプのフォーマット
		timestamp = 'yyyy/MM/dd HH:mm:ss'
		// ログ文字列のフォーマット
		message = '${timestamp} [${level}] ${messeage}'
	}
}
