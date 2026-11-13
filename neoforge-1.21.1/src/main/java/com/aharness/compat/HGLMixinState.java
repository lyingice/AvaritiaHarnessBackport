package com.aharness.compat;

/**
 * Separate state holder for the Happy Ghast Legacy speed mixin. Mixin classes
 * may not declare non-private static fields and external classes must not live
 * in the mixin package, so the test/log flag lives here.
 */
public final class HGLMixinState {
    public static boolean speedApplied;

    private HGLMixinState() {
    }
}
