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

package io.mcdev.obfex.formats.srg.gen;

import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import static io.mcdev.obfex.formats.srg.gen.psi.SrgTypes.*;
import static com.intellij.psi.TokenType.*;

%%

%{
    public SrgLexer() {
        this((java.io.Reader)null);
    }
%}

%public
%class SrgLexer
%implements FlexLexer
%function advance
%type IElementType

%s PACKAGE_ENTRY
%s ENTRY
%s SIGNATURE

%unicode

// Package name entries just match until whitespace is hit
PACKAGE_NAME=[^\s\r\n]+

// Name element is used for all parts of an identifier name
//  1. The parts of the package
//  2. The class name
//  3. The member name
NAME_ELEMENT=[\p{L}_\p{Sc}][\p{L}\p{N}_\p{Sc}]*

PRIMITIVE=[ZBCSIFDJV]
CLASS_TYPE=(\[+[ZBCSIFDJ]|\[*L[^;\s]+;)

COMMENT=\s*#.*
WHITE_SPACE=\s+
CRLF=\r\n|\n|\r

%%

<YYINITIAL> {
    "PK:"                              { yybegin(PACKAGE_ENTRY); return PACKAGE_KEY; }
    "CL:"                              { yybegin(ENTRY); return CLASS_KEY; }
    "FD:"                              { yybegin(ENTRY); return FIELD_KEY; }
    "MD:"                              { yybegin(ENTRY); return METHOD_KEY; }
}

<PACKAGE_ENTRY> {
    {PACKAGE_NAME}                     { return PACKAGE_NAME; }
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
    {CRLF}                             { yybegin(YYINITIAL); return CRLF; }
    {WHITE_SPACE}                      { yybegin(ENTRY); return WHITE_SPACE; }
}

{COMMENT}                              { return COMMENT; }
{CRLF}                                 { yybegin(YYINITIAL); return CRLF; }
{WHITE_SPACE}                          { return WHITE_SPACE; }
[^]                                    { return BAD_CHARACTER; }
