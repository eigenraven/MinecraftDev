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

package com.demonwav.mcdev.mixintestdata.invalidInjectorMethodSignatureInspection;

public class MixedInOuter {
    public MixedInOuter() {
        this(method1());
        method2();
    }

    public MixedInOuter(String arg) {
    }

    private static String method1() {
        return null;
    }

    private static void method2() {
    }

    public class MixedInInner {
        public MixedInInner() {
        }

        public MixedInInner(String string) {
        }
    }

    public static class MixedInStaticInner {
        public MixedInStaticInner() {
        }

        public MixedInStaticInner(String string) {
        }
    }
}
