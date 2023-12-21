package ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.CoroutineScope
import ui.common.Settings
import ui.editor.Editors
import ui.filetree.FileTree
import kotlin.coroutines.EmptyCoroutineContext

class CodeViewer(
    val editors: Editors,
    val fileTree: MutableState<FileTree?>,
    val settings: Settings,
    val lifeTime: CoroutineScope = CoroutineScope(EmptyCoroutineContext),
    val notification: MutableState<Notification?> = mutableStateOf(null)
)



data class Notification(val text: String, val color: Color)