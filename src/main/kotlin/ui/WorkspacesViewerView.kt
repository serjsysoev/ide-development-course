package ui

import BaseView
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import backend.Workspace
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import ui.common.AppTheme
import ui.common.Fonts
import util.toProjectFile
import java.io.File

@Composable
fun WorkspacesViewerView(workSpaceViewer: BaseView.WorkspacesViewer, baseView: MutableState<BaseView>) {
    val workspaces = remember { workSpaceViewer.workspaces.toMutableStateList() }

    Box(Modifier.fillMaxSize().background(color = AppTheme.colors.background.backgroundUltraDark)) {

        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center){
            Column(modifier = Modifier.padding(
                vertical = 60.dp,
            )) {

                Box(modifier = Modifier.fillMaxWidth(0.6f), contentAlignment = Alignment.TopStart) {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Workspaces", fontFamily = Fonts.jetbrainsMono(), fontWeight = FontWeight.Bold, fontSize = AppTheme.fontSize.large, softWrap = false)
                            Spacer(modifier = Modifier.padding(end = 15.dp))
                            CreateWorkspaceView(workspaces, workSpaceViewer)
                        }

                        Spacer(modifier = Modifier.height(15.dp))

                        // Workspaces
                        if (workspaces.isEmpty()) {
                            Row (modifier = Modifier.fillMaxSize()) {
                                Text("List of Workspaces is Empty", fontFamily = Fonts.jetbrainsMono(), fontSize = AppTheme.fontSize.medium, fontWeight = FontWeight.Normal, color = AppTheme.colors.font.grayLight)
                            }
                        }
                        else {
                            Row(modifier = Modifier.fillMaxWidth().verticalScroll(ScrollState(0))) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    workspaces.forEachIndexed { index, _ ->
                                        WorkspaceView(baseView, workSpaceViewer, workspaces, index)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WorkspaceView(baseView: MutableState<BaseView>, workSpaceViewer: BaseView.WorkspacesViewer, workspaces: SnapshotStateList<Workspace>, indexOfCurrentWorkspace: Int) {
    val interactionSource = remember { MutableInteractionSource() }
    val workspace = workSpaceViewer.workspaces[indexOfCurrentWorkspace]
    val isHovered = interactionSource.collectIsHoveredAsState()

    val backgroundColor = if (isHovered.value) {
        AppTheme.colors.background.backgroundOnHover
    } else {
        AppTheme.colors.background.backgroundDark
    }

    Row (modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(5.dp))
        .hoverable(interactionSource)
        .clickable {
            baseView.value = BaseView.CodeViewer(workSpaceViewer.workspaces[indexOfCurrentWorkspace])
        }
        .background(backgroundColor),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 7.dp)) {
            Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {

                Text(workspace.name, fontFamily = Fonts.jetbrainsMono(), fontWeight = FontWeight.Bold, fontSize = AppTheme.fontSize.medium, softWrap = false)

                val interactionSourceOfDots = remember { MutableInteractionSource() }
                val isHoveredOfStars = interactionSourceOfDots.collectIsHoveredAsState()

                val backgroundColorOfStars = if (isHoveredOfStars.value) {
                    AppTheme.colors.background.backgroundLight
                } else {
                    Color.Unspecified
                }

                Column(modifier = Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .hoverable(interactionSourceOfDots)
                    .clickable {
                        workspaces.removeAt(indexOfCurrentWorkspace)
                        workSpaceViewer.workspaces.removeAt(indexOfCurrentWorkspace)
                    }
                    .background(backgroundColorOfStars)
                ) {

                    Icon(AppTheme.icons.Delete, "Remove", modifier = Modifier.size(16.dp))
                }
            }
            var absolutePath = workspace.file.absolutePath
            if (absolutePath.length > 60) {
                val index = absolutePath.withIndex().firstOrNull() {
                    it.value == '/' && absolutePath.length - it.index < 60
                }
                absolutePath = if (index == null) absolutePath.takeLast(60) else "..." + absolutePath.substring(index.index)
            }

            Text(absolutePath, fontFamily = Fonts.jetbrainsMono(), fontWeight = FontWeight.ExtraLight, fontSize=AppTheme.fontSize.small, color = AppTheme.colors.font.grayLight, softWrap = false)
        }
    }
}

@Composable
fun CreateWorkspaceView(workspaces: SnapshotStateList<Workspace>, workspaceViewer: BaseView.WorkspacesViewer) {
    val interactionSource = remember { MutableInteractionSource() }

    val isHovered = interactionSource.collectIsHoveredAsState()

    val backgroundColor = if (isHovered.value) {
        AppTheme.colors.background.backgroundLight
    } else {
        AppTheme.colors.background.backgroundDark
    }

    var showDirPicker by remember { mutableStateOf(false) }

    DirectoryPicker(showDirPicker) { path ->
        showDirPicker = false

        if (path != null) {
            val file = File(path)
            val workspace = Workspace(file.name, file.toProjectFile())
            workspaces.add(0, workspace)
            workspaceViewer.workspaces.add(0, workspace)
        }
    }

    Row(modifier = Modifier
        .clip(RoundedCornerShape(5.dp))
        .hoverable(interactionSource)
        .clickable(interactionSource, indication = null) {
            showDirPicker = true
        }
        .background(color = backgroundColor)
        .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            "Add Workspace...",
            fontFamily = Fonts.jetbrainsMono(),
            fontWeight = FontWeight.ExtraLight,
            fontSize = AppTheme.fontSize.small,
            softWrap = false
        )
    }
}