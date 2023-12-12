package language.lexer.tokenizer

interface TokenParser<T: Token> {
    fun Tokenizer.parse(): T?
}