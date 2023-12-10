package ui.editor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.v2.ScrollbarAdapter
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.platform.FontLoadResult
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Font
import ui.common.AppTheme
import ui.common.FontSettings
import ui.common.Settings
import util.rope.*
import java.util.logging.Logger
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

const val GUTTER_TEXT_OFFSET = 5
const val GUTTER_SIZE = GUTTER_TEXT_OFFSET + 2
const val EDITOR_TEXT_OFFSET = GUTTER_SIZE + 2
const val TOP_MARGIN = 0.5f

object EditorView {
    val log: Logger = Logger.getLogger(EditorView::class.java.name)
}

internal data class EditorState(
    val verticalScrollOffset: MutableState<Float>,
    val horizontalScrollOffset: MutableState<Float>,
    val canvasSize: MutableState<IntSize>,
    val cursorPosition: MutableState<CursorPosition>,
    val isCursorVisible: MutableState<Pair<Boolean, Int>>,
    val rope: MutableState<Rope<LineMetrics>>, // TODO: undo
    val selectionStart: MutableState<CodePosition?>,
    val textSize: Size,
)

@Composable
fun BoxScope.EditorView(model: Editor, settings: Settings) = key(model) {
    val textMeasurer = rememberTextMeasurer()
    val fontFamilyResolver = LocalFontFamilyResolver.current

    val editorState = EditorState(
        verticalScrollOffset = remember { mutableStateOf(0f) },
        horizontalScrollOffset = remember { mutableStateOf(0f) },
        canvasSize = remember { mutableStateOf(IntSize.Zero) },
        cursorPosition = remember { mutableStateOf(CursorPosition(CodePosition(0, 0), 0)) },
        isCursorVisible = remember { mutableStateOf(true to 0) },
        rope = remember { mutableStateOf(model.rope) },
        selectionStart = remember { mutableStateOf(null) },
        textSize = remember(settings.fontSettings) {
            getTextSize(fontFamilyResolver, textMeasurer, settings.fontSettings)
        }
    )


    val verticalScrollState = editorState.initVerticalScrollState()
    val horizontalScrollState = editorState.initHorizontalScrollState()

    val renderedText = remember { mutableStateOf<RenderedText?>(null) }
    val previousRope = remember { mutableStateOf<Rope<LineMetrics>?>(null) }
    LaunchedEffect(
        editorState.verticalScrollOffset.value,
        editorState.canvasSize.value,
        editorState.rope.value,
        editorState.textSize,
        settings.fontSettings
    ) {
        editorState.rerenderText(renderedText, settings.fontSettings, textMeasurer, previousRope)
    }

    LaunchedEffect(editorState.isCursorVisible.value) { // TODO: documentation explicitly tells not to do that
        val time = editorState.isCursorVisible.value.second
        delay(500)
        editorState.isCursorVisible.value.takeIf { it.second == time }?.let {
            editorState.isCursorVisible.value = !it.first to it.second + 1
        }
    }

    val requester = remember { FocusRequester() }

    Canvas(Modifier
        .fillMaxSize()
        .clipToBounds()
        .focusRequester(requester)
        .focusable()
        .onSizeChanged { editorState.canvasSize.value = it }
        .keyboardInput(editorState, LocalClipboardManager.current)
        .pointerInput(editorState)
        .scrollable(verticalScrollState, Orientation.Vertical)
        .scrollable(horizontalScrollState, Orientation.Horizontal)
    ) {
        val verticalOffset = editorState.verticalScrollOffset.value
        val textSize = editorState.textSize

        drawRect(AppTheme.colors.background.material.background, size = this.size)

        drawSelection(editorState)

        renderedText.value?.let {
            drawText(
                it.textLayoutResult,
                topLeft = editorState.codeToViewport(CodePosition(0, it.from))
            )
        }

        if (editorState.isCursorVisible.value.first) {
            val cursorPosition = editorState.cursorPosition.value
            drawRect(
                color = AppTheme.code.simple.color,
                topLeft = editorState.codeToViewport(cursorPosition.codePosition),
                size = Size(1f, textSize.height),
            )
        }

        drawGutter(settings.fontSettings, textSize, verticalOffset, textMeasurer, editorState.rope.value.lineCount)
    }

    LaunchedEffect(Unit) {
        requester.requestFocus() // TODO: focus system?
    }
    VerticalScrollbar(editorState)
    HorizontalScrollbar(editorState)
}

private fun DrawScope.drawSelection(editorState: EditorState) {
    val (startPosition, endPosition) = editorState.getSelection() ?: return
    val textSize = editorState.textSize

    if (startPosition.y == endPosition.y) {
        drawRect(
            topLeft = editorState.codeToViewport(startPosition),
            size = Size((endPosition.x - startPosition.x) * textSize.width, textSize.height),
            color = Color.Blue,
            alpha = 0.3f
        )
    } else {
        drawRect(
            topLeft = editorState.codeToViewport(startPosition),
            size = Size(size.width, textSize.height),
            color = Color.Blue,
            alpha = 0.3f
        )
        for (lineNumber in startPosition.y + 1 until endPosition.y) {
            drawRect(
                topLeft = editorState.codeToViewport(CodePosition(0, lineNumber)),
                size = Size(size.width, textSize.height),
                color = Color.Blue,
                alpha = 0.3f
            )

        }
        val (x, y) = editorState.codeToViewport(endPosition)
        drawRect(
            topLeft = Offset(EDITOR_TEXT_OFFSET * textSize.width, y),
            size = Size(x - EDITOR_TEXT_OFFSET * textSize.width, textSize.height),
            color = Color.Blue,
            alpha = 0.3f
        )
    }
}

internal fun EditorState.getSelection(): Pair<CodePosition, CodePosition>? {
    val selectionStart = selectionStart.value
    val cursorPosition = cursorPosition.value.codePosition

    return if (selectionStart != null && selectionStart != cursorPosition) {
        val startPosition = minOf(selectionStart, cursorPosition, compareBy({ it.y }, { it.x }))
        val endPosition = if (startPosition != selectionStart) selectionStart else cursorPosition
        startPosition to endPosition
    } else null
}

/**
 * FIXME!!
 * This is a giant hack and I have no clue how to fix it.
 * The problem is that textMeasurer.measure("x").width (or .multiParagraph.width) is not correct.
 * Maybe it accounts for kerning differently, maybe it calculates something different, I don't know.
 * I found out that skia counts width correctly. But the height calculated by skia is not correct!
 * It has multiple ways of getting height, e.g. Font.measure("x").height / Font.metrics.height / Font.metrics.leading,
 * but none of them are correct.
 * That is why this atrocious piece of code calculates width using skia and height using compose.
 * This will only be recalculated on font size change, so it should not impact performance.
 */
private fun getTextSize(
    fontFamilyResolver: FontFamily.Resolver,
    textMeasurer: TextMeasurer,
    settings: FontSettings
): Size {
    val fontLoadResult = fontFamilyResolver.resolve(settings.fontFamily).value as FontLoadResult
    val typeface = fontLoadResult.typeface
    val width = Font(typeface, settings.fontSize.value).measureTextWidth("x") * 2
    val height = textMeasurer.measure("x", style = getTextStyle(settings)).multiParagraph.height
    return Size(width, height)
}

private fun EditorState.codeToViewport(codePosition: CodePosition): Offset = Offset(
    (codePosition.x + EDITOR_TEXT_OFFSET) * textSize.width - horizontalScrollOffset.value,
    (codePosition.y + TOP_MARGIN) * textSize.height - verticalScrollOffset.value
)

internal fun EditorState.viewportToCode(offset: Offset): CodePosition = CodePosition(
    ((offset.x + horizontalScrollOffset.value) / textSize.width - EDITOR_TEXT_OFFSET).roundToInt().coerceAtLeast(0),
    floor((offset.y + verticalScrollOffset.value) / textSize.height - TOP_MARGIN).toInt().coerceAtLeast(0)
)

internal data class CodePosition(val x: Int, val y: Int)
internal data class CursorPosition(val codePosition: CodePosition, val wantedX: Int)

internal fun Rope<LineMetrics>.indexOf(codePosition: CodePosition): Int =
    getIndexOfKthLine(codePosition.y) + codePosition.x

internal fun Rope<LineMetrics>.codePositionOf(index: Int, coerce: Boolean = false): CodePosition {
    require(coerce || index in 0..length) { "Index out of bounds!" }
    val coercedIndex = index.coerceIn(0..length)

    val slice = slice(0, coercedIndex)
    val y = slice.lineCount - 1
    val x = coercedIndex - getIndexOfKthLine(y)
    return CodePosition(x, y)
}

@Composable
private fun BoxScope.HorizontalScrollbar(editorState: EditorState) {
    HorizontalScrollbar(
        object : ScrollbarAdapter {
            override val contentSize: Double
                get() = editorState.getMaxHorizontalScroll() + viewportSize
            override val scrollOffset: Double
                get() = editorState.horizontalScrollOffset.value.toDouble()
            override val viewportSize: Double
                get() = (editorState.canvasSize.value.width - GUTTER_SIZE * editorState.textSize.width).toDouble()

            override suspend fun scrollTo(scrollOffset: Double) {
                editorState.horizontalScrollOffset.value =
                    editorState.coerceHorizontalOffset(scrollOffset.toFloat())
            }

        },
        // idk why / 2, it just doesn't work without it
        Modifier.align(Alignment.BottomCenter).absolutePadding(left = (GUTTER_SIZE * editorState.textSize.width / 2).dp)
    )
}

@Composable
private fun BoxScope.VerticalScrollbar(editorState: EditorState) {
    VerticalScrollbar(
        object : ScrollbarAdapter {
            override val contentSize: Double
                get() = editorState.getMaxVerticalScroll().toDouble() + viewportSize
            override val scrollOffset: Double
                get() = editorState.verticalScrollOffset.value.toDouble()
            override val viewportSize: Double
                get() = editorState.canvasSize.value.height.toDouble()

            override suspend fun scrollTo(scrollOffset: Double) {
                editorState.verticalScrollOffset.value =
                    editorState.coerceVerticalOffset(scrollOffset.toFloat())
            }

        },
        Modifier.align(Alignment.CenterEnd)
    )
}

@Composable
private fun EditorState.initHorizontalScrollState() = rememberScrollableState { delta ->
    val newScrollOffset = coerceHorizontalOffset(horizontalScrollOffset.value - delta)
    val scrollConsumed = horizontalScrollOffset.value - newScrollOffset
    horizontalScrollOffset.value = newScrollOffset
    scrollConsumed
}

@Composable
private fun EditorState.initVerticalScrollState() = rememberScrollableState { delta ->
    val newScrollOffset = coerceVerticalOffset(verticalScrollOffset.value - delta)
    val scrollConsumed = verticalScrollOffset.value - newScrollOffset
    verticalScrollOffset.value = newScrollOffset
    scrollConsumed
}

private suspend fun EditorState.rerenderText(
    renderedText: MutableState<RenderedText?>,
    settings: FontSettings,
    textMeasurer: TextMeasurer,
    previousRope: MutableState<Rope<LineMetrics>?>
): Unit = withContext(Dispatchers.Default) {
    val text = renderedText.value
    val verticalOffset = verticalScrollOffset.value
    val canvasSize = canvasSize.value

    if (text == null || previousRope.value != rope.value
        || text.textSize != settings.fontSize
        || (text.from > 0
                && verticalOffset - text.from * textSize.height < canvasSize.height / 2)
        || (text.to < rope.value.lineCount
                && text.to * textSize.height - verticalOffset - canvasSize.height < canvasSize.height / 2)
    ) {
        previousRope.value = rope.value
        val from = floor((verticalOffset - canvasSize.height) / textSize.height).toInt()
            .coerceAtLeast(0)
        val to = ceil((verticalOffset + 2 * canvasSize.height) / textSize.height).toInt()
            .coerceAtMost(rope.value.lineCount)
        EditorView.log.info("Relayout from $from to $to")
        renderedText.value = textMeasurer.layoutLines(rope.value, from, to, settings)
        // in case text size has changed we want to maintain correct verticalScrollOffset
        verticalScrollOffset.value = coerceVerticalOffset(verticalScrollOffset.value)
    }
}

private fun EditorState.coerceVerticalOffset(offset: Float) = offset
    .coerceAtLeast(0f)
    .coerceAtMost(getMaxVerticalScroll())

private fun EditorState.coerceHorizontalOffset(offset: Float) = offset
    .coerceAtLeast(0f)
    .coerceAtMost(getMaxHorizontalScroll())

private fun EditorState.getMaxVerticalScroll() =
    ((rope.value.lineCount + 5) * textSize.height - canvasSize.value.height).coerceAtLeast(0f)

private fun EditorState.getMaxHorizontalScroll() =
    ((rope.value.maxLineLength + GUTTER_SIZE) * textSize.width - canvasSize.value.width).coerceAtLeast(0f)

private fun DrawScope.drawGutter(
    settings: FontSettings,
    textSize: Size,
    verticalScrollOffset: Float,
    textMeasurer: TextMeasurer,
    textLength: Int
) {
    drawRect(
        AppTheme.colors.background.material.background,
        size = Size(GUTTER_SIZE * textSize.width, size.height)
    )

    val textStyle = getTextStyle(settings).copy(color = AppTheme.code.simple.color.copy(alpha = 0.3f))
    val minLineNumber = (floor(verticalScrollOffset / textSize.height).toInt() - 3).coerceAtLeast(0)
    val maxLineNumber =
        (ceil((verticalScrollOffset + size.height) / textSize.height).toInt() + 3).coerceAtMost(textLength)
    for (lineNumber in minLineNumber until maxLineNumber) {
        val lineNumberString = (lineNumber + 1).toString()
        val xOffset = (GUTTER_TEXT_OFFSET - lineNumberString.length) * textSize.width
        val yOffset = -verticalScrollOffset + (lineNumber + TOP_MARGIN) * textSize.height
        drawText(
            textMeasurer.measure(lineNumberString, textStyle),
            topLeft = Offset(xOffset, yOffset),
        )
    }

    drawLine(
        Color.Gray,
        Offset(GUTTER_SIZE * textSize.width, 0f),
        Offset(GUTTER_SIZE * textSize.width, size.height)
    )
}

internal data class RenderedText(
    val textLayoutResult: TextLayoutResult,
    val from: Int,
    val to: Int,
    val textSize: TextUnit
)

private fun TextMeasurer.layoutLines(
    rope: Rope<LineMetrics>,
    from: Int,
    to: Int,
    settings: FontSettings
): RenderedText {
    val builder = AnnotatedString.Builder()
    val text = rope.getLines(from, to)

    builder.append(text)
    builder.addStyle(AppTheme.code.simple, 0, text.length)

    val textLayoutResult = measure(
        builder.toAnnotatedString(),
        getTextStyle(settings),
    )
    return RenderedText(textLayoutResult, from, to, settings.fontSize)
}

internal fun getTextStyle(settings: FontSettings) =
    TextStyle(fontFamily = settings.fontFamily, fontSize = settings.fontSize)
