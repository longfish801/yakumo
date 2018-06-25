
// 変換スクリプト
configure {
	fileName = 'ymoscript.groovy';	// ファイル名
}

// 事前整形スクリプト
washscr {
	dirName = 'washscr';	// フォルダ名
	includePattern = ['*.tpac'];	// ファイル名のパターン
	excludePattern = [];	// ファイル名の除外パターン
}

// クロージャマップ
clmap {
	dirName = 'clmap';	// フォルダ名
	includePattern = ['*.tpac'];	// ファイル名のパターン
	excludePattern = [];	// ファイル名の除外パターン
}

// テンプレート
template {
	dirName = 'template';	// フォルダ名
	includePattern = [];	// ファイル名のパターン
	excludePattern = [];	// ファイル名の除外パターン
}

// メタ定義
meta {
	dirName = 'meta';	// フォルダ名
	includePattern = ['*.txt'];	// ファイル名のパターン
	excludePattern = [];	// ファイル名の除外パターン
}

// 固定ファイル
asset {
	dirName = 'asset';	// フォルダ名
	includePattern = [];	// ファイル名のパターン
	excludePattern = [];	// ファイル名の除外パターン
}

// 変換対象
target {
	includePattern = ['*.txt'];	// ファイル名のパターン
	excludePattern = [];	// ファイル名の除外パターン
}
