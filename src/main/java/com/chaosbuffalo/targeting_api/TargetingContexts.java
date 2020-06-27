package com.chaosbuffalo.targeting_api;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class TargetingContexts {
    public static TargetingContext ALL = TargetingContext.Builder.create(LivingEntity.class)
            .setTargetTest(Targeting::allowAny)
            .build();
    public static TargetingContext ALL_AROUND = TargetingContext.Builder.from(ALL)
            .canTargetCaster(false)
            .build();
    public static TargetingContext PLAYERS = TargetingContext.Builder.create(PlayerEntity.class)
            .setTargetTest(Targeting::allowAny)
            .build();
    public static TargetingContext PLAYERS_AROUND = TargetingContext.Builder.from(PLAYERS)
            .canTargetCaster(false)
            .build();
    public static TargetingContext SELF = TargetingContext.Builder.create(LivingEntity.class)
            .setTargetTest(Targeting::areEntitiesEqual)
            .build();
    public static TargetingContext FRIENDLY = TargetingContext.Builder.create(LivingEntity.class)
            .setTargetTest(Targeting::isValidFriendly)
            .build();
    public static TargetingContext FRIENDLY_AROUND = TargetingContext.Builder.from(FRIENDLY)
            .canTargetCaster(false)
            .build();
    public static TargetingContext ENEMY = TargetingContext.Builder.create(LivingEntity.class)
            .canTargetCaster(false)
            .setTargetTest(Targeting::isValidEnemy)
            .build();
    public static TargetingContext NEUTRAL = TargetingContext.Builder.create(LivingEntity.class)
            .canTargetCaster(false)
            .setTargetTest(Targeting::isValidNeutral)
            .build();
}
