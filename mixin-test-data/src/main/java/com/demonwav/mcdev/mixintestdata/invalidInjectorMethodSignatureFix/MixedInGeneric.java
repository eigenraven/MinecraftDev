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

package com.demonwav.mcdev.mixintestdata.invalidInjectorMethodSignatureFix;

import java.util.List;
import java.util.Map;

public class MixedInGeneric {
    public GenericOneParam<String> genericMethod(
            String noGenerics,
            GenericOneParam<String> oneParam,
            GenericTwoParams<String, Integer> twoParams,
            GenericOneParam<GenericOneParam<String>> nestedParam,
            Map<String, List<Map.Entry<String, Map<Integer, int[]>>>[]> pleaseJava
    ) {
        return null;
    }

    public Map.Entry<String, List<Map.Entry<String, Map<Integer, int[]>>>[]> returnComplex() {
        return null;
    }
}
