package ascript.highlighting

import ascript.AScript
import ascript.ast.Program
import ascript.grammar.AProgram
import ascript.lexer.AScriptLexer
import backend.HighlightingBuilder
import language.ast.ASTNode
import language.grammar.ASTBuilder
import language.lexer.tokenizer.ConcreteToken
import language.lexer.tokenizer.Token

object AScriptHighlightingBuilder: HighlightingBuilder<Program>(AScript) {
    override fun buildHighlighting(ast: ASTNode, tokens: List<ConcreteToken<out Token>>): List<HToken> {
        val curAst = ast as? Program ?: return emptyList()
        return HighlighterVisitor(tokens, curAst).generateHighlighing()
    }

    override fun tokenize(input: CharSequence): List<ConcreteToken<Token>> {
        return AScriptLexer().tokenize(input)
    }

    override fun buildAst(tokens: List<ConcreteToken<Token>>): Result<Program> {
        return ASTBuilder(AProgram(), tokens).build()
    }
}
