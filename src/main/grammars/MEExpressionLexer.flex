/*
 * Minecraft Development for IntelliJ
 *
 * https://mcdev.io/
 *
 * Copyright (C) 2024 minecraft-dev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, version 3.0 only.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.demonwav.mcdev.platform.mixin.expression;

import com.demonwav.mcdev.platform.mixin.expression.gen.psi.MEExpressionTypes;
import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;

%%

%public
%class MEExpressionLexer
%implements FlexLexer
%function advance
%type IElementType

%state STRING

%unicode

WHITE_SPACE = [\ \n\t\r]
RESERVED = assert|break|case|catch|const|continue|default|else|finally|for|goto|if|switch|synchronized|try|while|yield|_
WILDCARD = "?"
NEW = new
INSTANCEOF = instanceof
BOOL_LIT = true|false
NULL_LIT = null
DO = do
RETURN = return
THROW = throw
THIS = this
SUPER = super
CLASS = class
IDENTIFIER = [A-Za-z_][A-Za-z0-9_]*
INT_LIT = ( [0-9]+ | 0x[0-9a-fA-F]+ )
DEC_LIT = [0-9]*\.[0-9]+
PLUS = "+"
MINUS = -
MULT = "*"
DIV = "/"
MOD = %
BITWISE_NOT = "~"
DOT = "."
COMMA = ,
LEFT_PAREN = "("
RIGHT_PAREN = ")"
LEFT_BRACKET = "["
RIGHT_BRACKET = "]"
LEFT_BRACE = "{"
RIGHT_BRACE = "}"
AT = @
SHL = <<
SHR = >>
USHR = >>>
LT = <
LE = <=
GT = >
GE = >=
EQ = ==
NE = "!="
BITWISE_AND = &
BITWISE_XOR = "^"
BITWISE_OR = "|"
ASSIGN = =
METHOD_REF = ::

STRING_TERMINATOR = '
STRING_ESCAPE = \\'|\\\\

%%

<YYINITIAL> {
    {WHITE_SPACE}+ { return TokenType.WHITE_SPACE; }
    {RESERVED} { return MEExpressionTypes.TOKEN_RESERVED; }
    {WILDCARD} { return MEExpressionTypes.TOKEN_WILDCARD; }
    {NEW} { return MEExpressionTypes.TOKEN_NEW; }
    {INSTANCEOF} { return MEExpressionTypes.TOKEN_INSTANCEOF; }
    {BOOL_LIT} { return MEExpressionTypes.TOKEN_BOOL_LIT; }
    {NULL_LIT} { return MEExpressionTypes.TOKEN_NULL_LIT; }
    {DO} { return MEExpressionTypes.TOKEN_DO; }
    {RETURN} { return MEExpressionTypes.TOKEN_RETURN; }
    {THROW} { return MEExpressionTypes.TOKEN_THROW; }
    {THIS} { return MEExpressionTypes.TOKEN_THIS; }
    {SUPER} { return MEExpressionTypes.TOKEN_SUPER; }
    {CLASS} { return MEExpressionTypes.TOKEN_CLASS; }
    {IDENTIFIER} { return MEExpressionTypes.TOKEN_IDENTIFIER; }
    {INT_LIT} { return MEExpressionTypes.TOKEN_INT_LIT; }
    {DEC_LIT} { return MEExpressionTypes.TOKEN_DEC_LIT; }
    {PLUS} { return MEExpressionTypes.TOKEN_PLUS; }
    {MINUS} { return MEExpressionTypes.TOKEN_MINUS; }
    {MULT} { return MEExpressionTypes.TOKEN_MULT; }
    {DIV} { return MEExpressionTypes.TOKEN_DIV; }
    {MOD} { return MEExpressionTypes.TOKEN_MOD; }
    {BITWISE_NOT} { return MEExpressionTypes.TOKEN_BITWISE_NOT; }
    {DOT} { return MEExpressionTypes.TOKEN_DOT; }
    {COMMA} { return MEExpressionTypes.TOKEN_COMMA; }
    {LEFT_PAREN} { return MEExpressionTypes.TOKEN_LEFT_PAREN; }
    {RIGHT_PAREN} { return MEExpressionTypes.TOKEN_RIGHT_PAREN; }
    {LEFT_BRACKET} { return MEExpressionTypes.TOKEN_LEFT_BRACKET; }
    {RIGHT_BRACKET} { return MEExpressionTypes.TOKEN_RIGHT_BRACKET; }
    {LEFT_BRACE} { return MEExpressionTypes.TOKEN_LEFT_BRACE; }
    {RIGHT_BRACE} { return MEExpressionTypes.TOKEN_RIGHT_BRACE; }
    {AT} { return MEExpressionTypes.TOKEN_AT; }
    {SHL} { return MEExpressionTypes.TOKEN_SHL; }
    {SHR} { return MEExpressionTypes.TOKEN_SHR; }
    {USHR} { return MEExpressionTypes.TOKEN_USHR; }
    {LT} { return MEExpressionTypes.TOKEN_LT; }
    {LE} { return MEExpressionTypes.TOKEN_LE; }
    {GT} { return MEExpressionTypes.TOKEN_GT; }
    {GE} { return MEExpressionTypes.TOKEN_GE; }
    {EQ} { return MEExpressionTypes.TOKEN_EQ; }
    {NE} { return MEExpressionTypes.TOKEN_NE; }
    {BITWISE_AND} { return MEExpressionTypes.TOKEN_BITWISE_AND; }
    {BITWISE_XOR} { return MEExpressionTypes.TOKEN_BITWISE_XOR; }
    {BITWISE_OR} { return MEExpressionTypes.TOKEN_BITWISE_OR; }
    {ASSIGN} { return MEExpressionTypes.TOKEN_ASSIGN; }
    {METHOD_REF} { return MEExpressionTypes.TOKEN_METHOD_REF; }
    {STRING_TERMINATOR} { yybegin(STRING); return MEExpressionTypes.TOKEN_STRING_TERMINATOR; }
}

<STRING> {
    {STRING_ESCAPE} { return MEExpressionTypes.TOKEN_STRING_ESCAPE; }
    {STRING_TERMINATOR} { yybegin(YYINITIAL); return MEExpressionTypes.TOKEN_STRING_TERMINATOR; }
    [^'\\]+ { return MEExpressionTypes.TOKEN_STRING; }
}

[^] { return TokenType.BAD_CHARACTER; }
