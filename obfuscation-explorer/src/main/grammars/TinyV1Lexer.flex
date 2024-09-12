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

package io.mcdev.obfex.formats.tinyv1.gen;

import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import static io.mcdev.obfex.formats.tinyv1.gen.psi.TinyV1Types.*;
import static com.intellij.psi.TokenType.*;

%%

%{
    public TinyV1Lexer() {
        this((java.io.Reader)null);
    }
%}

%public
%class TinyV1Lexer
%implements FlexLexer
%function advance
%type IElementType

%s NAMESPACES
%s CORE
%s ENTRY
%s SIGNATURE

%unicode

NAMESPACE=[^\s]

NAME_ELEMENT=[\p{L}_\p{Sc}][\p{L}\p{N}_\p{Sc}]*
PRIMITIVE=[ZBCSIFDJV]
CLASS_TYPE=(\[+[ZBCSIFDJ]|\[*L[^;\s]+;)

COMMENT=\s*#.*
WHITE_SPACE=\s+
CRLF=\r\n|\n|\r

%%

<YYINITIAL> {
    "v1"                               { yybegin(NAMESPACES); return V1_KEY; }
}

<NAMESPACES> {
    {NAMESPACE}+                       { return NAMESPACE_KEY; }
}

<CORE> {
    "CLASS"                            { yybegin(ENTRY); return CLASS_KEY; }
    "FIELD"                            { yybegin(ENTRY); return FIELD_KEY; }
    "METHOD"                           { yybegin(ENTRY); return METHOD_KEY; }
}

<ENTRY> {
    "("                                { yybegin(SIGNATURE); return OPEN_PAREN; }
    "/"                                { return SLASH; }
    {PRIMITIVE} {WHITE_SPACE}?         { zzMarkedPos = zzStartRead + 1; return PRIMITIVE; }
    {CLASS_TYPE}                       { return CLASS_TYPE; }
    {NAME_ELEMENT}                     { return NAME_ELEMENT; }
}

<SIGNATURE> {
    ")"                                { return CLOSE_PAREN; }
    {PRIMITIVE}                        { return PRIMITIVE; }
    {CLASS_TYPE}                       { return CLASS_TYPE; }
    {WHITE_SPACE}                      { yybegin(ENTRY); return WHITE_SPACE; }
}

{COMMENT}                              { return COMMENT; }
{CRLF}                                 { yybegin(CORE); return CRLF; }
{WHITE_SPACE}                          { return WHITE_SPACE; }
[^]                                    { return BAD_CHARACTER; }
