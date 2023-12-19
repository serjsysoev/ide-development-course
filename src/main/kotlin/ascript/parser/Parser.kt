package ascript.parser

import ascript.ast.Program
import ascript.grammar.AProgram
import ascript.lexer.AScriptLexer
import language.grammar.ASTBuilder


fun buildAst(input: CharSequence): Result<Program> {
    val tokens = AScriptLexer().tokenize(input)
    return ASTBuilder(AProgram(), tokens).build()
}