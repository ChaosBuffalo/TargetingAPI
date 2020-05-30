package com.chaosbuffalo.targeting_api;

import net.minecraft.entity.Entity;

public interface ITargetingContext {

    boolean isValidTarget(Entity caster, Entity target);

}
