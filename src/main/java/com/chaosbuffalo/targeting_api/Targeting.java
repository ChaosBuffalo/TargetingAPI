package com.chaosbuffalo.targeting_api;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Team;

import java.util.ArrayList;
import java.util.function.BiFunction;

public class Targeting {


    private static final ArrayList<BiFunction<Entity, Entity, TargetRelation>> relationCallbacks =
            new ArrayList<>();

    public enum TargetRelation {
        FRIEND,
        ENEMY,
        NEUTRAL,
        UNHANDLED
    }

    public enum TargetType {
        ALL,
        ENEMY,
        FRIENDLY,
        PLAYERS,
        SELF,
        NEUTRAL,
        ENEMY_INCLUDE_NEUTRAL,
        FRIENDLY_INCLUDE_NEUTRAL
    }

    private static boolean areEntitiesEqual(Entity first, Entity second) {
        return first != null && second != null && first.getUniqueID().compareTo(second.getUniqueID()) == 0;
    }

    public static TargetRelation getTargetRelation(Entity source, Entity target){
        for (BiFunction<Entity, Entity, TargetRelation> func : relationCallbacks){
            TargetRelation result = func.apply(source, target);
            if (result != TargetRelation.UNHANDLED){
                return result;
            }
        }
        return TargetRelation.UNHANDLED;
    }

    public static void registerRelationCallback(BiFunction<Entity, Entity, TargetRelation> callback) {
        relationCallbacks.add(callback);
    }

    public static boolean isValidTarget(TargetType type, Entity caster, Entity target, boolean excludeCaster) {
        if (caster == null || target == null) {
            return false;
        }
        if (!(target instanceof LivingEntity)){
            return false;
        }
        if (excludeCaster && areEntitiesEqual(caster, target)) {
            return false;
        }
        // Targets should be alive
        if (!target.isAlive())
            return false;

        // Ignore spectators
        if (target.isSpectator())
            return false;

        // Ignore Creative Mode players
        if (target instanceof PlayerEntity && ((PlayerEntity) target).isCreative())
            return false;

        switch (type) {
            case ALL:
                return true;
            case SELF:
                return areEntitiesEqual(caster, target);
            case PLAYERS:
                return target instanceof PlayerEntity;
            case FRIENDLY:
                return isValidFriendly(caster, target);
            case ENEMY:
                return isValidEnemy(caster, target);
            case NEUTRAL:
                return isValidNeutral(caster, target);
            case ENEMY_INCLUDE_NEUTRAL:
                return isValidEnemy(caster, target) || isValidNeutral(caster, target);
            case FRIENDLY_INCLUDE_NEUTRAL:
                return isValidFriendly(caster, target) || isValidNeutral(caster, target);
        }
        return false;
    }


    private static boolean isSameTeam(Entity caster, Entity target) {
        Team myTeam = caster.getTeam();
        Team otherTeam = target.getTeam();
        return myTeam != null && otherTeam != null && myTeam.isSameTeam(otherTeam);
    }

    private static boolean isFriendlyPlayer(Entity caster, Entity target) {

        if (areEntitiesEqual(caster, target))
            return true;

        // Currently the only friendly player check is if the caster and target are on the same team
        if (isSameTeam(caster, target))
            return true;

        return false;
    }

    private static boolean isPlayerControlled(Entity target) {
        Entity controller = target.getControllingPassenger();
        if (controller instanceof PlayerEntity) {
            return true;
        }
        if (target instanceof TameableEntity) {
            TameableEntity ownable = (TameableEntity) target;
            if (ownable.getOwnerId() != null) {
                // Entity is owned, but the owner is offline
                // If the owner if offline then there's not much we can do.
                return true;
            }
        }
        return false;
    }

    private static boolean isFriendlyPlayerControlled(Entity caster, Entity target) {
        Entity controller = target.getControllingPassenger();
        if (controller instanceof PlayerEntity) {
            return isValidFriendly(caster, controller);
        }

        if (target instanceof TameableEntity) {
            TameableEntity ownable = (TameableEntity) target;

            Entity owner = ownable.getOwner();
            if (owner != null) {
                // Owner is online, perform the normal checks
                return isValidFriendly(caster, owner);
            } else if (ownable.getOwnerId() != null) {
                // Entity is owned, but the owner is offline
                // If the owner if offline then there's not much we can do.
                return false;
            }
        }

        return false;
    }

    private static boolean casterIsFriendlyPlayerControlled(Entity caster, Entity target){
        Entity controller = caster.getControllingPassenger();
        if (controller instanceof PlayerEntity) {
            return isValidFriendly(controller, target);
        }

        if (target instanceof TameableEntity) {
            TameableEntity ownable = (TameableEntity) target;
            Entity owner = ownable.getOwner();
            if (owner != null) {
                // Owner is online, perform the normal checks
                return isValidFriendly(owner, target);
            } else if (ownable.getOwnerId() != null) {
                // Entity is owned, but the owner is offline
                // If the owner if offline then there's not much we can do.
                // return true so that we consider everyone our friend
                return true;
            }
        }

        return false;
    }


    private static boolean isVanillaFriendly(Entity caster, Entity target){
        // Always friendly with ourselves
        if (areEntitiesEqual(caster, target)) {
            return true;
        }

        // Always friendly with entities on the same team
        if (isSameTeam(caster, target)) {
            return true;
        }

        if (isFriendlyPlayer(caster, target))
            return true;

        if (isFriendlyPlayerControlled(caster, target))
            return true;

        return casterIsFriendlyPlayerControlled(caster, target);
    }

    private static boolean isValidFriendly(Entity caster, Entity target) {

        if (isVanillaFriendly(caster, target)){
            return true;
        }


        TargetRelation relation = getTargetRelation(caster, target);
        if (relation == TargetRelation.FRIEND){
            return true;
        }

        return false;
    }

    private static boolean isValidEnemy(Entity caster, Entity target) {
        if (isVanillaFriendly(caster, target)){
            return false;
        }

        TargetRelation relation = getTargetRelation(caster, target);
        if (relation == TargetRelation.ENEMY){
            return true;
        }

        return false;
    }

    private static boolean isValidNeutral(Entity caster, Entity target){
        if (isVanillaFriendly(caster, target)){
            return false;
        }

        TargetRelation relation = getTargetRelation(caster, target);
        if (relation == TargetRelation.NEUTRAL || relation == TargetRelation.UNHANDLED){
            return true;
        }

        return false;
    }

}
