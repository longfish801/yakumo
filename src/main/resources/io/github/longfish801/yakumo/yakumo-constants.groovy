
// clmap文書関連
clmap {
	// クロージャパス
	clpath = 'dflt#dflt'
	// 足跡の変数名
	footprint = 'fprint'
}

// 資材スクリプト
setting {
	fileName = 'material.groovy'	// ファイル名
}

// 変換スクリプト
script {
	// ファイル名
	fileName = 'ymoScript.groovy'
}

// 変換対象
target {
	// ファイル名のパターン
	includePattern = ['*.txt']
	// ファイル名の除外パターン
	excludePattern = []
}

// テンプレート関係
template {
	// デフォルトのテンプレートキー
	defaultKey = 'default'
}

// 関連ファイル
related {
	// ファイル名のパターン
	includePattern = []
	// ファイル名の除外パターン
	excludePattern = ['*.txt', '_*']
}

footprints {
	format {
		// タイムスタンプのフォーマット
		timestamp = 'yyyy/MM/dd HH:mm:ss'
		// ログ文字列のフォーマット
		message = '${timestamp} [${level}] ${messeage}'
	}
}
