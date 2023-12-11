package ui

import backend.Workspace
import ui.common.Settings
import ui.editor.Editors
import ui.filetree.FileTree

class CodeViewer(
    val workspace: Workspace,
    val editors: Editors,
    val fileTree: FileTree,
    val settings: Settings
)