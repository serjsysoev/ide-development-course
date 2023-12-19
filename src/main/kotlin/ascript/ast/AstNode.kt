package ascript.ast

import ascript.lexer.Tokenizer.*
import language.ast.ASTNode
import language.ast.Visitor
import language.grammar.ANode
import language.lexer.tokenizer.Location


class StmtList(val list: List<Stmt>, override val location: Location): ASTNode {
    override fun accept(visitor: Visitor) {
        TODO("Not yet implemented")
    }

}

sealed class Stmt: ASTNode {
    class VarDeclaration(
        val symbol: Expr.SymbolName,
        val expr: Expr,
        override val location: Location
    ) : Stmt() {
        override fun accept(visitor: Visitor) {
            TODO("Not yet implemented")
        }
    }


    class Assignment(
        val symbol: Expr.SymbolName,
        val expr: Expr,
        override val location: Location
    ) : Stmt() {
        override fun accept(visitor: Visitor) {
            TODO("Not yet implemented")
        }
    }

    class IfStatement(
        val condition: Expr,
        val thenBlock: Block,
        val elseBlock: Block?,
        override val location: Location

    ) : Stmt() {
        override fun accept(visitor: Visitor) {
            TODO("Not yet implemented")
        }
    }

    class WhileStatement(
        val condition: Expr,
        val block: Block,
        override val location: Location
    ) : Stmt() {
        override fun accept(visitor: Visitor) {
            TODO("Not yet implemented")
        }
    }

    data class Block(
        val statements: List<Stmt>,
        override val location: Location
    ) : Stmt() {
        override fun accept(visitor: Visitor) {
            TODO("Not yet implemented")
        }
    }

    class PrintStatement(
        val expr: Expr,
        override val location: Location
    ) : Stmt() {
        override fun accept(visitor: Visitor) {
            TODO("Not yet implemented")
        }
    }


    class FuncDeclaration(
        val symbol: Expr.SymbolName,
        val parameters: List<FunParameter>,
        val block: Block,
        override val location: Location
    ) : Stmt() {
        override fun accept(visitor: Visitor) {
            TODO("Not yet implemented")
        }
    }

    class ReturnStatement(
        val expr: Expr,
        override val location: Location
    ) : Stmt() {
        override fun accept(visitor: Visitor) {
            TODO("Not yet implemented")
        }
    }

    class ProcDeclaration(
        val symbol: Expr.SymbolName,
        val parameters: List<FunParameter>,
        val block: Block,
        override val location: Location
    ) : Stmt() {
        override fun accept(visitor: Visitor) {
            TODO("Not yet implemented")
        }
    }

    class ProcCall(
        val symbol: Expr.SymbolName,
        val arguments: List<Expr>,
        override val location: Location
    ) : Stmt() {
        override fun accept(visitor: Visitor) {
            TODO("Not yet implemented")
        }
    }
}
sealed class ArgType: ASTNode {
    class BooleanType(override val location: Location): ArgType() {
        override fun accept(visitor: Visitor) {
            TODO("Not yet implemented")
        }
    }

    class StringType(override val location: Location): ArgType() {
        override fun accept(visitor: Visitor) {
            TODO("Not yet implemented")
        }
    }

    class NumberType(override val location: Location): ArgType() {
        override fun accept(visitor: Visitor) {
            TODO("Not yet implemented")
        }
    }
}


data class FunParameter(
    val symbol: Expr.SymbolName,
    val type: ArgType,
    override val location: Location
) : ASTNode {
    override fun accept(visitor: Visitor) {
        TODO("Not yet implemented")
    }
}

data class Program(
    val statementList: StmtList,
    override val location: Location
) : ASTNode {
    override fun accept(visitor: Visitor) {
        TODO("Not yet implemented")
    }
}


class Term(val expr: Expr, override val location: Location): ASTNode {
    override fun accept(visitor: Visitor) {
        TODO("Not yet implemented")
    }
}

class Factor(val expr: Expr, override val location: Location) : ASTNode {
    override fun accept(visitor: Visitor) {
        TODO("Not yet implemented")
    }
}

class Arguments(val arguments: Argument?, override val location: Location): ASTNode {
    override fun accept(visitor: Visitor) {
        TODO("Not yet implemented")
    }
}

class Argument(val exprs: List<Expr>, override val location: Location): ASTNode {
    override fun accept(visitor: Visitor) {
        TODO("Not yet implemented")
    }
}

class FunParameters(val parameter: List<FunParameter>, override val location: Location): ASTNode {
    override fun accept(visitor: Visitor) {
        TODO("Not yet implemented")
    }
}

sealed class Expr: ASTNode {
    class SymbolName(
        val identifier: IdentifierToken,
        override val location: Location
    ) : Expr() {
        override fun accept(visitor: Visitor) {
            TODO("Not yet implemented")
        }
    }

    class BinaryOpExpr(
        val leftExpr: Expr,
        val rightExpr: Expr,
        val opToken:  ANode.Terminal.Token<OpToken>,
        override val location: Location
    ): Expr() {
        override fun accept(visitor: Visitor) {
            TODO("Not yet implemented")
        }
    }

    class UnaryOp(
        val operation: OpToken, val expr: Expr,
        override val location: Location
    ): Expr() {
        override fun accept(visitor: Visitor) {
            TODO("Not yet implemented")
        }
    }

    class FuncCall(
        val symbolName: SymbolName,
        val arguments: List<Expr>,
        override val location: Location
    ) : Expr() {
        override fun accept(visitor: Visitor) {
            TODO("Not yet implemented")
        }
    }


    class BooleanValue(
        val value: BooleanToken,
        override val location: Location
    ) : Expr() {
        override fun accept(visitor: Visitor) {
            TODO("Not yet implemented")
        }
    }


    class StringLiteral(
        val value: StringLiteralToken,
        override val location: Location
    ) : Expr() {
        override fun accept(visitor: Visitor) {
            TODO("Not yet implemented")
        }
    }

    class NumberValue(
        val value: NumberToken,
        override val location: Location
    ) : Expr() {
        override fun accept(visitor: Visitor) {
            TODO("Not yet implemented")
        }
    }
}