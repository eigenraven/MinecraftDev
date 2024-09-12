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

package io.mcdev.obfex.formats.tinyv1.lang

import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.util.io.ByteSequence
import com.intellij.openapi.vfs.VirtualFile
import io.mcdev.obfex.formats.tinyv2.lang.TinyV2FileType

class TinyFileTypeDetector : FileTypeRegistry.FileTypeDetector {

    override fun detect(file: VirtualFile, firstBytes: ByteSequence, firstCharsIfText: CharSequence?): FileType? {
        if (firstCharsIfText == null) {
            return null
        }

        if (!file.name.endsWith(".tiny")) {
            return null
        }

        if (firstCharsIfText.startsWith("v1")) {
            return TinyV1FileType
        } else if (firstCharsIfText.startsWith("tiny\t2\t0")) {
            return TinyV2FileType
        }

        return null
    }
}
