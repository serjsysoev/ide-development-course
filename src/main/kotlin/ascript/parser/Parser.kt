package ascript.parser

import ascript.ast.Program
import ascript.grammar.AProgram
import ascript.lexer.AScriptLexer
import language.grammar.ASTBuilder


fun createProgram(input: CharSequence): Result<Program> {
    val tokens = AScriptLexer().tokenize(input)
    return ASTBuilder(AProgram(), tokens).build()
}


fun main() {
    val myProgramm =
        """  
        func add(a: number, b: number)
        {
           return a + b;
        }

        func main() {
        }
        """


}