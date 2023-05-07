
// 例外メッセージ
exc {
	noSuchMaterialResource = 'No such material resource, path=%s'
	noSuchMaterialFile = 'No such material file, path=%s'
	noResult = 'No result for the specified result key. key=%s'
	noTarget = 'No target for the specified target key. key=%s'
	noTemplateStr = 'No template string for the specified template key. templateKey=%s'
	noTemplate = 'No template for the specified template key. templateKey=%s, templateKeys=%s'
	invalidCopyMode = 'No support such copy mode. copyMode=%s'
	noClmap = 'No clmap for specified clpath. clpath=%s'
	noClmapForResult = 'No clmap for the specified clmap name. resultKey=%s, clmapName=%s'
	noSwitem = 'No switem scrpit for the specified target key. targetKey=%s, switemName=%s'
	cannotSetTemplateKey = 'Cannot set template key, because no result is set. key=%s, templateKey=%s'
	invalidOutDir = 'No such directory for outputting related files. path=%s'
	noClmapForPrepare = 'No clmap for the specified clmap name. clmapName=%s, clpath=%s'
	errorCallPrepare = 'An error occurred while executing prepare clmap.'
	errorCallClmap = 'An error occurred while executing clmap. resultKey=%s'
	failApplyTemplate = 'Failed to apply template. resultKey=%s templateKey=%s'
}

// ログメッセージ
logstr {
	copyRelatedBegin = 'copy related files. outDir={}'
	copyRelatedEach = 'copy related files. setName={} path={}'
	failedDeleteBltxtDir = 'Failed to delete bltxt directory. path={}'
	failedMakeBltxtDir = 'Failed to make bltxt directory. path={}'
	makedBltxtDir = 'Bltxt directory is maked. path={}'
}
