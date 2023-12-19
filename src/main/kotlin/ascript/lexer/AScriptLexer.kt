package ascript.lexer

import ascript.lexer.Tokenizer.TOKENS_PARSER
import language.lexer.Lexer
import language.lexer.tokenizer.ConcreteToken
import language.lexer.tokenizer.Token
import language.lexer.tokenizer.Tokenizer


class AScriptLexer : Lexer {
    override fun tokenize(input: CharSequence): List<ConcreteToken<Token>> = Tokenizer(input, TOKENS_PARSER).tokenize()
}




