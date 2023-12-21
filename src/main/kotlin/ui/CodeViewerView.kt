package ui

import androidx.compose.animation.core.Spring.StiffnessLow
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ui.common.AppTheme
import ui.common.Fonts
import ui.editor.EditorEmptyView
import ui.editor.EditorTabsView
import ui.editor.EditorView
import ui.filetree.FileTreeView
import ui.filetree.FileTreeViewTabView
import util.SplitterState
import util.VerticalSplittable

@Composable
fun CodeViewerView(model: CodeViewer) {
    val panelState = remember { PanelState() }

    val animatedSize = if (panelState.splitter.isResizing) {
        if (panelState.isExpanded) panelState.expandedSize else panelState.collapsedSize
    } else {
        animateDpAsState(
            if (panelState.isExpanded) panelState.expandedSize else panelState.collapsedSize,
            SpringSpec(stiffness = StiffnessLow)
        ).value
    }
    Box {
        Box {
            VerticalSplittable(
                Modifier.fillMaxSize(),
                panelState.splitter,
                onResize = {
                    panelState.expandedSize =
                        (panelState.expandedSize + it).coerceAtLeast(panelState.expandedSizeMin)
                }
            ) {
                ResizablePanel(Modifier.width(animatedSize).fillMaxHeight(), panelState) {
                    Column {
                        FileTreeViewTabView()
                        FileTreeView(model.fileTree)
                    }
                }

                Box {
                    if (model.editors.active != null) {
                        Column(Modifier.fillMaxSize()) {
                            EditorTabsView(model.editors)
                            Box {
                                EditorView(model.editors.active!!, model.settings, model)
                            }
                        }
                    } else {
                        EditorEmptyView()
                    }
                }
            }
        }

        NotificationView(model.notification)
    }
}

@Composable
fun BoxScope.NotificationView(model: MutableState<Notification?>) {
    val notification = remember { model }

    val notify = notification.value
    if (notify != null) {
        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .padding(40.dp)
                .defaultMinSize(250.dp, 70.dp)
                .clip(RoundedCornerShape(7.dp))
                .border(1.dp, AppTheme.colors.font.grayLight.copy(alpha = 0.6f), RoundedCornerShape(7.dp))
                .background(AppTheme.colors.background.backgroundDark)
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 10.dp),
            ) {
                Row {
                    Icon(
                        AppTheme.icons.Circle,
                        "Notification",
                        modifier = Modifier.size(14.dp),
                        tint = notify.color
                    )

                    Spacer(modifier = Modifier.size(5.dp))

                    Text(
                        notify.text,
                        fontFamily = Fonts.jetbrainsMono(),
                        fontSize = AppTheme.fontSize.small,
                        color = AppTheme.colors.font.mainLight,
                        fontWeight = FontWeight.Light
                    )
                }
            }
        }
    }
}


private class PanelState {
    val collapsedSize = 24.dp
    var expandedSize by mutableStateOf(300.dp)
    val expandedSizeMin = 90.dp
    var isExpanded by mutableStateOf(true)
    val splitter = SplitterState()
}

@Composable
private fun ResizablePanel(
    modifier: Modifier,
    state: PanelState,
    content: @Composable () -> Unit,
) {
    val alpha by animateFloatAsState(if (state.isExpanded) 1f else 0f, SpringSpec(stiffness = StiffnessLow))

    Box(modifier) {
        Box(Modifier.fillMaxSize().graphicsLayer(alpha = alpha)) {
            content()
        }

        Icon(
            if (state.isExpanded) Icons.Default.ArrowBack else Icons.Default.ArrowForward,
            contentDescription = if (state.isExpanded) "Collapse" else "Expand",
            tint = LocalContentColor.current,
            modifier = Modifier
                .padding(top = 4.dp)
                .width(24.dp)
                .clickable {
                    state.isExpanded = !state.isExpanded
                }
                .padding(4.dp)
                .align(Alignment.TopEnd)
        )
    }
}