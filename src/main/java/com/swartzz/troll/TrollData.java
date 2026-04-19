package com.swartzz.troll;

import lombok.Getter;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

@Getter
public class TrollData {

    private final UUID targetUUID;
    private final Set<TrollType> activeTrolls = EnumSet.noneOf(TrollType.class);

    public TrollData(UUID targetUUID) {
        this.targetUUID = targetUUID;
    }

    public boolean isActive(TrollType type) {
        return activeTrolls.contains(type);
    }

    public void enable(TrollType type) {
        activeTrolls.add(type);
    }

    public void disable(TrollType type) {
        activeTrolls.remove(type);
    }

    public void clear() {
        activeTrolls.clear();
    }
}