package com.chaosbuffalo.targeting_api;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid=TargetingAPI.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
public class TargetingRegistry {
    public static IForgeRegistry<TargetingContext> TARGET_CONTEXT_REGISTRY = null;

    @Nullable
    public static TargetingContext getTargetingContext(ResourceLocation name){
        return TARGET_CONTEXT_REGISTRY.getValue(name);
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void createRegistries(RegistryEvent.NewRegistry event) {
        TARGET_CONTEXT_REGISTRY = new RegistryBuilder<TargetingContext>()
                .setName(new ResourceLocation(TargetingAPI.MODID, "contexts"))
                .setType(TargetingContext.class)
                .setIDRange(0, Integer.MAX_VALUE - 1)
                .allowModification()
                .create();
    }
}