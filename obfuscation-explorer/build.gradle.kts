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

import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    `mcdev-core`
    `mcdev-parsing`
    `mcdev-publishing`
}

val jflex by configurations
val jflexSkeleton by configurations
val grammarKit by configurations

group = "io.mcdev.obfex"

intellijPlatform {
    projectName = "Obfuscation Explorer"
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity(libs.versions.intellij.ide)

        plugin(libs.versions.psiPlugin.map { "PsiViewer:$it" })

        testFramework(TestFrameworkType.JUnit5)
        testFramework(TestFrameworkType.Platform)
    }
}

val generateSrgLexer by lexer("SrgLexer", "io/mcdev/obfex/formats/srg/gen")
val generateSrgParser by parser("SrgParser", "io/mcdev/obfex/formats/srg/gen")

val generateCSrgLexer by lexer("CSrgLexer", "io/mcdev/obfex/formats/csrg/gen")
val generateCSrgParser by parser("CSrgParser", "io/mcdev/obfex/formats/csrg/gen")

val generateTSrgLexer by lexer("TSrgLexer", "io/mcdev/obfex/formats/tsrg/gen")
val generateTSrgParser by parser("TSrgParser", "io/mcdev/obfex/formats/tsrg/gen")

val generateTSrg2Lexer by lexer("TSrg2Lexer", "io/mcdev/obfex/formats/tsrg2/gen")
val generateTSrg2Parser by parser("TSrg2Parser", "io/mcdev/obfex/formats/tsrg2/gen")

val generateJamLexer by lexer("JamLexer", "io/mcdev/obfex/formats/jam/gen")
val generateJamParser by parser("JamParser", "io/mcdev/obfex/formats/jam/gen")

val generateEnigmaLexer by lexer("EnigmaLexer", "io/mcdev/obfex/formats/enigma/gen")
val generateEnigmaParser by parser("EnigmaParser", "io/mcdev/obfex/formats/enigma/gen")

val generateTinyV1Lexer by lexer("TinyV1Lexer", "io/mcdev/obfex/formats/tinyv1/gen")
val generateTinyV1Parser by parser("TinyV1Parser", "io/mcdev/obfex/formats/tinyv1/gen")

val generateTinyV2Lexer by lexer("TinyV2Lexer", "io/mcdev/obfex/formats/tinyv2/gen")
val generateTinyV2Parser by parser("TinyV2Parser", "io/mcdev/obfex/formats/tinyv2/gen")

val generateProGuardLexer by lexer("ProGuardLexer", "io/mcdev/obfex/formats/proguard/gen")
val generateProGuardParser by parser("ProGuardParser", "io/mcdev/obfex/formats/proguard/gen")

val generate by tasks.registering {
    group = "minecraft"
    description = "Generates sources needed to compile the plugin."
    outputs.dir(layout.buildDirectory.dir("gen"))
    dependsOn(
        generateSrgLexer,
        generateSrgParser,
        generateCSrgLexer,
        generateCSrgParser,
        generateTSrgLexer,
        generateTSrgParser,
        generateTSrg2Lexer,
        generateTSrg2Parser,
        generateJamLexer,
        generateJamParser,
        generateEnigmaLexer,
        generateEnigmaParser,
        generateTinyV1Lexer,
        generateTinyV1Parser,
        generateTinyV2Lexer,
        generateTinyV2Parser,
        generateProGuardLexer,
        generateProGuardParser,
    )
}

sourceSets.main { java.srcDir(generate) }

// Remove gen directory on clean
tasks.clean { delete(generate) }

license {
    tasks {
        register("gradle") {
            files.from(
                fileTree(project.projectDir) {
                    include("*.gradle.kts", "gradle.properties")
                    exclude("**/buildSrc/**", "**/build/**")
                }
            )
        }
        register("grammars") {
            files.from(project.fileTree("src/main/grammars"))
        }
    }
}
