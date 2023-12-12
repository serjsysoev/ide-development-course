package ascript.lexer

import ascript.lexer.Tokenizer.TOKENS_PARSER
import language.lexer.Lexer
import language.lexer.tokenizer.*


class AScriptLexer: Lexer {
    override fun tokenize(input: CharSequence): List<Either<ConcreteToken<Token>, TokenError>> = Tokenizer(input, TOKENS_PARSER).tokenize()
}




