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

@file:Suppress("MemberVisibilityCanBePrivate")

package io.mcdev.obfex.mappings

import com.intellij.util.containers.map2Array
import io.mcdev.obfex.mappings.MappingNamespace.Companion.UNNAMED_FROM
import io.mcdev.obfex.mappings.MappingNamespace.Companion.UNNAMED_TO
import io.mcdev.obfex.ref.ClassName
import io.mcdev.obfex.ref.FieldName
import io.mcdev.obfex.ref.LinePriority
import io.mcdev.obfex.ref.LocalMemberRef
import io.mcdev.obfex.ref.LocalMethodRef
import io.mcdev.obfex.ref.LocalVarIndex
import io.mcdev.obfex.ref.LocalVarName
import io.mcdev.obfex.ref.LvtIndex
import io.mcdev.obfex.ref.MemberName
import io.mcdev.obfex.ref.MethodDescriptor
import io.mcdev.obfex.ref.MethodName
import io.mcdev.obfex.ref.PackName
import io.mcdev.obfex.ref.ParamIndex
import io.mcdev.obfex.ref.ParamName
import io.mcdev.obfex.ref.TypeDef
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap

interface MappingSetBuilderCore {
    fun pack(line: LinePriority? = null, col: Int = 1): PackageMappingBuilder
    fun clazz(line: LinePriority? = null, col: Int = 1): ClassMappingBuilder

    fun clazz(ref: NS<ClassName>, line: LinePriority? = null, col: Int = 1): ClassMappingBuilder
}

class MappingSetBuilder(
    val issues: MappingIssuesRegistry,
    vararg namespaces: String,
) : MappingSetBuilderCore, MappingIssuesRegistry by issues {

    val namespaces: Array<String> = if (namespaces.isEmpty()) {
        arrayOf(UNNAMED_FROM, UNNAMED_TO)
    } else {
        // Kotlin wants `namespaces` to be Array<out String>
        @Suppress("UNCHECKED_CAST")
        namespaces as Array<String>
    }

    val packageMappings = ArrayList<PackageMappingBuilder>()
    val classMappings = ArrayList<ClassMappingBuilder>()

    // mapping files often keep mappings lines for the same class together, so this is an easy way to optimize that case
    private var lastClassMapping: ClassMappingBuilder? = null

    override fun pack(line: LinePriority?, col: Int): PackageMappingBuilder {
        return PackageMappingBuilder(namespaces, this, FileCoords(line, col)).also { packageMappings += it }
    }

    override fun clazz(line: LinePriority?, col: Int): ClassMappingBuilder {
        return ClassMappingBuilder(namespaces, this, FileCoords(line, col)).also { classMappings += it }
    }

    override fun clazz(ref: NS<ClassName>, line: LinePriority?, col: Int): ClassMappingBuilder {
        if (lastClassMapping?.names?.get(ref.ns) == ref.v.name) {
            return lastClassMapping!!
        }
        val res =
            classMappings.firstOrNull { it.names[ref.ns] == ref.v.name }
                ?: ClassMappingBuilder(namespaces, this, FileCoords(line, col)).also {
                    classMappings += it
                    it.with(ref)
                }
        lastClassMapping = res
        return res
    }

    fun build(): MappingSet {
        val set = MappingSet(namespaces.asIterable())

        packageMappings.forEach { it.build(set, issues) }
        classMappings.forEach { it.build(set, issues) }

        return set
    }
}

class PackageMappingBuilder(
    namespaces: Array<String>,
    val parent: MappingSetBuilder,
    var coords: FileCoords,
) : NamingBuilder<PackName>(namespaces) {

    override fun addName(ref: NS<String>) {
        names[ref.ns] = ref.v
    }

    fun build(set: MappingSet, issues: MappingIssuesRegistry) {
        val n = names.map2Array { name ->
            if (name == null) {
                issues.warning("Package mapping found without target name", coords)
            }
            name?.takeIf { it.isNotBlank() }
        }
        if (n.all { it == null }) {
            issues.error("Package mapping found with no target names", coords)
        }

        set.addPackageMapping(n, coords)
    }
}

class ClassMappingBuilder(
    namespaces: Array<String>,
    val parent: MappingSetBuilder,
    var coords: FileCoords,
) : NamingBuilder<ClassName>(namespaces), MappingIssuesRegistry by parent {

    val fieldMappings = ArrayList<FieldMappingBuilder>()
    val methodMappings = ArrayList<MethodMappingBuilder>()

    private var lastMethodMapping: MethodMappingBuilder? = null

    val set: MappingSetBuilder
        get() = parent

    fun field(line: LinePriority? = null, col: Int = 1): FieldMappingBuilder {
        return FieldMappingBuilder(namespaces, this, FileCoords(line, col)).also { fieldMappings += it }
    }

    fun method(line: LinePriority? = null, col: Int = 1): MethodMappingBuilder {
        return MethodMappingBuilder(namespaces, this, FileCoords(line, col)).also { methodMappings += it }
    }

    fun method(ref: NS<LocalMethodRef>, line: LinePriority? = null, col: Int = 1): MethodMappingBuilder {
        if (lastMethodMapping?.names?.get(ref.ns) == ref.v.name.name && lastMethodMapping?.desc == ref.v.desc) {
            return lastMethodMapping!!
        }
        val res =
            methodMappings.firstOrNull { it.names[ref.ns] == ref.v.name.name }
                ?: MethodMappingBuilder(namespaces, this, FileCoords(line, col)).also {
                    methodMappings += it
                    it.desc = ref.v.desc
                    it.with(ref.withValue { name })
                }
        lastMethodMapping = res
        return res
    }

    override fun addName(ref: NS<String>) {
        names[ref.ns] = ref.v
    }

    fun build(set: MappingSet, issues: MappingIssuesRegistry, parentElement: ClassMappingElement? = null) {
        val n = names.map2Array { name ->
            if (name == null) {
                issues.warning("Class mapping found without target name", coords)
            }
            name?.takeIf { it.isNotBlank() }
        }
        if (n.all { it == null }) {
            issues.error("Class mapping found with no target names", coords)
        }

        val element = set.addClassMapping(n, parentElement, coords)

        fieldMappings.forEach { it.build(issues, element) }
        methodMappings.forEach { it.build(issues, element) }
    }
}

class FieldMappingBuilder(
    namespaces: Array<String>,
    val parent: ClassMappingBuilder,
    var coords: FileCoords,
) : NamingBuilder<FieldName>(namespaces), MappingIssuesRegistry by parent {

    var type: TypeDef? = null

    var meta: MemberMetadata = MemberMetadata.UNKNOWN

    override fun addName(ref: NS<String>) {
        names[ref.ns] = ref.v
    }

    fun build(issues: MappingIssuesRegistry, parent: ClassMappingElement) {
        val n = names.map2Array { name ->
            if (name == null) {
                issues.warning("Field mapping found without target name", coords)
            }
            name?.takeIf { it.isNotBlank() }
        }
        if (n.all { it == null }) {
            issues.error("Field mapping found with no target names", coords)
        }

        parent.addFieldMapping(n, coords, type, meta)
    }
}

class MethodMappingBuilder(
    namespaces: Array<String>,
    val parent: ClassMappingBuilder,
    var coords: FileCoords,
) : NamingBuilder<MethodName>(namespaces), MappingIssuesRegistry by parent {

    var desc: MethodDescriptor? = null

    val paramMappings = Int2ObjectLinkedOpenHashMap<ParamMappingBuilder>()
    val localVarMappings = ArrayList<LocalVarMappingBuilder>()

    var meta: MemberMetadata = MemberMetadata.UNKNOWN

    fun param(index: ParamIndex, line: LinePriority? = null, col: Int = 1): ParamMappingBuilder {
        return paramMappings.compIfAbsent(index.index) {
            ParamMappingBuilder(namespaces, this, index, FileCoords(line, col))
        }
    }

    // tiny-v2 is the only format that I know of that supports locals, it only requires a local var index, but I could
    // see other formats might have different requirements, so this stays as flexible as possible, not requiring any
    fun localVar(
        localVarIndex: LocalVarIndex = LocalVarIndex.UNKNOWN,
        lvtIndex: LvtIndex = LvtIndex.UNKNOWN,
        line: LinePriority? = null,
        col: Int = 1
    ): LocalVarMappingBuilder {
        return localVarMappings.firstOrNull { mapping ->
            mapping.localVarIndex == localVarIndex && mapping.lvtIndex == lvtIndex
        } ?: LocalVarMappingBuilder(namespaces, this, localVarIndex, lvtIndex, FileCoords(line, col))
            .also { localVarMappings += it }
    }

    override fun addName(ref: NS<String>) {
        names[ref.ns] = ref.v
    }

    fun build(issues: MappingIssuesRegistry, parent: ClassMappingElement) {
        val n = names.map2Array { name ->
            if (name == null) {
                issues.warning("Method mapping found without target name", coords)
            }
            name?.takeIf { it.isNotBlank() }
        }
        if (n.all { it == null }) {
            issues.error("Method mapping found with no target names", coords)
        }

        val desc = this.desc ?: return issues.error("Method mappings must have a descriptor", coords)

        val element = parent.addMethodMapping(n, coords, desc, meta)

        paramMappings.values.forEach { it.build(issues, element) }
        localVarMappings.forEach { it.build(issues, element) }
    }
}

class ParamMappingBuilder(
    namespaces: Array<String>,
    val parent: MethodMappingBuilder,
    val index: ParamIndex,
    var coords: FileCoords,
) : NamingBuilder<ParamName>(namespaces), MappingIssuesRegistry by parent {

    var lvtRef: LvtIndex? = LvtIndex.UNKNOWN

    var type: TypeDef? = null

    override fun addName(ref: NS<String>) {
        names[ref.ns] = ref.v
    }

    fun build(issues: MappingIssuesRegistry, parent: MethodMappingElement) {
        val n = names.map2Array { name -> name?.takeIf { it.isNotBlank() } }
        if (n.all { it == null }) {
            issues.error("Method parameter mapping found with no target names", coords)
        }

        parent.addParamMapping(n, coords, index, lvtRef ?: LvtIndex.UNKNOWN, type)
    }
}

class LocalVarMappingBuilder(
    namespaces: Array<String>,
    val parent: MethodMappingBuilder,
    var localVarIndex: LocalVarIndex,
    var lvtIndex: LvtIndex,
    var coords: FileCoords,
) : NamingBuilder<LocalVarName>(namespaces), MappingIssuesRegistry by parent {

    var type: TypeDef? = null

    override fun addName(ref: NS<String>) {
        names[ref.ns] = ref.v
    }

    fun build(issues: MappingIssuesRegistry, parent: MethodMappingElement) {
        if (!localVarIndex.isKnown && !lvtIndex.isKnown) {
            return issues.error("Method local var mapping found without known any known indices", coords)
        }

        val n = names.map2Array { name -> name?.takeIf { it.isNotBlank() } }
        if (n.all { it == null }) {
            issues.error("Method local var mapping found with no target names", coords)
        }

        parent.addLocalVarMapping(n, coords, localVarIndex, lvtIndex, type)
    }
}

abstract class NamingBuilder<T : MemberName>(val namespaces: Array<String>) {

    val names: Array<String?> = arrayOfNulls(namespaces.size)

    operator fun set(ns: String, name: T) {
        set(namespaces.indexOf(ns), name)
    }

    operator fun set(index: Int, name: T) {
        with(NS(name, index))
    }

    fun with(ref: NS<T>) {
        addName(ref.forName())
    }

    fun unlessExists(ref: NS<T>) {
        if (names[ref.ns] != null) {
            return
        }
        addName(ref.forName())
    }

    protected abstract fun addName(ref: NS<String>)
}

// as in "NameSpace"
data class NS<V : Any>(val v: V, val ns: Int) {
    fun <T : Any> withValue(newV: T): NS<T> = NS(newV, ns)
    inline fun <T : Any> withValue(block: V.() -> T): NS<T> = NS(v.block(), ns)
}
fun NS<out MemberName>.forName(): NS<String> = NS(v.name, ns)

fun <T : MemberName> T.ns(ns: Int): NS<T> = NS(this, ns)
fun <T : LocalMemberRef<*>> T.ns(ns: Int): NS<T> = NS(this, ns)

// helpers

private fun <M, V> M.compIfAbsent(key: Int, func: Int2ObjectFunction<V>): V
    where M : Int2ObjectLinkedOpenHashMap<V> = computeIfAbsent(key, func)

inline fun MappingSetBuilderCore.pack(
    line: LinePriority? = null,
    col: Int = 1,
    conf: PackageMappingBuilder.() -> Unit,
) = pack(line, col).apply(conf)

inline fun MappingSetBuilderCore.clazz(
    line: LinePriority? = null,
    col: Int = 1,
    conf: ClassMappingBuilder.() -> Unit,
) = clazz(line, col).apply(conf)

inline fun MappingSetBuilderCore.clazz(
    ref: NS<ClassName>,
    line: LinePriority? = null,
    col: Int = 1,
    conf: ClassMappingBuilder.() -> Unit,
) = clazz(ref, line, col).apply(conf)

inline fun ClassMappingBuilder.field(
    line: LinePriority? = null,
    col: Int = 1,
    conf: FieldMappingBuilder.() -> Unit,
) = field(line, col).apply(conf)

inline fun ClassMappingBuilder.method(
    line: LinePriority? = null,
    col: Int = 1,
    conf: MethodMappingBuilder.() -> Unit,
) = method(line, col).apply(conf)

inline fun MethodMappingBuilder.param(
    index: ParamIndex,
    line: LinePriority? = null,
    col: Int = 1,
    conf: ParamMappingBuilder.() -> Unit,
) = param(index, line, col).apply(conf)

inline fun MethodMappingBuilder.localVar(
    localVarIndex: LocalVarIndex = LocalVarIndex.UNKNOWN,
    lvtIndex: LvtIndex = LvtIndex.UNKNOWN,
    line: LinePriority? = null,
    col: Int = 1,
    conf: LocalVarMappingBuilder.() -> Unit,
) = localVar(localVarIndex, lvtIndex, line, col).apply(conf)
