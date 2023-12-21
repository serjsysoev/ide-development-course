package ui.editor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ui.CodeViewer
import ui.Notification
import ui.common.AppTheme
import util.File

class Editors(var codeViewer: CodeViewer? = null) {
    private val selectedEditor = SelectedEditor()

    var editors = mutableStateListOf<Editor>()
        private set

    val active: Editor? get() = selectedEditor.selected

    fun open(file: File) {
        editors.firstOrNull {it.file == file}?.let { it.activate(); return }
        if (java.io.File(file.absolutePath).exists()) {
            val editor = Editor(file, selectedEditor) { close(it) }
            editors.add(editor)
            editor.activate()
        } else {
            codeViewer?.let {
                GlobalScope.launch(Dispatchers.Default) {
                    val notification = Notification("Error. File does not exist.", AppTheme.colors.state.fail)
                    this@Editors.codeViewer?.notification?.value = notification

                    delay(4000)

                    if (this@Editors.codeViewer?.notification?.value == notification) {
                        this@Editors.codeViewer?.notification?.value = null
                    }
                }

            }
        }
    }

    private fun close(editor: Editor) {
        val index = editors.indexOf(editor)
        editors.remove(editor)
        if (editor.isActive) {
            selectedEditor.selected = editors.getOrNull(index.coerceAtMost(editors.lastIndex))
        }
    }
}

class SelectedEditor {
    var selected: Editor? by mutableStateOf(null)
}
