package ui.common

import androidx.compose.material.darkColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

object AppTheme {
    object colors {
        object background {
            val backgroundDark: Color = Color(0xFF2B2B2B)
            val backgroundMedium: Color = Color(0xFF3C3F41)
            val backgroundLight: Color = Color(0xFF4E5254)
            val backgroundUltraDark: Color = Color(0xFF181818)
            val backgroundOnHover: Color = Color(0xFF184176)
            val material: androidx.compose.material.Colors = darkColors(
                background = backgroundDark,
                surface = backgroundMedium,
                primary = Color.White
            )
        }

        object font {
            val grayLight: Color = Color(0xFF767676)

        }
    }

    object code {
        val simple: SpanStyle = SpanStyle(Color(0xFFA9B7C6))
        val value: SpanStyle = SpanStyle(Color(0xFF6897BB))
        val keyword: SpanStyle = SpanStyle(Color(0xFFCC7832))
        val punctuation: SpanStyle = SpanStyle(Color(0xFFA1C17E))
        val annotation: SpanStyle = SpanStyle(Color(0xFFBBB529))
        val comment: SpanStyle = SpanStyle(Color(0xFF808080))
    }

    object icons {
        val Delete = Icons.Outlined.Close
    }

    object fontSize {
        val large: TextUnit = 20.sp
        val medium: TextUnit = 14.sp
        val small: TextUnit = 12.sp
    }
}