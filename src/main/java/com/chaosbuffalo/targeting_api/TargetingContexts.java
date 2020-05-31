package com.chaosbuffalo.targeting_api;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class TargetingContexts {

    public static final ResourceLocation ALL_NAME = new ResourceLocation(TargetingAPI.MODID, "contexts.all");
    public static final ResourceLocation ALL_AROUND_NAME = new ResourceLocation(TargetingAPI.MODID, "contexts.all_around");
    public static final ResourceLocation PLAYERS_NAME = new ResourceLocation(TargetingAPI.MODID, "contexts.players");
    public static final ResourceLocation PLAYERS_AROUND_NAME = new ResourceLocation(TargetingAPI.MODID, "contexts.players_around");
    public static final ResourceLocation SELF_NAME = new ResourceLocation(TargetingAPI.MODID, "contexts.self");
    public static final ResourceLocation FRIENDLY_NAME = new ResourceLocation(TargetingAPI.MODID, "contexts.friendly");
    public static final ResourceLocation FRIENDLY_AROUND_NAME = new ResourceLocation(TargetingAPI.MODID, "contexts.friendly_around");
    public static final ResourceLocation ENEMY_NAME = new ResourceLocation(TargetingAPI.MODID, "contexts.enemy");
    public static final ResourceLocation NEUTRAL_NAME = new ResourceLocation(TargetingAPI.MODID, "contexts.neutral");

    @ObjectHolder("targeting_api:contexts.all")
    public static TargetingContext ALL;

    @ObjectHolder("targeting_api:contexts.all_around")
    public static TargetingContext ALL_AROUND;

    @ObjectHolder("targeting_api:contexts.players")
    public static TargetingContext PLAYERS;

    @ObjectHolder("targeting_api:contexts.players_around")
    public static TargetingContext PLAYERS_AROUND;

    @ObjectHolder("targeting_api:contexts.self")
    public static TargetingContext SELF;

    @ObjectHolder("targeting_api:contexts.friendly")
    public static TargetingContext FRIENDLY;

    @ObjectHolder("targeting_api:contexts.friendly_around")
    public static TargetingContext FRIENDLY_AROUND;

    @ObjectHolder("targeting_api:contexts.enemy")
    public static TargetingContext ENEMY;

    @ObjectHolder("targeting_api:contexts.neutral")
    public static TargetingContext NEUTRAL;

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void registerContexts(RegistryEvent.Register<TargetingContext> event) {
        TargetingContext all = new TargetingContext(LivingEntity.class, ALL_NAME, (caster, target) -> true);
        event.getRegistry().register(all);
        event.getRegistry().register(new TargetingContext(all, ALL_AROUND_NAME).setAcceptSelf(false));
        event.getRegistry().register(new TargetingContext(LivingEntity.class, SELF_NAME, Targeting::areEntitiesEqual));
        TargetingContext players = new TargetingContext(PlayerEntity.class, PLAYERS_NAME, (caster, target) -> true);
        event.getRegistry().register(players);
        event.getRegistry().register(new TargetingContext(players, PLAYERS_AROUND_NAME).setAcceptSelf(false));
        TargetingContext friendly = new TargetingContext(LivingEntity.class, FRIENDLY_NAME, Targeting::isValidFriendly);
        event.getRegistry().register(friendly);
        event.getRegistry().register(new TargetingContext(friendly, FRIENDLY_AROUND_NAME).setAcceptSelf(false));
        event.getRegistry().register(new TargetingContext(LivingEntity.class, ENEMY_NAME, Targeting::isValidEnemy).setAcceptSelf(false));
        event.getRegistry().register(new TargetingContext(LivingEntity.class, NEUTRAL_NAME, Targeting::isValidNeutral).setAcceptSelf(false));
    }
}
