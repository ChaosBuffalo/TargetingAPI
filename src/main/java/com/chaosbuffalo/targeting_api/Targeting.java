package com.chaosbuffalo.targeting_api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.EntityPredicates;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.function.BiFunction;

public class Targeting {

    private static final List<TargetRelationCallback> relationCallbacks = new ArrayList<>();

    protected static class TargetRelationCallback {
        BiFunction<Entity, Entity, TargetRelation> func;
        int priority;

        TargetRelationCallback(BiFunction<Entity, Entity, TargetRelation> func, int priority) {
            this.func = func;
            this.priority = priority;
        }

        public int getPriority() {
            return priority;
        }
    }

    public enum TargetRelation {
        FRIEND,
        ENEMY,
        NEUTRAL,
        UNHANDLED
    }

    public static boolean areEntitiesEqual(Entity first, Entity second) {
        return first != null && second != null && first.getUniqueID().compareTo(second.getUniqueID()) == 0;
    }

    static TargetRelation defaultRelationCheck(Entity source, Entity target) {
        return target.getClassification(false).getPeacefulCreature() ?
                Targeting.TargetRelation.FRIEND :
                Targeting.TargetRelation.ENEMY;
    }

    public static TargetRelation getTargetRelation(Entity source, Entity target) {
        // can't be enemy with self
        //need to handle null
        if (source == null || target == null) {
            return TargetRelation.NEUTRAL;
        }
        if (areEntitiesEqual(source, target)) {
            return TargetRelation.FRIEND;
        }

        if (!EntityPredicates.CAN_AI_TARGET.test(target)) {
            return TargetRelation.UNHANDLED;
        }
        // can't be enemy with entities on same team
        if (source.isOnSameTeam(target)) {
            return TargetRelation.FRIEND;
        }

        if (relationCallbacks.size() > 0) {
            for (TargetRelationCallback func : relationCallbacks) {
                TargetRelation result = func.func.apply(source, target);
                if (result != TargetRelation.UNHANDLED) {
                    return result;
                }
            }
        } else {
            return defaultRelationCheck(source, target);
        }
        return TargetRelation.UNHANDLED;
    }

    public static void registerRelationCallback(BiFunction<Entity, Entity, TargetRelation> callback) {
        relationCallbacks.add(new TargetRelationCallback(callback, 10));
        relationCallbacks.sort(Comparator.comparingInt(TargetRelationCallback::getPriority));
    }

    public static void registerRelationCallback(BiFunction<Entity, Entity, TargetRelation> callback, int priority) {
        relationCallbacks.add(new TargetRelationCallback(callback, priority));
        relationCallbacks.sort(Comparator.comparingInt(TargetRelationCallback::getPriority));
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
