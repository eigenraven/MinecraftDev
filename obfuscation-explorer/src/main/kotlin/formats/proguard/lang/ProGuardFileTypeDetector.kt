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

package io.mcdev.obfex.formats.proguard.lang

import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.FileTypeRegistry.FileTypeDetector
import com.intellij.openapi.util.io.ByteSequence
import com.intellij.openapi.vfs.VirtualFile
import org.intellij.lang.annotations.Language

class ProGuardFileTypeDetector : FileTypeDetector {

    override fun detect(file: VirtualFile, firstBytes: ByteSequence, firstCharsIfText: CharSequence?): FileType? {
        if (firstCharsIfText == null) {
            return null
        }

        val lines = firstCharsIfText.lineSequence()
            .filter { !LINE_COMMENT.matches(it) }
            .toList()

        if (lines.isEmpty()) {
            return null
        }

        // first line must be a class mapping line
        val firstLine = lines.first()
        if (firstLine.matches(CLASS_MAPPING_LINE)) {
            return ProGuardFileType
        }

        return null
    }

    private companion object {
        private val LINE_COMMENT = Regex("^\\s*#.*$")
        @Language("RegExp")
        private const val JAVA_IDENTIFIER_REGEX =
            // Initial name (either class or first package identifier)
            "\\p{javaJavaIdentifierStart}\\p{javaUnicodeIdentifierPart}*" +
                // additional name parts (package and class name)
                "(\\.\\p{javaJavaIdentifierStart}\\p{javaUnicodeIdentifierPart}*)*"

        private val CLASS_MAPPING_LINE = Regex(
            // starts at beginning of the line
            "^" +
                JAVA_IDENTIFIER_REGEX +
                // Separator
                " -> " +
                JAVA_IDENTIFIER_REGEX +
                // ending can include a trailing comment
                ":\\s*(#.*)?$"
        )
    }
}
