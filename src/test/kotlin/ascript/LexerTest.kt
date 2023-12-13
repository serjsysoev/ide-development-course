package ascript

import ascript.lexer.AScriptLexer
import ascript.lexer.Tokenizer.*
import language.lexer.tokenizer.ConcreteToken
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class LexerTest {
    private val ascriptLext = AScriptLexer()

    @Test
    fun `String Literal Test`() {
        val oneStringLiteral = "\"Help me\""
        val tokens = ascriptLext.tokenize(oneStringLiteral)
        assertEquals(1, tokens.size)
        assertTrue(tokens[0].token is StringLiteralToken)
        when(val token = tokens[0].token) {
            is StringLiteralToken -> {
                assertEquals("Help me", token.value)
            }
        }
    }

    @Test
    fun `Boolean Test`() {
        val text = "true false"
        val tokens = ascriptLext.tokenize(text).filterIsInstance<ConcreteToken<BooleanToken>>()

        assertEquals(2, tokens.size)
        assertEquals(true, tokens[0].token.value)
        assertEquals(false, tokens[1].token.value)
    }


    @Test
    fun `Numbers Test`() {
        val text = "15 23 44"
        val tokens = ascriptLext.tokenize(text).filterIsInstance<ConcreteToken<NumberToken>>()

        assertEquals(3, tokens.size)

        assertEquals(15, tokens[0].token.value)
        assertEquals(23, tokens[1].token.value)
        assertEquals(44, tokens[2].token.value)
    }


    @Test
    fun `Some Program Test`() {
        val input1 = "var a = 33;"
        val sequence1 = listOf(VarToken, IdentifierToken("a"), AssignToken, NumberToken(33), SemicolonToken)
        val tokens1 = ascriptLext.tokenize(input1).map { it.token }
        assertEquals(sequence1.size, tokens1.size)
        assertEquals(sequence1.map { it.javaClass }, tokens1.map { it.javaClass })
    }

}