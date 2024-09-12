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

import java.io.ByteArrayOutputStream
import javax.inject.Inject
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity

@CacheableTask
abstract class ParserExec : JavaExec() {

    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val sourceFile: RegularFileProperty

    @get:Classpath
    abstract val grammarKit: ConfigurableFileCollection

    @get:Internal
    abstract val destinationRootDirectory: DirectoryProperty

    @get:OutputDirectory
    abstract val psiDirectory: DirectoryProperty

    @get:OutputDirectory
    abstract val parserDirectory: DirectoryProperty

    @get:Internal
    abstract val logFile: RegularFileProperty

    @get:Inject
    abstract val fs: FileSystemOperations

    init {
        mainClass.set("org.intellij.grammar.Main")

        @Suppress("LeakingThis")
        jvmArgs(
            "--add-opens", "java.base/java.lang=ALL-UNNAMED",
            "--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED",
            "--add-opens", "java.base/java.util=ALL-UNNAMED"
        )
    }

    override fun exec() {
        classpath = grammarKit
        args(
            destinationRootDirectory.get().asFile,
            sourceFile.get().asFile
        )

        fs.delete { delete(psiDirectory, parserDirectory) }

        val taskOutput = ByteArrayOutputStream()
        standardOutput = taskOutput
        errorOutput = taskOutput

        super.exec()

        val log = logFile.get().asFile
        log.parentFile.mkdirs()
        log.writeBytes(taskOutput.toByteArray())
    }
}
