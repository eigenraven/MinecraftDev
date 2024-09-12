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

package io.mcdev.obfex.lookup

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.util.Collections
import kotlin.reflect.KClass

/**
 * [MultiLookupTable] implementation based on hash tables.
 */
open class HashLookupTable<T> : MultiLookupTable<T> {

    private val store: ObjectLinkedOpenHashSet<T> = ObjectLinkedOpenHashSet()

    private val indices: MutableList<LookupIndex<T, *>> = mutableListOf()

    override fun seq(): Sequence<T> = store.asSequence()

    override fun list(): List<T> = Collections.unmodifiableList(store.toList())

    override val size: Int
        get() = store.size

    override fun <K> indexMulti(transformer: LookupIndexTransformer<T, K>): LookupIndex<T, K> {
        for (index in indices) {
            if (lookupIndexTransformer.get(index) === transformer) {
                @Suppress("UNCHECKED_CAST")
                return index as LookupIndex<T, K>
            }
        }

        val index = HashLookupIndex(this, transformer)
        rebuildIndex.invoke(index)
        indices += index

        return index
    }

    override fun add(value: T) {
        if (store.add(value)) {
            for (index in indices) {
                addToIndex.invoke(index, value)
            }
        }
    }

    override fun remove(value: T) {
        if (store.remove(value)) {
            for (index in indices) {
                removeFromIndex.invoke(index, value)
            }
        }
    }

    override fun <L : MultiLookupTable<*>> unwrap(type: KClass<L>): L? {
        if (type.isInstance(this)) {
            @Suppress("UNCHECKED_CAST")
            return this as L
        }
        return null
    }

    companion object {
        @JvmStatic
        private val lookupIndexTransformer =
            MethodHandles.privateLookupIn(HashLookupIndex::class.java, MethodHandles.lookup())
                .findVarHandle(HashLookupIndex::class.java, "transformer", Function1::class.java)

        @JvmStatic
        private val rebuildIndex =
            MethodHandles.privateLookupIn(HashLookupIndex::class.java, MethodHandles.lookup())
                .findVirtual(
                    HashLookupIndex::class.java,
                    "rebuild",
                    MethodType.methodType(Void::class.javaPrimitiveType)
                )

        @JvmStatic
        private val addToIndex =
            MethodHandles.privateLookupIn(HashLookupIndex::class.java, MethodHandles.lookup())
                .findVirtual(
                    HashLookupIndex::class.java,
                    "add",
                    MethodType.methodType(Void::class.javaPrimitiveType, Any::class.java)
                )

        @JvmStatic
        private val removeFromIndex =
            MethodHandles.privateLookupIn(HashLookupIndex::class.java, MethodHandles.lookup())
                .findVirtual(
                    HashLookupIndex::class.java,
                    "remove",
                    MethodType.methodType(Void::class.javaPrimitiveType, Any::class.java)
                )
    }
}
