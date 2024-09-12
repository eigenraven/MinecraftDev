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

package io.mcdev.obfex.formats.enigma

import io.mcdev.obfex.formats.enigma.lang.EnigmaLanguage
import io.mcdev.obfex.formats.util.TabCodeStyleSettingsProvider
import org.intellij.lang.annotations.Language

class EnigmaLanguageCodeStyleSettingsProvider : TabCodeStyleSettingsProvider() {
    override fun getLanguage() = EnigmaLanguage
    override fun getCodeSample(settingsType: SettingsType) = SAMPLE
}

@Language("Enigma")
private const val SAMPLE = """
CLASS net/minecraft/class_6489 net/minecraft/GameVersion
	COMMENT The game version interface used by Minecraft, replacing the javabridge
	COMMENT one's occurrences in Minecraft code.
	METHOD method_37912 getSaveVersion ()Lnet/minecraft/class_6595;
		COMMENT {@return the save version information for this game version}
	METHOD method_48017 getResourceVersion (Lnet/minecraft/class_3264;)I
		ARG 1 type
	METHOD method_48018 getId ()Ljava/lang/String;
	METHOD method_48019 getName ()Ljava/lang/String;
	METHOD method_48020 getProtocolVersion ()I
	METHOD method_48021 getBuildTime ()Ljava/util/Date;
	METHOD method_48022 isStable ()Z
"""
