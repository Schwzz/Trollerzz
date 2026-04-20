package com.swartzz.troll;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum TrollType {

    FLIP("Flip", "COMPASS", Arrays.asList(
            "&7Flips the player's view &f180°",
            "&7whenever they interact with any block."
    )),
    TNT("TNT Rain", "TNT", Arrays.asList(
            "&7Primed TNT spawns near",
            "&7the player periodically.",
            "&7It vanishes before exploding."
    )),
    LUCK("Bad Luck", "POISONOUS_POTATO", Arrays.asList(
            "&7Ores drop &fpoisonous potatoes",
            "&7instead of normal drops."
    )),
    STICKY("Sticky Fingers", "SLIME_BALL", Arrays.asList(
            "&7Items cannot be moved",
            "&7in the player's inventory."
    )),
    SOUND("Haunted Sounds", "NOTE_BLOCK", Arrays.asList(
            "&7Random ambient sounds",
            "&7play every &f30 seconds&7."
    )),
    HAUNT("Haunt", "VILLAGER_SPAWN_EGG", Arrays.asList(
            "&7A stalker villager follows",
            "&7the player, plays a &fscream",
            "&7and vanishes when looked at."
    )),
    HOTBAR_SHUFFLE("Hotbar Shuffle", "CHEST", Arrays.asList(
            "&7Randomly rearranges",
            "&7the player's hotbar",
            "&7every &f5 seconds&7."
    )),
    SHIELD_DROP("Shield Drop", "SHIELD", Arrays.asList(
            "&7Forces the player to drop",
            "&7their shield the moment",
            "&7they try to block with it."
    )),
    GAPPLE_TRAP("Gapple Trap", "GOLDEN_APPLE", Arrays.asList(
            "&7Applies &fBlindness &7and &fWeakness",
            "&7when the player eats a",
            "&7Golden Apple or Golden Carrot."
    )),
    VEGETARIAN_CURSE("Vegetarian Curse", "DRIED_KELP", Arrays.asList(
            "&7Replaces any meat the player",
            "&7eats with &fDried Kelp&7",
            "&7the moment they finish eating."
    ));

    private final String displayName;
    private final String materialName;
    private final List<String> description;

    TrollType(String displayName, String materialName, List<String> description) {
        this.displayName = displayName;
        this.materialName = materialName;
        this.description = description;
    }
}