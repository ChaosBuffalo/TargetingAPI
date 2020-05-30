package com.chaosbuffalo.targeting_api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.function.BiFunction;

public class TargetingContext<T extends Entity> implements ITargetingContext {
    private final boolean excludeSelf;
    private final boolean requiresAlive;
    private final boolean canBeSpectator;
    private final boolean canBeCreative;
    private final Class<T> clazz;
    private final BiFunction<Entity, Entity, Boolean> targetTest;

    protected boolean isValidClass(Entity target){
        return clazz.isInstance(target);
    }

    public TargetingContext(Class<T> clazz, boolean requiresAlive, boolean excludeSelf, boolean canBeCreative,
                            boolean canBeSpectator, BiFunction<Entity, Entity, Boolean> targetTest){
        this.clazz = clazz;
        this.requiresAlive = requiresAlive;
        this.excludeSelf = excludeSelf;
        this.canBeCreative = canBeCreative;
        this.canBeSpectator = canBeSpectator;
        this.targetTest = targetTest;
    }

    public TargetingContext(Class<T> clazz, boolean excludeSelf, BiFunction<Entity, Entity, Boolean> targetTest){
        this(clazz, true, excludeSelf, false, false, targetTest);
    }

    @Override
    public boolean isValidTarget(Entity caster, Entity target) {
        if (caster == null || target == null) {
            return false;
        }
        if (!isValidClass(target)){
            return false;
        }
        if (excludeSelf && Targeting.areEntitiesEqual(caster, target)){
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
