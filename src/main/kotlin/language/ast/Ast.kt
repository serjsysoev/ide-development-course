package language.ast

import language.lexer.tokenizer.Location

interface ASTNode {
    abstract val location: Location
    abstract fun accept(visitor: Visitor)
}


class Visitor {
    fun visit() {

    }
}