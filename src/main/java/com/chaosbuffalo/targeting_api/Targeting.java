package com.chaosbuffalo.targeting_api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.EntityPredicates;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.BiFunction;

public class Targeting {

    private static final List<BiFunction<Entity, Entity, TargetRelation>> relationCallbacks = new ArrayList<>();

    public enum TargetRelation {
        FRIEND,
        ENEMY,
        NEUTRAL,
        UNHANDLED
    }

    public static boolean areEntitiesEqual(Entity first, Entity second) {
        return first != null && second != null && first.getUniqueID().compareTo(second.getUniqueID()) == 0;
    }

    public static TargetRelation getTargetRelation(Entity source, Entity target) {
        // can't be enemy with self
        if (areEntitiesEqual(source, target)) {
            return TargetRelation.FRIEND;
        }

        if (!EntityPredicates.CAN_AI_TARGET.test(target))
        {
            return TargetRelation.UNHANDLED;
        }
        // can't be enemy with entities on same team
        if (source.isOnSameTeam(target)) {
            return TargetRelation.FRIEND;
        }

        for (BiFunction<Entity, Entity, TargetRelation> func : relationCallbacks) {
            TargetRelation result = func.apply(source, target);
            if (result != TargetRelation.UNHANDLED) {
                return result;
            }
        }
        return TargetRelation.UNHANDLED;
    }

    public static void registerRelationCallback(BiFunction<Entity, Entity, TargetRelation> callback) {
        relationCallbacks.add(callback);
    }


    public static boolean isValidTarget(TargetingContext context, Entity caster, Entity target) {
        return context.isValidTarget(caster, target);
    }

    private static Entity getRootEntity(Entity source) {
        Entity controller = source.getControllingPassenger();
        if (controller != null) {
            return getRootEntity(controller);
        }

        if (source instanceof TameableEntity) {
            TameableEntity owned = (TameableEntity) source;
            Entity owner = owned.getOwner();
            if (owner != null) {
                // Owner is online, so use it for relationship checks
                return getRootEntity(owner);
            } else if (owned.getOwnerId() != null) {
                // Entity is owned, but the owner is offline
                // If the owner if offline then there's not much we can do.
                return source;
            }
        }

        return source;
    }

    static boolean validCheck(Entity caster, Entity target, EnumSet<TargetRelation> relations) {
        Entity casterRoot = getRootEntity(caster);
        Entity targetRoot = getRootEntity(target);

        TargetRelation relation = getTargetRelation(casterRoot, targetRoot);
        return relations.contains(relation);
    }

    public static boolean allowAny(Entity caster, Entity target) {
        return true;
    }

    public static boolean isValidFriendly(Entity caster, Entity target) {
        return validCheck(caster, target, EnumSet.of(TargetRelation.FRIEND));
    }

    public static boolean isValidEnemy(Entity caster, Entity target) {
        return validCheck(caster, target, EnumSet.of(TargetRelation.ENEMY));
    }

    public static boolean isValidNeutral(Entity caster, Entity target) {
        return validCheck(caster, target, EnumSet.of(TargetRelation.NEUTRAL, TargetRelation.UNHANDLED));
    }
}
