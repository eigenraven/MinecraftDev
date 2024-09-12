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

package io.mcdev.obfex.formats.tinyv2.gen;

import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import static io.mcdev.obfex.formats.tinyv2.gen.psi.TinyV2Types.*;
import static com.intellij.psi.TokenType.*;

%%

%{
    private int versionNumCounter = 0;

    public TinyV2Lexer() {
        this((java.io.Reader)null);
    }
%}

%public
%class TinyV2Lexer
%implements FlexLexer
%function advance
%type IElementType

%s HEADER
%s MAYBE_PROPERTIES
%s PROPERTIES
%s PROP_VALUE
%s DOC_LINE
%s CORE
%s CORE2
%s MAPPING
%s PARAM_MAPPING
%s LOCAL_VAR_MAPPING
%s SIGNATURE

%unicode

SAFE_STRING=[^\\\n\r\t\0\s]
PROP_VALUE=[^\s]

DOC_TEXT=[^\r\n]+

NAME_ELEMENT=[\p{L}_\p{Sc}][\p{L}\p{N}_\p{Sc}]*

PRIMITIVE=[ZBCSIFDJV]
CLASS_TYPE=(\[+[ZBCSIFDJ]|\[*L[^;\s]+;)

DIGIT=\d

COMMENT=\s*#.*

WHITE_SPACE=\s
CRLF=\r\n|\n|\r

%%

<YYINITIAL> {
    "tiny"                             { yybegin(HEADER); return TINY_KEY; }
}

<HEADER> {
    {DIGIT}+                           { if (versionNumCounter++ <= 2) return VERSION_NUM; else return NAMESPACE_KEY; }
    {SAFE_STRING}+                     { return NAMESPACE_KEY; }
    {CRLF}                             { yybegin(MAYBE_PROPERTIES); return CRLF; }
}

<MAYBE_PROPERTIES> {
    "c"                                { yybegin(MAPPING); return CLASS_KEY; }
    {CRLF}                             { yybegin(CORE); return CRLF; }
    {WHITE_SPACE}                      { yybegin(PROPERTIES); return WHITE_SPACE; }
}

<PROPERTIES> {
    {SAFE_STRING}+                     { return PROPERTY_KEY; }
    {CRLF}                             { yybegin(MAYBE_PROPERTIES); return CRLF; }
    {WHITE_SPACE}                      { yybegin(PROP_VALUE); return WHITE_SPACE; }
}

<PROP_VALUE> {
    {CRLF}                             { yybegin(MAYBE_PROPERTIES); return CRLF; }
    {PROP_VALUE}+                      { return PROPERTY_VALUE; }
}

<CORE> {
    "c"                                { yybegin(MAPPING); return CLASS_KEY; }
    {CRLF}                             { yybegin(CORE); return CRLF; }
    {WHITE_SPACE}                      { yybegin(CORE2); return WHITE_SPACE; }
}

<CORE2> {
    "m"                                { yybegin(MAPPING); return METHOD_KEY; }
    "f"                                { yybegin(MAPPING); return FIELD_KEY; }
    "p"                                { yybegin(PARAM_MAPPING); return PARAM_KEY; }
    "v"                                { yybegin(LOCAL_VAR_MAPPING); return VAR_KEY; }
    "c"                                { yybegin(DOC_LINE); return COMMENT_KEY; }
}

<DOC_LINE> {
    {DOC_TEXT}                         { return DOC_TEXT; }
    {CRLF}                             { yybegin(CORE); return CRLF; }
}

<MAPPING> {
    "("                                { yybegin(SIGNATURE); return OPEN_PAREN; }
    "/"                                { return SLASH; }
    {PRIMITIVE} {WHITE_SPACE}?         { zzMarkedPos = zzStartRead + 1; return PRIMITIVE; }
    "<init>"                           { return INIT; }
    "<clinit>"                         { return CLINIT; }
    "package-info"                     { return NAME_ELEMENT; }
    {CLASS_TYPE}                       { return CLASS_TYPE; }
    {NAME_ELEMENT}                     { return NAME_ELEMENT; }
}

<PARAM_MAPPING> {
    {DIGIT}+                           { yybegin(MAPPING); return DIGIT; }
}

<LOCAL_VAR_MAPPING> {
    {DIGIT}+                           { return DIGIT; }
    {NAME_ELEMENT}                     { return NAME_ELEMENT; }
}

<SIGNATURE> {
    ")"                                { return CLOSE_PAREN; }
    {PRIMITIVE}                        { return PRIMITIVE; }
    {CLASS_TYPE}                       { return CLASS_TYPE; }
    {CRLF}                             { yybegin(CORE); return CRLF; }
    {WHITE_SPACE}                      { yybegin(MAPPING); return WHITE_SPACE; }
}

{COMMENT}                              { return COMMENT; }
{CRLF}                                 { yybegin(CORE); return CRLF; }
{WHITE_SPACE}                          { return WHITE_SPACE; }
[^]                                    { return BAD_CHARACTER; }
