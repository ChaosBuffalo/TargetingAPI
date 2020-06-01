package com.chaosbuffalo.targeting_api;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class TargetingContexts {
    public static TargetingContext ALL = new TargetingContext(LivingEntity.class, (caster, target) -> true);;
    public static TargetingContext ALL_AROUND = new TargetingContext(ALL).setAcceptSelf(false);
    public static TargetingContext PLAYERS = new TargetingContext(PlayerEntity.class, (caster, target) -> true);
    public static TargetingContext PLAYERS_AROUND = new TargetingContext(PLAYERS).setAcceptSelf(false);
    public static TargetingContext SELF = new TargetingContext(LivingEntity.class, Targeting::areEntitiesEqual);
    public static TargetingContext FRIENDLY = new TargetingContext(LivingEntity.class, Targeting::isValidFriendly);
    public static TargetingContext FRIENDLY_AROUND = new TargetingContext(FRIENDLY).setAcceptSelf(false);
    public static TargetingContext ENEMY = new TargetingContext(LivingEntity.class, Targeting::isValidEnemy).setAcceptSelf(false);
    public static TargetingContext NEUTRAL = new TargetingContext(LivingEntity.class, Targeting::isValidNeutral).setAcceptSelf(false);
}
