package ascript.lexer.Tokenizer

import language.lexer.tokenizer.Token
import language.lexer.tokenizer.TokenParser


abstract class SymbolToken(val symbol: String) : Token


interface RoundBracketToken
object RoundBracketOpenToken : SymbolToken("("), RoundBracketToken
object RoundBracketCloseToken : SymbolToken(")"), RoundBracketToken

interface CurlyBracketToken
object CurlyBracketOpenToken : SymbolToken("{"), CurlyBracketToken
object CurlyBracketCloseToken : SymbolToken("}"), CurlyBracketToken

object SemicolonToken : SymbolToken(";")
object ColonToken : SymbolToken(":")
object CommaToken : SymbolToken(",")
object AssignToken : SymbolToken("=")


val MARKUP_TOKENS = listOf(
    RoundBracketOpenToken,
    RoundBracketCloseToken,
    CurlyBracketOpenToken,
    CurlyBracketCloseToken,
    SemicolonToken,
    ColonToken,
    CommaToken,
    AssignToken
)



open class BooleanToken(symbol: String, val value: Boolean) : SymbolToken(symbol)

object TrueToken : BooleanToken("true", true)
object FalseToken : BooleanToken("false", false)

val BOOLEAN_TOKENS = listOf(
    TrueToken,
    FalseToken
)

abstract class OpToken(symbol: String, val maybeUnary: Boolean, val maybeBinary: Boolean) : SymbolToken(symbol)

// Concatenation of Strings
object StringConcatOpToken : OpToken("%", false, true)

// Operations on numbers
interface OnNumbers

// Operations return number
interface RetNumbers

// result of operators is boolean
interface RetBoolean

// Operations on booleans
interface OnBooleans


interface ArithmeticOp
object PlusOpToken : OpToken("+", false, true), OnNumbers, RetNumbers, ArithmeticOp
object MinusOpToken : OpToken("-", true, true), OnNumbers, RetNumbers, ArithmeticOp
object MulOpToken : OpToken("*", false, true), OnNumbers, RetNumbers, ArithmeticOp
object DivOpToken : OpToken("/", false, true), OnNumbers, RetNumbers, ArithmeticOp


interface LogicalOp
object NotOpToken : OpToken("!", true, false), OnBooleans, RetBoolean, LogicalOp
object OrOpToken : OpToken("||", false, true), OnBooleans, RetBoolean, LogicalOp
object AndOpToken : OpToken("&&", false, true), OnBooleans, RetBoolean, LogicalOp


// Operations on objects
interface OnObjects

interface EqualityOp
object EqOpToken : OpToken("==", false, true), OnObjects, RetBoolean, EqualityOp
object NotEqOpToken : OpToken("!=", false, true), OnObjects, RetBoolean, EqualityOp


interface CompOperatorOp
object LessOpToken : OpToken("<", false, true), OnNumbers, RetBoolean, CompOperatorOp
object LessOrEqualOpToken : OpToken("<=", false, true), OnNumbers, RetBoolean, CompOperatorOp
object GreaterOpToken : OpToken(">", false, true), OnNumbers, RetBoolean, CompOperatorOp
object GreaterOrEqualOpToken : OpToken(">=", false, true), OnNumbers, RetBoolean, CompOperatorOp


val ONE_CHAR_OP_TOKENS = listOf(
    StringConcatOpToken,
    PlusOpToken,
    MinusOpToken,
    MulOpToken,
    DivOpToken,
    NotOpToken,
    LessOpToken,
    GreaterOpToken,
)

val TWO_CHAR_OP_TOKENS: List<OpToken> = listOf(
    OrOpToken,
    AndOpToken,
    EqOpToken,
    NotEqOpToken,
    LessOrEqualOpToken,
    GreaterOrEqualOpToken,
)


abstract class KeywordToken(symbol: String): SymbolToken(symbol)

object IfKeywordToken: KeywordToken("if")
object ElseKeywordToken: KeywordToken("else")
object VarToken: KeywordToken("var")
object WhileKeywordToken: KeywordToken("while")
object FuncKeywordToken: KeywordToken("fun")
object ReturnKeywordToken: KeywordToken("return")
object ProcKeywordToken: KeywordToken("proc")
object PrintKeywordToken: KeywordToken("print")


val KEYWORD_TOKENS = listOf(
    IfKeywordToken,
    ElseKeywordToken,
    VarToken,
    WhileKeywordToken,
    FuncKeywordToken,
    ReturnKeywordToken,
    ProcKeywordToken,
    PrintKeywordToken
)


abstract class TypeToken(symbol: String) : SymbolToken(symbol)

object NumberTypeToken : TypeToken("number")
object StringTypeToken : TypeToken("string")
object BoolTypeToken : TypeToken("bool")

val TYPE_TOKENS = listOf(
    NumberTypeToken,
    StringTypeToken,
    BoolTypeToken,
)


abstract class RegexpToken(val pattern: String): Token

object IdentifierToken: RegexpToken("^[a-zA-Z_][a-zA-Z0-9_]*\$")

object NumberToken: RegexpToken("^[0-9_]+\$")

data class StringLiteralToken(val value: String): Token



internal val TOKENS_PARSER: List<TokenParser<out Token>> = listOf(
    StringLiteralParser(),
    SymbolParser(KEYWORD_TOKENS),
    SymbolParser(TYPE_TOKENS),
    SymbolParser(BOOLEAN_TOKENS),
    SymbolParser(TWO_CHAR_OP_TOKENS),
    SymbolParser(ONE_CHAR_OP_TOKENS),
    SymbolParser(MARKUP_TOKENS),
    RegexpTokenParser(NumberToken),
    RegexpTokenParser(IdentifierToken),
)




