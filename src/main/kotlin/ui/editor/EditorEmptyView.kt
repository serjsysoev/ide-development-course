package ui.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ui.common.AppTheme
import ui.common.Fonts

@Composable
fun EditorEmptyView() = Box(Modifier.fillMaxSize()) {
    Column(Modifier.align(Alignment.Center)) {
        Icon(
            Icons.Default.Code,
            contentDescription = null,
            tint = AppTheme.colors.font.grayLight,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Text(
            "To view file open it from the file tree",
            color = AppTheme.colors.font.grayLight,
            fontFamily = Fonts.jetbrainsMono(),
            fontWeight = FontWeight.Light,
            fontSize = AppTheme.fontSize.medium,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp)
        )
    }
}