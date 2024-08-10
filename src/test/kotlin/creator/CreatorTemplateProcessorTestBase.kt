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

package com.demonwav.mcdev.creator

import com.demonwav.mcdev.creator.custom.CreatorTemplateProcessor
import com.demonwav.mcdev.creator.custom.ExternalTemplatePropertyProvider
import com.demonwav.mcdev.creator.custom.TemplateDescriptor
import com.demonwav.mcdev.creator.custom.TemplateService
import com.demonwav.mcdev.creator.custom.TemplateValidationReporterImpl
import com.demonwav.mcdev.creator.custom.providers.LoadedTemplate
import com.google.gson.Gson
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.observable.properties.GraphProperty
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.testFramework.fixtures.BareTestFixture
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

class TestLoadedTemplate(descriptor: String) : LoadedTemplate {

    override val label: String = "Test template"

    override val tooltip: String? = null

    override val descriptor: TemplateDescriptor =
        Gson().fromJson<TemplateDescriptor>(descriptor, TemplateDescriptor::class.java)

    override val isValid: Boolean = true

    override fun loadTemplateContents(path: String): String? {
        return null
    }
}

class TestExternalTemplatePropertyProvider(propertyGraph: PropertyGraph) : ExternalTemplatePropertyProvider {

    override val projectNameProperty: GraphProperty<String> = propertyGraph.property("Test project")

    override val useGit: Boolean = false
}

abstract class CreatorTemplateProcessorTestBase {

    lateinit var fixture: BareTestFixture
    lateinit var propertyGraph: PropertyGraph
    lateinit var processor: CreatorTemplateProcessor

    @BeforeEach
    open fun setUp() {
        fixture = IdeaTestFixtureFactory.getFixtureFactory().createBareFixture()
        fixture.setUp()

        propertyGraph = PropertyGraph()
        val wizardContext = WizardContext(null, fixture.testRootDisposable)
        val scope = TemplateService.instance.scope("MinecraftDev Creator Test")
        val externalPropertyProvider = TestExternalTemplatePropertyProvider(propertyGraph)
        processor = CreatorTemplateProcessor(propertyGraph, wizardContext, scope, externalPropertyProvider)
        processor.initBuiltinProperties()
    }

    @AfterEach
    open fun tearDown() {
        fixture.tearDown()
    }

    fun makeTemplate(@Language("JSON") descriptor: String): TemplateValidationReporterImpl {
        val reporter = TemplateValidationReporterImpl()
        val loadedTemplate = TestLoadedTemplate(descriptor)
        processor.setupTemplate(loadedTemplate, reporter)
        return reporter
    }
}
