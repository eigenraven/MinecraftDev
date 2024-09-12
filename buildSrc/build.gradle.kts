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

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(8)
}
tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    // hack for version catalogs
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(libs.gson)
    implementation(libs.kotlin.plugin)
    implementation(libs.intellij.plugin)
    implementation(libs.licenser.plugin)
    implementation(libs.changelog.plugin)
}
