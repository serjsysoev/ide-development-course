package language.lexer

import language.lexer.tokenizer.ConcreteToken
import language.lexer.tokenizer.Token

interface Lexer {
    fun tokenize(input: CharSequence): List<ConcreteToken<Token>>
}