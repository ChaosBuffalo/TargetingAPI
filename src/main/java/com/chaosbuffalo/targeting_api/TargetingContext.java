package com.chaosbuffalo.targeting_api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.function.BiPredicate;

public class TargetingContext implements IForgeRegistryEntry<TargetingContext> {
    private boolean acceptSelf;
    private boolean requiresAlive;
    private boolean canBeSpectator;
    private boolean canBeCreative;
    private ResourceLocation name;
    private final Class<? extends Entity> clazz;
    private BiPredicate<Entity, Entity> targetTest;

    protected boolean isValidClass(Entity target){
        return clazz.isInstance(target);
    }

    protected <T extends Entity> TargetingContext(Class<T> clazz, ResourceLocation name, boolean requiresAlive,
                               boolean acceptSelf, boolean canBeCreative,
                             boolean canBeSpectator, BiPredicate<Entity, Entity> targetTest){
        setRegistryName(name);
        this.clazz = clazz;
        this.requiresAlive = requiresAlive;
        this.acceptSelf = acceptSelf;
        this.canBeCreative = canBeCreative;
        this.canBeSpectator = canBeSpectator;
        this.targetTest = targetTest;
    }

    public <T extends Entity> TargetingContext(Class<T> clazz, ResourceLocation name, BiPredicate<Entity, Entity> targetTest){
        this(clazz, name, true, targetTest);
    }


    public TargetingContext(TargetingContext context, ResourceLocation name){
        this(context.clazz, name, context.requiresAlive, context.acceptSelf, context.canBeCreative,
                context.canBeSpectator, context.targetTest);
    }

    public TargetingContext setCanBeCreative(boolean canBeCreative) {
        this.canBeCreative = canBeCreative;
        return this;
    }

    public TargetingContext setCanBeSpectator(boolean canBeSpectator) {
        this.canBeSpectator = canBeSpectator;
        return this;
    }

    public TargetingContext setAcceptSelf(boolean acceptSelf) {
        this.acceptSelf = acceptSelf;
        return this;
    }

    public TargetingContext setRequiresAlive(boolean requiresAlive) {
        this.requiresAlive = requiresAlive;
        return this;
    }

    public TargetingContext setTargetTest(BiPredicate<Entity, Entity> targetTest) {
        this.targetTest = targetTest;
        return this;
    }

    protected <T extends Entity> TargetingContext(Class<T> clazz, ResourceLocation name, boolean acceptSelf, BiPredicate<Entity, Entity> targetTest){
        this(clazz, name, true, acceptSelf, false, false, targetTest);
    }

    public boolean isValidTarget(Entity caster, Entity target) {
        if (caster == null || target == null) {
            return false;
        }
        if (!isValidClass(target)){
            return false;
        }
        if (!acceptSelf && Targeting.areEntitiesEqual(caster, target)){
            return false;
        }
        if (requiresAlive && !target.isAlive()){
            return false;
        }

        if (!canBeSpectator && target.isSpectator()){
            return false;
        }

        if (!canBeCreative && target instanceof PlayerEntity && ((PlayerEntity) target).isCreative()){
            return false;
        }

        return targetTest.test(caster, target);
    }

    @Override
    public TargetingContext setRegistryName(ResourceLocation name) {
        this.name = name;
        return this;
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return name;
    }

    @Override
    public Class<TargetingContext> getRegistryType() {
        return TargetingContext.class;
    }


}
