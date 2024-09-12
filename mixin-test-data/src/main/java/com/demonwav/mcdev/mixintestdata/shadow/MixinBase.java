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

package com.demonwav.mcdev.mixintestdata.shadow;

public class MixinBase {
    private MixinBase() {}

    // Static
    private static final String privateStaticFinalString = "";
    private static String privateStaticString = "";

    protected static final String protectedStaticFinalString = "";
    protected static String protectedStaticString = "";

    static final String packagePrivateStaticFinalString = "";
    static String packagePrivateStaticString = "";

    public static final String publicStaticFinalString = "";
    public static String publicStaticString = "";

    // Non-static
    private final String privateFinalString = "";
    private String privateString = "";

    protected final String protectedFinalString = "";
    protected String protectedString = "";

    final String packagePrivateFinalString = "";
    String packagePrivateString = "";

    public final String publicFinalString = "";
    public String publicString = "";

    // Bad shadows
    protected String wrongAccessor = "";
    protected final String noFinal = "";

    public final String twoIssues = "";

    private static String privateStaticMethod() {
        return null;
    }

    private String privateMethod() {
        return null;
    }
}
