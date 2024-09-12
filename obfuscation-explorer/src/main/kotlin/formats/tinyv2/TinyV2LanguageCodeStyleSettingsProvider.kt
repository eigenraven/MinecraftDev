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

package io.mcdev.obfex.formats.tinyv2

import io.mcdev.obfex.formats.tinyv2.lang.TinyV2Language
import io.mcdev.obfex.formats.util.TabCodeStyleSettingsProvider
import org.intellij.lang.annotations.Language

class TinyV2LanguageCodeStyleSettingsProvider : TabCodeStyleSettingsProvider() {
    override fun getLanguage() = TinyV2Language
    override fun getCodeSample(settingsType: SettingsType) = SAMPLE
}

@Language("TinyV2")
private const val SAMPLE = """
tiny	2	0	source	target	target2
c	class_1	class1Ns0Rename	class1Ns1Rename
	f	I	field_1	field1Ns0Rename	field1Ns1Rename
	m	()I	method_1	method1Ns0Rename	method1Ns1Rename
		p	1	param_1	param1Ns0Rename	param1Ns1Rename
		v	2	2	2	var_1	var1Ns0Rename	var1Ns1Rename
c	class_1${'$'}class_2	class1Ns0Rename${'$'}class2Ns0Rename	class1Ns1Rename${'$'}class2Ns1Rename
	f	I	field_2	field2Ns0Rename	field2Ns1Rename
c	class_3	class3Ns0Rename	class3Ns1Rename
"""
