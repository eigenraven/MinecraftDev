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

package io.mcdev.obfex.mappings

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.vfs.VirtualFile
import io.mcdev.obfex.mappings.MappingsFormatParser.Companion.EP_NAME
import io.mcdev.obfex.ref.LocalMemberRef
import io.mcdev.obfex.ref.MemberName

/**
 * A parser for Java obfuscation mapping file formats. Provide implementations using the [EP_NAME] extension point.
 */
interface MappingsFormatParser {

    /**
     * File extensions this parser expects to see. This will be the first thing checked to determine the file type, if
     * multiple parsers share the same extension match then [isSupportedFile] will be used as a fallback.
     */
    val expectedFileExtensions: Array<String>

    /**
     * Quickly check the file to determine if this parser supports it. Only used as a fallback if multiple parsers
     * match the same [expectedFileExtensions].
     */
    fun isSupportedFile(file: VirtualFile): Boolean = false

    /**
     * Attempt to parse the given file into a [MappingsDefinition]. If the file type is unknown or not supported by this
     * parser, or the file contents are too corrupt to parse, return `null`.
     *
     * It's generally preferable to return a _partial_ [MappingsDefinition] when the mappings file contains errors (that
     * is, it's preferable to skip over errors rather than fail completely). Record parse errors in the
     * [MappingsDefinition] so they can be presented to the user.
     */
    fun parse(file: VirtualFile): MappingsDefinition?

    companion object {
        @JvmStatic
        val EP_NAME = ExtensionPointName.create<MappingsFormatParser>("io.mcdev.obfex.mappingsFormatParser")
    }
}

/**
 * A [MappingsFormatParser] for mapping formats without namespaces.
 */
interface UnnamedMappingsFormatParser : MappingsFormatParser {

    /**
     * Helper property for unnamed mapping formats.
     */
    val unnamedFrom: Int
        get() = 0

    /**
     * Helper property for unnamed mapping formats.
     */
    val unnamedTo: Int
        get() = 1

    val <T : MemberName> T.from: NS<T>
        get() = ns(unnamedFrom)
    val <T : MemberName> T.to: NS<T>
        get() = ns(unnamedTo)

    val <T : LocalMemberRef<*>> T.from: NS<T>
        get() = ns(unnamedFrom)
    val <T : LocalMemberRef<*>> T.to: NS<T>
        get() = ns(unnamedTo)
}
