package language.grammar

import language.lexer.tokenizer.Location
import language.lexer.tokenizer.Token
import kotlin.reflect.KClass

sealed class Error(open val type: ErrorType): Throwable()

data class LexicalError(val offset: Int, override val type: ErrorType): Error(type)

abstract class SyntaxError(override val type: ErrorType) : Error(type)

class ExpectedTokenError(val expectedToken: KClass<out Token>, val offset: Int): SyntaxError(ErrorType.EXPECTED_TOKEN)

data class ExpectedExpression(val expectedExpression: ANode): SyntaxError(ErrorType.EXPECTED_EXPRESSION)

class UnexpectedExpressionError(val location: Location) : Error(ErrorType.UNEXPECTED_EXPRESSION)

enum class ErrorType {
    UNEXPECTED_SYMBOL,
    UNEXPECTED_EXPRESSION,
    EXPECTED_EXPRESSION,
    EXPECTED_TOKEN
}

class EpsError(): Throwable()