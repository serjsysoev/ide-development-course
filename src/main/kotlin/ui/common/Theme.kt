package ui.common

import androidx.compose.material.darkColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

object AppTheme {
    object colors {
        object background {
            val backgroundDark: Color = Color(0xFF2B2B2B)
            val backgroundLight: Color = Color(0xFF4E5254)
            val backgroundUltraDark: Color = Color(0xFF181818)
            val backgroundOnHover: Color = Color(0xFF184176)
            val material: androidx.compose.material.Colors = darkColors(
                background = backgroundUltraDark,
                surface = backgroundUltraDark,
                primary = Color.White,
            )
        }

        object state {
            val warning: Color = Color(0xFFFFB534)
            val fail: Color = Color(0xFFBF3131)
            val success: Color = Color(0xFF4E9F3D)
        }


        object font {
            val grayLight: Color = Color(0xFF767676)
            val mainLight: Color = Color(0xFFD1D1D1)
        }

    }

    object code {
        val selection: Color = Color(0xFF173764)
        val simple: SpanStyle = SpanStyle(Color(0xFFD1D1D1))
        val value: SpanStyle = SpanStyle(Color(0xFFEBC88E))
        val keyword: SpanStyle = SpanStyle(Color(0xFF82D1CD))
        val type: SpanStyle = SpanStyle(Color(0xFF79ADE3))
        val annotation: SpanStyle = SpanStyle(Color(0xFFBBB529))
        val blockBrackets: SpanStyle = SpanStyle(Color(0xFFEBC88E))
        val symbolName: SpanStyle = SpanStyle(Color(0xFFAF9CFF))
    }

    object icons {
        val Delete = Icons.Outlined.Close
        val Circle = Icons.Default.Circle
    }

    object fontSize {
        val large: TextUnit = 20.sp
        val medium: TextUnit = 14.sp
        val small: TextUnit = 12.sp
    }
}