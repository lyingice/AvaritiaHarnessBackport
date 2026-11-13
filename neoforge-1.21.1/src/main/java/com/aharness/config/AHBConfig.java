package com.aharness.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class AHBConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.DoubleValue SHIELD_RADIUS;
    public static final ModConfigSpec.IntValue SHIELD_DURATION_SECONDS;
    public static final ModConfigSpec.IntValue SHIELD_COOLDOWN_SECONDS;
    public static final ModConfigSpec.DoubleValue GHAST_DAMAGE_REDUCTION;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        SHIELD_RADIUS = builder.comment("Radius of the harness shield that protects friendly and neutral entities")
                .defineInRange("harness_shield_radius", 4.0D, 1.0D, 32.0D);
        SHIELD_DURATION_SECONDS = builder.comment("How long the harness shield lasts")
                .defineInRange("harness_shield_duration_seconds", 15, 1, 120);
        SHIELD_COOLDOWN_SECONDS = builder.comment("Cooldown of the harness shield")
                .defineInRange("harness_shield_cooldown_seconds", 20, 0, 600);
        GHAST_DAMAGE_REDUCTION = builder.comment("Damage reduction granted to a happy ghast wearing the neutron harness")
                .defineInRange("harness_ghast_damage_reduction", 0.8D, 0.0D, 1.0D);
        SPEC = builder.build();
    }

    private AHBConfig() {
    }
}
