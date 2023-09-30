package ui.editor

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.awtEventOrNull
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.pointerInput
import java.awt.event.KeyEvent as AwtKeyEvent
import util.rope.lineCount
import util.rope.lineLength
import kotlin.math.min

@Composable
internal fun Modifier.pointerInput(editorState: EditorState): Modifier {
    var oldDragOffset by remember { mutableStateOf(Offset(0f, 0f)) }
    val cursorPosition = editorState.cursorPosition

    return pointerInput(Unit) {
        detectTapGestures(onPress = { offset ->
            cursorPosition.value = editorState.setCursorByCodePosition(offset)
            editorState.resetCursorBlinking()
        })
    }.pointerInput(Unit) { // TODO: selection
        detectDragGestures(
            onDragStart = { offset ->
                oldDragOffset = offset
                cursorPosition.value = editorState.setCursorByCodePosition(offset)
                editorState.resetCursorBlinking()
            },
            onDrag = { _, offset ->
                val newOffset = oldDragOffset + offset
                oldDragOffset = newOffset
                cursorPosition.value = editorState.setCursorByCodePosition(newOffset)
                editorState.resetCursorBlinking()
            })
    }
}

private fun EditorState.setCursorByCodePosition(offset: Offset): CursorPosition {
    val (x, y) = viewportToCode(offset)
    val newY = min(y, rope.value.lineCount - 1)
    val lineLength = rope.value.lineLength(newY)
    val newX = min(x, lineLength)
    return CursorPosition(CodePosition(newX, newY), newX)
}

internal fun Modifier.keyboardInput(editorState: EditorState): Modifier =
    onKeyEvent { keyEvent ->
        val cursorPosition = editorState.cursorPosition.value
        val rope = editorState.rope.value

        keyEvent.awtEventOrNull?.let { awtEvent ->
            if (awtEvent.id == AwtKeyEvent.KEY_TYPED) {
                println("`${awtEvent.keyChar}` ${awtEvent.keyChar.code}")
                val ropeIndex = rope.indexOf(cursorPosition.codePosition)
                val (newRope, newCursorPosition) = when (awtEvent.keyChar.code) {
                    AwtKeyEvent.VK_ENTER -> {
                        val newRope = rope.insert(ropeIndex, awtEvent.keyChar)
                        val newCodePosition = cursorPosition.codePosition.let { it.copy(x = 0, y = it.y + 1) }
                        newRope to CursorPosition(newCodePosition, newCodePosition.x)
                    }

                    AwtKeyEvent.VK_BACK_SPACE -> {
                        if (ropeIndex != 0) {
                            val newCodePosition = rope.codePositionOf(ropeIndex - 1)
                            val newRope = rope.delete(ropeIndex - 1, ropeIndex)
                            newRope to CursorPosition(newCodePosition, newCodePosition.x)
                        } else {
                            rope to cursorPosition.copy(wantedX = 0) // TODO: do we want this in history?
                        }
                    }

                    AwtKeyEvent.VK_DELETE -> {
                        if (ropeIndex != rope.length) {
                            rope.delete(ropeIndex, ropeIndex + 1)
                        } else {
                            rope
                        } to cursorPosition.copy(wantedX = cursorPosition.codePosition.x)
                    }

                    else -> {
                        val newRope = rope.insert(
                            editorState.rope.value.indexOf(cursorPosition.codePosition),
                            awtEvent.keyChar
                        )
                        val newCodePosition = cursorPosition.codePosition.let { it.copy(x = it.x + 1) }
                        newRope to CursorPosition(newCodePosition, newCodePosition.x)
                    }
                }
                editorState.rope.value = newRope
                editorState.cursorPosition.value = newCursorPosition
            }
        }

        if (keyEvent.type != KeyEventType.KeyDown || keyEvent.hasModifiers) return@onKeyEvent false
        when (keyEvent.key) {
            Key.DirectionRight -> {
                editorState.cursorPosition.value = cursorPosition.let { (codePosition, _) ->
                    val newIndex = rope.indexOf(codePosition) + 1
                    val newCodePosition = rope.codePositionOf(newIndex, coerce = true)
                    CursorPosition(newCodePosition, newCodePosition.x)
                }
            }

            Key.DirectionLeft -> {
                editorState.cursorPosition.value = cursorPosition.let { (codePosition, _) ->
                    val newIndex = rope.indexOf(codePosition) - 1
                    val newCodePosition = rope.codePositionOf(newIndex, coerce = true)
                    CursorPosition(newCodePosition, newCodePosition.x)
                }
            }

            Key.DirectionUp -> {
                editorState.cursorPosition.value = cursorPosition.let { (codePosition, wantedX) ->
                    if (codePosition.y - 1 >= 0) {
                        val lineLength = rope.lineLength(codePosition.y - 1)
                        val newCodePosition = CodePosition(min(wantedX, lineLength), codePosition.y - 1)
                        CursorPosition(newCodePosition, wantedX)
                    } else {
                        CursorPosition(CodePosition(0, 0), 0)
                    }
                }
            }

            Key.DirectionDown -> {
                editorState.cursorPosition.value = cursorPosition.let { (codePosition, wantedX) ->
                    if (codePosition.y + 1 < rope.lineCount) {
                        val lineLength = rope.lineLength(codePosition.y + 1)
                        val newCodePosition = CodePosition(min(wantedX, lineLength), codePosition.y + 1)
                        CursorPosition(newCodePosition, wantedX)
                    } else {
                        val lineLength = rope.lineLength(codePosition.y)
                        CursorPosition(CodePosition(lineLength, codePosition.y), lineLength)
                    }
                }
            }

            else -> {
                return@onKeyEvent false
            }
        }
        editorState.resetCursorBlinking()
        return@onKeyEvent true
    }

private val KeyEvent.hasModifiers
    get() = isCtrlPressed || isAltPressed || isShiftPressed || isMetaPressed

private fun EditorState.resetCursorBlinking() {
    isCursorVisible.value = true to isCursorVisible.value.second + 1
}
