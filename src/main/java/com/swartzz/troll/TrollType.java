package com.swartzz.troll;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum TrollType {

    FLIP("Flip", "OAK_DOOR", Arrays.asList(
            "&7Rotates the player &f180°",
            "&7when walking through doors."
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
            "&7the player and &fdisappears",
            "&7when looked at."
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