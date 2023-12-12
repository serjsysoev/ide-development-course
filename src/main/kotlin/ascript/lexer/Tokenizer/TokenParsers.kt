package ascript.lexer.Tokenizer

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

class RegexpTokenParser(private val token: RegexpToken): TokenParser<RegexpToken> {
    override fun Tokenizer.parse(): RegexpToken? {
        val text = takeWhile { !it.isWhitespace()}

        if (token.pattern.toRegex().matchEntire(text) != null) {
            return token
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
                return StringLiteralToken(text.toString())
            }
        }
        return null
    }
}