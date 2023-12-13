package ascript.lexer.Tokenizer

import language.lexer.tokenizer.Token
import language.lexer.tokenizer.TokenParser
import language.lexer.tokenizer.Tokenizer


class SymbolParser(private val tokens: List<SymbolToken>): TokenParser<SymbolToken> {
    override fun Tokenizer.parse(): SymbolToken? {
        val text = takeWhile { !it.isWhitespace()}

        for (token in tokens) {
            if (token.symbol == text) {
                return token
            }
        }

        return null
    }
}

open class RegexpTokenParser<T: Token>(private val matcher: RegexpTokenMatcher<T>): TokenParser<T> {
    override fun Tokenizer.parse(): T? {
        val regex = Regex(matcher.pattern)

        val match = regex.matchAt(input, curIndex)

        if (match != null && match.range.start == curIndex) {
           curIndex = match.range.endInclusive
           inc()
           return matcher.onMatch(match.value)
        }
        return null
    }
}


class StringLiteralParser: TokenParser<StringLiteralToken> {
    override fun Tokenizer.parse(): StringLiteralToken? {
        if (!isEOF() && peek() == '\"') {
            inc()

            val text = takeWhile { it != '\"' }
            if (!isEOF() && peek() == '\"') {
                inc()
                return StringLiteralToken(text.toString())
            }
        }
        return null
    }
}