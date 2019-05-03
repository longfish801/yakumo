// 変換スクリプト
script {
	fileName = 'ymoScript.groovy';	// ファイル名
}

// 変換対象
target {
	includePattern = ['*.txt'];	// ファイル名のパターン
	excludePattern = [];	// ファイル名の除外パターン
}

// 固定ファイル
asset {
	includePattern = [];	// ファイル名のパターン
	excludePattern = ['*.txt', '_*'];	// ファイル名の除外パターン
}
