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

package io.mcdev.obfex.formats.proguard.gen;

import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import static io.mcdev.obfex.formats.proguard.gen.psi.ProGuardTypes.*;
import static com.intellij.psi.TokenType.*;

%%

%{
    public ProGuardLexer() {
        this((java.io.Reader)null);
    }
%}

%public
%class ProGuardLexer
%implements FlexLexer
%function advance
%type IElementType

%s CORE

%unicode

NAME_ELEMENT=[\p{L}_\p{Sc}][\p{L}\p{N}_\p{Sc}]*

PRIMITIVE=void|boolean|char|byte|short|int|long|float|double

NUMBER=\d
POINTER=\s*->\s*

COMMENT=\s*#.*

WHITE_SPACE=\s
CRLF=\r\n|\n|\r

%%

<YYINITIAL> {
    ":"                                { return COLON; }
    "."                                { return DOT; }
    ","                                { return COMMA; }
    "("                                { return OPEN_PAREN; }
    ")"                                { return CLOSE_PAREN; }
    "<init>"                           { return INIT; }
    "<clinit>"                         { return INIT; }
    "[]"                               { return ARRAY_BRACKETS; }
    "package-info"                     { return PACKAGE_INFO; }
    {NUMBER}+                          { return NUMBER; }
    {POINTER}                          { return POINTER; }
    {PRIMITIVE}                        { return PRIMITIVE; }
    {NAME_ELEMENT}                     { return NAME_ELEMENT; }
}

{COMMENT}                              { return COMMENT; }
{WHITE_SPACE}                          { return WHITE_SPACE; }
[^]                                    { return BAD_CHARACTER; }
