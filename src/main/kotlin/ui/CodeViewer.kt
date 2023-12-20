package ui

import androidx.compose.runtime.MutableState
import kotlinx.coroutines.CoroutineScope
import ui.common.Settings
import ui.editor.Editors
import ui.filetree.FileTree
import kotlin.coroutines.EmptyCoroutineContext

class CodeViewer(
    val editors: Editors,
    val fileTree: MutableState<FileTree>,
    val settings: Settings,
    val lifeTime: CoroutineScope = CoroutineScope(EmptyCoroutineContext)
)