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

@file:Suppress("MemberVisibilityCanBePrivate", "DuplicatedCode")

package io.mcdev.obfex.mappings

import com.intellij.util.containers.map2Array
import io.mcdev.obfex.Tristate
import io.mcdev.obfex.ref.FieldName
import io.mcdev.obfex.ref.LocalFieldRef
import io.mcdev.obfex.ref.LocalMethodRef
import io.mcdev.obfex.ref.LocalVarIndex
import io.mcdev.obfex.ref.LvtIndex
import io.mcdev.obfex.ref.MethodDescriptor
import io.mcdev.obfex.ref.MethodName
import io.mcdev.obfex.ref.ParamIndex
import io.mcdev.obfex.ref.ParamMap
import io.mcdev.obfex.ref.TypeDef
import io.mcdev.obfex.ref.asFieldRef
import io.mcdev.obfex.ref.asMethodRef
import io.mcdev.obfex.ref.asParamIndex
import io.mcdev.obfex.ref.asRef
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import java.lang.IllegalArgumentException
import java.util.ArrayList
import java.util.Collections

sealed interface MappingElement {
    val names: Array<String?>
    val location: MappingLocation
    val mappingSet: MappingSet

    fun name(index: Int): String? = names[index]
    fun name(ns: MappingNamespace): String? = name(index(ns))

    fun index(ns: MappingNamespace): Int = mappingSet.namespaceIndex(ns)
}

val MappingElement.unnamedFrom: String?
    get() = name(MappingNamespace.unnamedFrom(mappingSet))
val MappingElement.unnamedTo: String?
    get() = name(MappingNamespace.unnamedTo(mappingSet))

class PackageMappingElement(
    override val names: Array<String?>,
    override val mappingSet: MappingSet,
    override val location: MappingLocation,
) : MappingElement

class ClassMappingElement(
    override val names: Array<String?>,
    val parent: ClassMappingElement? = null,
    override val mappingSet: MappingSet,
    override val location: MappingLocation = UnknownLocation,
) : MappingElement {

    // classes generally have relatively few members (< 1000) so the naive approach for storing and querying them is
    // probably better
    private val fieldStore = ObjectArrayList<FieldMappingElement>()
    private val methodStore = ObjectArrayList<MethodMappingElement>()

    fun fullyQualifiedName(index: Int): String? = when (val p = parent) {
        is ClassMappingElement -> p.fullyQualifiedName(index) + "." + name(index)
        else -> name(index)
    }

    fun fullyQualifiedName(ns: MappingNamespace): String? = fullyQualifiedName(mappingSet.namespaceIndex(ns))

    // fields

    fun fields(): List<FieldMappingElement> {
        return Collections.unmodifiableList(fieldStore)
    }

    fun fields(ref: LocalFieldRef): List<FieldMappingElement> {
        if (ref.type == null) {
            return fieldStore.filter { it.names.contains(ref.name.name) }
        }

        // We don't know which namespace this field type is, so attempt to map for each namespace
        val mappedTypes = if (ref.type.isMappable()) {
            mappingSet.namespaces.map2Array { mappingSet.mapTypeFrom(it, ref.type) }
        } else {
            Array(mappingSet.namespaces.size) { ref.type }
        }

        return fieldStore.filter { mapping ->
            mappedTypes.withIndex().any { (i, type) ->
                mapping.name(i) == ref.name.name && type == mapping.type
            }
        }
    }

    fun fields(name: FieldName, type: TypeDef? = null): List<FieldMappingElement> =
        fields(name.asRef(type))

    fun fields(name: String, type: TypeDef? = null): List<FieldMappingElement> =
        fields(name.asFieldRef(type))

    fun fields(ns: MappingNamespace, ref: LocalFieldRef): List<FieldMappingElement> {
        if (ref.type == null) {
            return fieldStore.filter { it.name(ns) == ref.name.name }
        }

        val type = if (ns != mappingSet.typeNamespace && ref.type.isMappable()) {
            mappingSet.mapTypeFrom(ns, ref.type)
        } else {
            ref.type
        }

        return fieldStore.filter { it.name(ns) == ref.name.name && it.type == type }
    }

    fun fields(ns: MappingNamespace, name: FieldName, type: TypeDef? = null): List<FieldMappingElement> =
        fields(ns, name.asRef(type))

    fun fields(ns: MappingNamespace, name: String, type: TypeDef? = null): List<FieldMappingElement> =
        fields(ns, name.asFieldRef(type))

    fun field(ns: MappingNamespace, ref: LocalFieldRef): FieldMappingElement? =
        fields(ns, ref).firstOrNull()

    fun field(ns: MappingNamespace, name: FieldName, type: TypeDef? = null): FieldMappingElement? =
        field(ns, name.asRef(type))

    fun field(ns: MappingNamespace, name: String, type: TypeDef? = null): FieldMappingElement? =
        field(ns, name.asFieldRef(type))

    fun field(ref: LocalFieldRef): FieldMappingElement? {
        return mappingSet.namespaces.firstNotNullOfOrNull { ns -> field(ns, ref) }
    }

    fun field(name: FieldName, type: TypeDef? = null): FieldMappingElement? = field(name.asRef(type))
    fun field(name: String, type: TypeDef? = null): FieldMappingElement? = field(name.asFieldRef(type))

    fun addFieldMapping(
        names: Array<String?>,
        location: MappingLocation,
        type: TypeDef? = null,
        metadata: MemberMetadata = MemberMetadata.UNKNOWN,
    ): FieldMappingElement {
        val element = FieldMappingElement(names, this, type, metadata, mappingSet, location)
        fieldStore.add(element)
        return element
    }

    // methods

    fun methods(): List<MethodMappingElement> {
        return Collections.unmodifiableList(methodStore)
    }

    fun methods(ref: LocalMethodRef): List<MethodMappingElement> {
        if (ref.desc == null) {
            return methodStore.filter { it.names.contains(ref.name.name) }
        }

        // We don't know which namespace this field type is, so attempt to map for each namespace
        val mappedTypes = if (ref.desc.isMappable()) {
            mappingSet.namespaces.map2Array { mappingSet.mapTypeFrom(it, ref.desc) }
        } else {
            Array(mappingSet.namespaces.size) { ref.desc }
        }

        return methodStore.filter { mapping ->
            mappedTypes.withIndex().any { (i, type) ->
                mapping.name(i) == ref.name.name && type == mapping.descriptor
            }
        }
    }

    fun methods(name: MethodName, desc: MethodDescriptor? = null): List<MethodMappingElement> =
        methods(name.asRef(desc))

    fun methods(name: String, desc: MethodDescriptor? = null): List<MethodMappingElement> =
        methods(name.asMethodRef(desc))

    fun methods(ns: MappingNamespace, ref: LocalMethodRef): List<MethodMappingElement> {
        if (ref.desc == null) {
            return methodStore.filter { it.name(ns) == ref.name.name }
        }

        val desc = if (ns != mappingSet.typeNamespace) {
            mappingSet.mapTypeFrom(ns, ref.desc)
        } else {
            ref.desc
        }

        return methodStore.filter { it.descriptor == desc }
    }

    fun methods(
        ns: MappingNamespace,
        name: MethodName,
        desc: MethodDescriptor? = null
    ): List<MethodMappingElement> = methods(ns, name.asRef(desc))

    fun methods(
        ns: MappingNamespace,
        name: String,
        desc: MethodDescriptor? = null
    ): List<MethodMappingElement> = methods(ns, name.asMethodRef(desc))

    fun method(ns: MappingNamespace, ref: LocalMethodRef): MethodMappingElement? =
        methods(ns, ref).firstOrNull()

    fun method(ns: MappingNamespace, name: MethodName, desc: MethodDescriptor? = null): MethodMappingElement? =
        method(ns, name.asRef(desc))

    fun method(ns: MappingNamespace, name: String, desc: MethodDescriptor? = null): MethodMappingElement? =
        method(ns, name.asMethodRef(desc))

    fun method(ref: LocalMethodRef): MethodMappingElement? {
        return mappingSet.namespaces.firstNotNullOfOrNull { ns -> method(ns, ref) }
    }

    fun method(name: MethodName, desc: MethodDescriptor? = null): MethodMappingElement? =
        method(name.asRef(desc))

    fun method(name: String, desc: MethodDescriptor? = null): MethodMappingElement? =
        method(name.asMethodRef(desc))

    fun addMethodMapping(
        names: Array<String?>,
        location: MappingLocation,
        descriptor: MethodDescriptor,
        metadata: MemberMetadata = MemberMetadata.UNKNOWN,
    ): MethodMappingElement {
        val element = MethodMappingElement(names, this, descriptor, metadata, mappingSet, location)
        methodStore.add(element)
        return element
    }
}

class FieldMappingElement(
    override val names: Array<String?>,
    val containingClass: ClassMappingElement,
    val type: TypeDef? = null,
    val metadata: MemberMetadata = MemberMetadata.UNKNOWN,
    override val mappingSet: MappingSet = containingClass.mappingSet,
    override val location: MappingLocation = UnknownLocation,
) : MappingElement

class MethodMappingElement(
    override val names: Array<String?>,
    val containingClass: ClassMappingElement,
    val descriptor: MethodDescriptor,
    val metadata: MemberMetadata = MemberMetadata.UNKNOWN,
    override val mappingSet: MappingSet = containingClass.mappingSet,
    override val location: MappingLocation = UnknownLocation,
) : MappingElement {

    private val paramMappings = ParamMap<MethodParameterMappingElement>() // keys should be ParameterRef
    private val localVarMappings = ArrayList<LocalVariableMappingElement>() // keys should be LvtRef

    fun descriptor(ns: MappingNamespace): String =
        "(" + descriptor.params.joinToString("") { mappingSet.mapTypeTo(ns, it).descriptor } + ")" +
            mappingSet.mapTypeTo(ns, descriptor.returnType).descriptor

    fun params(): Collection<MethodParameterMappingElement> {
        return Collections.unmodifiableCollection(paramMappings.values)
    }

    fun param(index: ParamIndex): MethodParameterMappingElement? {
        return paramMappings[index]
    }

    fun param(index: Int): MethodParameterMappingElement? = param(index.asParamIndex())

    fun addParamMapping(
        names: Array<String?>,
        location: MappingLocation,
        paramIndex: ParamIndex,
        lvtIndex: LvtIndex = LvtIndex.UNKNOWN,
        type: TypeDef? = null,
    ): MethodParameterMappingElement {
        val existing = paramMappings[paramIndex]

        if (existing != null && location.priority <= existing.location.priority) {
            // existing takes precedence, so ignore
            return existing
        } else {
            val element = MethodParameterMappingElement(names, this, paramIndex, lvtIndex, type, mappingSet, location)
            paramMappings[paramIndex] = element
            return element
        }
    }

    fun localVars(): Collection<LocalVariableMappingElement> {
        return Collections.unmodifiableCollection(localVarMappings)
    }

    fun localVars(
        lvtIndex: LvtIndex = LvtIndex.UNKNOWN,
        localVarIndex: LocalVarIndex = LocalVarIndex.UNKNOWN
    ): List<LocalVariableMappingElement> {
        return localVarMappings.filter {
            // only filter by values which are known
            (!lvtIndex.isKnown || it.lvtIndex == lvtIndex) &&
                (!localVarIndex.index.isKnown || localVarIndex.index == it.localVarIndex.index) &&
                (!localVarIndex.localStart.isKnown || localVarIndex.localStart == it.localVarIndex.localStart) &&
                (!localVarIndex.localEnd.isKnown || localVarIndex.localEnd == it.localVarIndex.localEnd)
        }
    }

    fun localVars(index: LocalVarIndex): List<LocalVariableMappingElement> {
        return localVars(localVarIndex = index)
    }

    fun localVar(index: LocalVarIndex): LocalVariableMappingElement? {
        return localVars(index).firstOrNull()
    }

    fun localVars(index: LvtIndex): List<LocalVariableMappingElement> {
        return localVars(lvtIndex = index)
    }

    fun localVar(index: LvtIndex): LocalVariableMappingElement? {
        return localVars(index).firstOrNull()
    }

    fun addLocalVarMapping(
        names: Array<String?>,
        location: MappingLocation,
        localVarIndex: LocalVarIndex = LocalVarIndex.UNKNOWN,
        lvtIndex: LvtIndex = LvtIndex.UNKNOWN,
        type: TypeDef? = null,
    ): LocalVariableMappingElement {
        val existing = localVars(localVarIndex = localVarIndex, lvtIndex = lvtIndex)
        if (existing.isNotEmpty() && location.priority <= existing.first().location.priority) {
            // existing takes precedence, so ignore
            return existing.first()
        } else {
            val element = LocalVariableMappingElement(names, this, localVarIndex, lvtIndex, type, mappingSet, location)
            localVarMappings += element
            return element
        }
    }
}

class MethodParameterMappingElement(
    override val names: Array<String?>,
    val containingMethod: MethodMappingElement,
    val index: ParamIndex,
    val lvtIndex: LvtIndex = LvtIndex.UNKNOWN,
    val type: TypeDef? = null,
    override val mappingSet: MappingSet = containingMethod.mappingSet,
    override val location: MappingLocation = UnknownLocation,
) : MappingElement {
    fun hasLvtIndex() = lvtIndex.isKnown
}

data class MemberMetadata(
    val isStatic: Tristate,
    val isSynthetic: Tristate,
) {
    companion object {
        val UNKNOWN = MemberMetadata(Tristate.UNKNOWN, Tristate.UNKNOWN)
    }
}

class LocalVariableMappingElement(
    override val names: Array<String?>,
    val containingMethod: MethodMappingElement,
    val localVarIndex: LocalVarIndex,
    val lvtIndex: LvtIndex,
    val type: TypeDef? = null,
    override val mappingSet: MappingSet = containingMethod.mappingSet,
    override val location: MappingLocation = UnknownLocation,
) : MappingElement {
    init {
        if (!localVarIndex.isKnown && !lvtIndex.isKnown) {
            throw IllegalArgumentException("Cannot create LocalVariableMappingElement with unknown indices")
        }
    }
}
