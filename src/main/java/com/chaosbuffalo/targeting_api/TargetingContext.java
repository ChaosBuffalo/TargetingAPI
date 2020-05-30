package com.chaosbuffalo.targeting_api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.function.BiFunction;

public class TargetingContext<T extends Entity> implements ITargetingContext {
    private boolean acceptSelf;
    private boolean requiresAlive;
    private boolean canBeSpectator;
    private boolean canBeCreative;
    private final Class<T> clazz;
    private BiFunction<Entity, Entity, Boolean> targetTest;

    protected boolean isValidClass(Entity target){
        return clazz.isInstance(target);
    }

    public TargetingContext(Class<T> clazz, boolean requiresAlive, boolean acceptSelf, boolean canBeCreative,
                            boolean canBeSpectator, BiFunction<Entity, Entity, Boolean> targetTest){
        this.clazz = clazz;
        this.requiresAlive = requiresAlive;
        this.acceptSelf = acceptSelf;
        this.canBeCreative = canBeCreative;
        this.canBeSpectator = canBeSpectator;
        this.targetTest = targetTest;
    }

    public TargetingContext(Class<T> clazz, BiFunction<Entity, Entity, Boolean> targetTest){
        this(clazz, true, targetTest);
    }

    public TargetingContext(TargetingContext<T> context){
        this(context.clazz, context.requiresAlive, context.acceptSelf, context.canBeCreative,
                context.canBeSpectator, context.targetTest);
    }

    public TargetingContext<T> setCanBeCreative(boolean canBeCreative) {
        this.canBeCreative = canBeCreative;
        return this;
    }

    public TargetingContext<T> setCanBeSpectator(boolean canBeSpectator) {
        this.canBeSpectator = canBeSpectator;
        return this;
    }

    public TargetingContext<T> setAcceptSelf(boolean acceptSelf) {
        this.acceptSelf = acceptSelf;
        return this;
    }

    public TargetingContext<T> setRequiresAlive(boolean requiresAlive) {
        this.requiresAlive = requiresAlive;
        return this;
    }

    public TargetingContext<T> setTargetTest(BiFunction<Entity, Entity, Boolean> targetTest) {
        this.targetTest = targetTest;
        return this;
    }

    public TargetingContext(Class<T> clazz, boolean acceptSelf, BiFunction<Entity, Entity, Boolean> targetTest){
        this(clazz, true, acceptSelf, false, false, targetTest);
    }

    @Override
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

        return targetTest.apply(caster, target);
    }
}
