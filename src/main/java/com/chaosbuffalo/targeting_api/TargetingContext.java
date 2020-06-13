package com.chaosbuffalo.targeting_api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.function.BiPredicate;

public class TargetingContext {
    private boolean acceptSelf;
    private boolean requiresAlive;
    private boolean canBeSpectator;
    private boolean canBeCreative;
    private final Class<? extends Entity> clazz;
    private BiPredicate<Entity, Entity> targetTest;

    protected boolean isValidClass(Entity target) {
        return clazz.isInstance(target);
    }

    protected <T extends Entity> TargetingContext(Class<T> clazz, boolean requiresAlive,
                                                  boolean acceptSelf, boolean canBeCreative,
                                                  boolean canBeSpectator, BiPredicate<Entity, Entity> targetTest) {
        this.clazz = clazz;
        this.requiresAlive = requiresAlive;
        this.acceptSelf = acceptSelf;
        this.canBeCreative = canBeCreative;
        this.canBeSpectator = canBeSpectator;
        this.targetTest = targetTest;
    }

    public <T extends Entity> TargetingContext(Class<T> clazz, BiPredicate<Entity, Entity> targetTest) {
        this(clazz, true, targetTest);
    }


    public TargetingContext(TargetingContext context) {
        this(context.clazz, context.requiresAlive, context.acceptSelf, context.canBeCreative,
                context.canBeSpectator, context.targetTest);
    }

    public TargetingContext setCanBeCreative(boolean canBeCreative) {
        this.canBeCreative = canBeCreative;
        return this;
    }

    public boolean canBeCreative() {
        return canBeCreative;
    }

    public TargetingContext setCanBeSpectator(boolean canBeSpectator) {
        this.canBeSpectator = canBeSpectator;
        return this;
    }

    public boolean canBeSpectator() {
        return canBeSpectator;
    }

    public TargetingContext setAcceptSelf(boolean acceptSelf) {
        this.acceptSelf = acceptSelf;
        return this;
    }

    public boolean canTargetSelf() {
        return acceptSelf;
    }

    public TargetingContext setRequiresAlive(boolean requiresAlive) {
        this.requiresAlive = requiresAlive;
        return this;
    }

    public TargetingContext setTargetTest(BiPredicate<Entity, Entity> targetTest) {
        this.targetTest = targetTest;
        return this;
    }

    protected <T extends Entity> TargetingContext(Class<T> clazz, boolean acceptSelf, BiPredicate<Entity, Entity> targetTest) {
        this(clazz, true, acceptSelf, false, false, targetTest);
    }

    public boolean isValidTarget(Entity caster, Entity target) {
        if (caster == null || target == null) {
            return false;
        }

        if (!isValidClass(target)) {
            return false;
        }

        if (!acceptSelf && Targeting.areEntitiesEqual(caster, target)) {
            return false;
        }

        if (requiresAlive && !target.isAlive()) {
            return false;
        }

        if (!canBeSpectator && target.isSpectator()) {
            return false;
        }

        if (!canBeCreative && target instanceof PlayerEntity && ((PlayerEntity) target).isCreative()) {
            return false;
        }

        return targetTest.test(caster, target);
    }
}
