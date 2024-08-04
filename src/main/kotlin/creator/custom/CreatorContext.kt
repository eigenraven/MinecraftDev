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

package com.demonwav.mcdev.creator.custom

import com.demonwav.mcdev.creator.custom.types.CreatorProperty
import com.demonwav.mcdev.creator.modalityState
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.application.EDT
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.application.asContextElement
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.platform.util.coroutines.namedChildScope
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

data class CreatorContext(
    val graph: PropertyGraph,
    val properties: Map<String, CreatorProperty<*>>,
    val wizardContext: WizardContext,
    val scope: CoroutineScope
) {
    val modalityState: ModalityState
        get() = wizardContext.modalityState

    val coroutineContext: CoroutineContext
        get() = modalityState.asContextElement()

    /**
     * The CoroutineContext to use when a change has to be made to the creator UI
     */
    val uiContext: CoroutineContext
        get() = Dispatchers.EDT + coroutineContext

    /**
     * A general purpose scope dependent of the main creator scope, cancelled when the creator is closed.
     */
    fun childScope(name: String): CoroutineScope = scope.namedChildScope(name)
}
