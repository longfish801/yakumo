
// clmap文書関連
clmap {
	// クロージャパス
	clpath = 'dflt#dflt'
	// 足跡の変数名
	footprint = 'fprint'
}

// 資材スクリプト
material {
	// ファイル名
	fileName = 'material.groovy'
}

// 変換対象
target {
	// switem宣言の名前の基底値
	baseSwitemName = 'fyakumo'
}

// 変換結果
results {
	// clmap宣言の名前の基底値
	baseClmapName = 'thtml'
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
