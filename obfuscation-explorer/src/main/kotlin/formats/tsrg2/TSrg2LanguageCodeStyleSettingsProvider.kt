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

package io.mcdev.obfex.formats.tsrg2

import io.mcdev.obfex.formats.tsrg2.lang.TSrg2Language
import io.mcdev.obfex.formats.util.TabCodeStyleSettingsProvider
import org.intellij.lang.annotations.Language

class TSrg2LanguageCodeStyleSettingsProvider : TabCodeStyleSettingsProvider() {
    override fun getLanguage() = TSrg2Language
    override fun getCodeSample(settingsType: SettingsType): String? {
        TODO("Not yet implemented")
    }
}

@Language("TSRGv2")
private const val SAMPLE = """
tsrg2 source target target2
class_1 class1Ns0Rename class1Ns1Rename
	field_1 I field1Ns0Rename field1Ns1Rename
	method_1 ()I method1Ns0Rename method1Ns1Rename
		1 param_1 param1Ns0Rename param1Ns1Rename
class_1${'$'}class_2 class1Ns0Rename$${'$'}class2Ns0Rename class1Ns1Rename$${'$'}class2Ns1Rename
	field_2 I field2Ns0Rename field2Ns1Rename
class_3 class3Ns0Rename class3Ns1Rename
"""
