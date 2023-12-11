import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import backend.Workspace
import ui.CodeViewer
import ui.CodeViewerView
import ui.WorkspacesViewerView
import ui.common.AppTheme
import ui.common.Settings
import ui.filetree.FileTree


sealed class BaseView {
    class WorkspacesViewer(val workspaces: MutableList<Workspace>): BaseView()

    class CodeViewer(val workspace: Workspace): BaseView()
}


fun main() = application {
    val workspacesViewer = BaseView.WorkspacesViewer(mutableListOf())

    val baseViewState: MutableState<BaseView> = remember { mutableStateOf(workspacesViewer) }


    Window(
        onCloseRequest = {
            when (baseViewState.value) {
                is BaseView.WorkspacesViewer -> {
                    exitApplication()
                }
                is BaseView.CodeViewer -> {
                    baseViewState.value = workspacesViewer
                }
            }
        },
        title = "Another IDE",
        state = WindowState(width = 1280.dp, height = 768.dp)
    ) {
        DisableSelection {
            MaterialTheme(
                colors = AppTheme.colors.background.material
            ) {
                Surface {
                    when (val view = baseViewState.value) {
                        is BaseView.WorkspacesViewer -> {
                            WorkspacesViewerView(workspacesViewer, baseViewState)
                        }
                        is BaseView.CodeViewer -> {
                            val codeViewer = remember {
                                CodeViewer(
                                    workspace = view.workspace,
                                    editors = view.workspace.editors,
                                    fileTree = FileTree(view.workspace.file, view.workspace.editors),
                                    settings = Settings()
                                )
                            }

                            CodeViewerView(codeViewer)
                        }
                    }
                }
            }
        }
    }
}