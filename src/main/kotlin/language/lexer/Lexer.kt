package language.lexer

import language.lexer.tokenizer.ConcreteToken
import language.lexer.tokenizer.Either
import language.lexer.tokenizer.Token
import language.lexer.tokenizer.TokenError

interface Lexer {
    fun tokenize(input: CharSequence): List<Either<ConcreteToken<Token>, TokenError>>
}