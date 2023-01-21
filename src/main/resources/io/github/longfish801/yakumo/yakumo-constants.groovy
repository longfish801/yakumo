
// clmap文書関連
clmap {
	// 事前準備のためのクロージャパス
	clpathPrepare = '#prepare'
	// テンプレートに適用するバインド変数取得のためのクロージャパス
	clpathBind = '#'
	// 足跡の変数名
	footprint = 'fprint'
	// テンプレート利用の変数名
	templateHandler = 'templateHandler'
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
