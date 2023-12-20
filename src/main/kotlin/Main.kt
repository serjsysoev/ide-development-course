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
import frontend.Workspace
import io.github.irgaly.kfswatch.KfsDirectoryWatcher
import io.github.irgaly.kfswatch.KfsEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

    val mainView: MutableState<BaseView> = remember { mutableStateOf(workspacesViewer) }

    Window(
        onCloseRequest = {
            when (mainView.value) {
                is BaseView.WorkspacesViewer -> {
                    exitApplication()
                }
                is BaseView.CodeViewer -> {
                    mainView.value = workspacesViewer
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
                    when (val view = mainView.value) {
                        is BaseView.WorkspacesViewer -> {
                            WorkspacesViewerView(workspacesViewer, mainView)
                        }
                        is BaseView.CodeViewer -> {

                            val codeViewer = remember {
                                CodeViewer(
                                    workspace = view.workspace,
                                    editors = view.workspace.editors,
                                    fileTree = mutableStateOf(FileTree(view.workspace.file, view.workspace.editors)),
                                    settings = Settings()
                                )
                            }

                            val fileWatcher = KfsDirectoryWatcher (
                                scope = codeViewer.lifeTime,
                                dispatcher = Dispatchers.IO,
                                rawEventEnabled = false,
                            )
                            runBlocking {
                                fileWatcher.add(view.workspace.file.absolutePath)
                            }

                            runBlocking {
                                codeViewer.lifeTime.launch(Dispatchers.Default) {
                                    fileWatcher.onEventFlow.filter {
                                        it.event in listOf(KfsEvent.Create, KfsEvent.Delete)
                                    }.collect {
                                        codeViewer.fileTree.value =
                                            FileTree(view.workspace.file, view.workspace.editors)
                                    }
                                }
                            }


                            CodeViewerView(codeViewer)
                        }
                    }
                }
            }
        }
    }
}
