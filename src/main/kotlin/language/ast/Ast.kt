package language.ast

import ascript.ast.AstVisitor
import language.lexer.tokenizer.Location

interface ASTNode {
    val location: Location
    fun <T: Any?, R> accept(visitor: AstVisitor<T, R>, context: T): R
}

