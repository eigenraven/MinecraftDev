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

package io.mcdev.obfex.formats.tsrg

import io.mcdev.obfex.formats.tsrg.lang.TSrgLanguage
import io.mcdev.obfex.formats.util.TabCodeStyleSettingsProvider
import org.intellij.lang.annotations.Language

class TSrgLanguageCodeStyleSettingsProvider : TabCodeStyleSettingsProvider() {
    override fun getLanguage() = TSrgLanguage
    override fun getCodeSample(settingsType: SettingsType) = SAMPLE
}

@Language("TSRG")
private const val SAMPLE = """
class_1 class1Ns0Rename
	field_1 field1Ns0Rename
	method_1 ()I method1Ns0Rename
class_1${'$'}class_2 class1Ns0Rename${'$'}class2Ns0Rename
	field_2 field2Ns0Rename
class_3 class3Ns0Rename
"""
