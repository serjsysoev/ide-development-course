package frontend

import kotlinx.serialization.Serializable
import ui.editor.Editors
import util.File

@Serializable
data class Workspace(val name: String, val file: File, val editors: Editors = Editors())
