package com.swartzz.troll;

import com.cryptomorin.xseries.XSound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Villager;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class TrollManager {

    private final TrollPlugin plugin;
    private final Map<UUID, TrollData> playerData = new HashMap<>();
    private final Map<UUID, BukkitTask> soundTasks = new HashMap<>();
    private final Map<UUID, BukkitTask> tntTasks = new HashMap<>();
    private final Map<UUID, BukkitTask> hauntTasks = new HashMap<>();
    private final Map<UUID, UUID> hauntVillagers = new HashMap<>();
    private final Random random = new Random();

    private static final String[] AMBIENT_SOUNDS = {
            "ENTITY_ENDERMAN_AMBIENT",
            "ENTITY_GHAST_AMBIENT",
            "ENTITY_WITHER_AMBIENT",
            "AMBIENT_CAVE",
            "ENTITY_ELDER_GUARDIAN_AMBIENT",
            "ENTITY_WITCH_AMBIENT",
            "ENTITY_ZOMBIE_AMBIENT",
            "ENTITY_SKELETON_AMBIENT"
    };

    public TrollManager(TrollPlugin plugin) {
        this.plugin = plugin;
    }

    public TrollData getData(UUID uuid) {
        return playerData.computeIfAbsent(uuid, TrollData::new);
    }

    public boolean isActive(UUID uuid, TrollType type) {
        TrollData data = playerData.get(uuid);
        return data != null && data.isActive(type);
    }

    public void toggleTroll(Player target, TrollType type) {
        if (isActive(target.getUniqueId(), type)) {
            disableTroll(target, type);
        } else {
            enableTroll(target, type);
        }
    }

    public void enableTroll(Player target, TrollType type) {
        getData(target.getUniqueId()).enable(type);
        switch (type) {
            case SOUND -> startSoundTask(target);
            case TNT -> startTNTTask(target);
            case HAUNT -> startHauntTask(target);
            default -> {}
        }
    }

    public void disableTroll(Player target, TrollType type) {
        getData(target.getUniqueId()).disable(type);
        switch (type) {
            case SOUND -> cancelTask(soundTasks, target.getUniqueId());
            case TNT -> cancelTask(tntTasks, target.getUniqueId());
            case HAUNT -> {
                cancelTask(hauntTasks, target.getUniqueId());
                removeHauntVillager(target.getUniqueId());
            }
            default -> {}
        }
    }

    public void resetTrolls(Player target) {
        UUID uuid = target.getUniqueId();
        TrollData data = playerData.get(uuid);
        if (data == null) return;
        Set<TrollType> copy = EnumSet.copyOf(data.getActiveTrolls().isEmpty()
                ? EnumSet.noneOf(TrollType.class) : data.getActiveTrolls());
        for (TrollType type : copy) {
            disableTroll(target, type);
        }
        playerData.remove(uuid);
    }

    public void onPlayerQuit(Player player) {
        UUID uuid = player.getUniqueId();
        cancelTask(soundTasks, uuid);
        cancelTask(tntTasks, uuid);
        cancelTask(hauntTasks, uuid);
        removeHauntVillager(uuid);
    }

    public void onPlayerJoin(Player player) {
        TrollData data = playerData.get(player.getUniqueId());
        if (data == null || data.getActiveTrolls().isEmpty()) return;
        for (TrollType type : EnumSet.copyOf(data.getActiveTrolls())) {
            switch (type) {
                case SOUND -> startSoundTask(player);
                case TNT -> startTNTTask(player);
                case HAUNT -> startHauntTask(player);
                default -> {}
            }
        }
    }

    private void startSoundTask(Player target) {
        UUID uuid = target.getUniqueId();
        cancelTask(soundTasks, uuid);
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Player p = Bukkit.getPlayer(uuid);
            if (p == null || !p.isOnline()) {
                cancelTask(soundTasks, uuid);
                return;
            }
            String soundName = AMBIENT_SOUNDS[random.nextInt(AMBIENT_SOUNDS.length)];
            XSound.matchXSound(soundName).ifPresent(s -> s.play(p));
        }, 600L, 600L);
        soundTasks.put(uuid, task);
    }

    private void startTNTTask(Player target) {
        UUID uuid = target.getUniqueId();
        cancelTask(tntTasks, uuid);
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Player p = Bukkit.getPlayer(uuid);
            if (p == null || !p.isOnline()) {
                cancelTask(tntTasks, uuid);
                return;
            }
            Location spawnLoc = p.getLocation().clone().add(
                    random.nextInt(7) - 3,
                    3 + random.nextInt(3),
                    random.nextInt(7) - 3
            );
            TNTPrimed tnt = p.getWorld().spawn(spawnLoc, TNTPrimed.class);
            tnt.setFuseTicks(40);
            tnt.setMetadata("trollTNT", new FixedMetadataValue(plugin, true));
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (tnt.isValid()) tnt.remove();
            }, 38L);
        }, 200L, 200L);
        tntTasks.put(uuid, task);
    }

    private void startHauntTask(Player target) {
        UUID uuid = target.getUniqueId();
        cancelTask(hauntTasks, uuid);
        removeHauntVillager(uuid);

        Player p = Bukkit.getPlayer(uuid);
        if (p == null) return;

        Location spawnLoc = getRandomNearbyLocation(p.getLocation(), 8, 12);
        Villager villager = p.getWorld().spawn(spawnLoc, Villager.class, v -> {
            v.setSilent(true);
            v.setAI(false);
            v.setInvulnerable(true);
            v.setCustomNameVisible(false);
            v.setRemoveWhenFarAway(false);
        });
        hauntVillagers.put(uuid, villager.getUniqueId());

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Player pl = Bukkit.getPlayer(uuid);
            UUID villagerUID = hauntVillagers.get(uuid);
            if (pl == null || !pl.isOnline() || villagerUID == null) {
                cancelTask(hauntTasks, uuid);
                removeHauntVillager(uuid);
                return;
            }
            Entity v = Bukkit.getEntity(villagerUID);
            if (v == null || v.isDead()) {
                hauntVillagers.remove(uuid);
                cancelTask(hauntTasks, uuid);
                return;
            }
            if (isLookingAt(pl, v)) {
                Location newLoc = getRandomNearbyLocation(pl.getLocation(), 10, 15);
                v.teleport(newLoc);
            } else {
                double dist = v.getLocation().distance(pl.getLocation());
                if (dist > 6) {
                    Vector dir = pl.getLocation().getDirection().normalize();
                    Location behind = pl.getLocation().clone().subtract(dir.multiply(3));
                    behind.setY(pl.getLocation().getY());
                    v.teleport(behind);
                }
            }
        }, 10L, 10L);
        hauntTasks.put(uuid, task);
    }

    private boolean isLookingAt(Player player, Entity entity) {
        Vector toEntity = entity.getLocation().add(0, 1, 0).toVector()
                .subtract(player.getEyeLocation().toVector());
        if (toEntity.lengthSquared() == 0) return false;
        toEntity.normalize();
        double dot = toEntity.dot(player.getEyeLocation().getDirection());
        return dot > 0.95;
    }

    private Location getRandomNearbyLocation(Location base, int minDist, int maxDist) {
        double angle = random.nextDouble() * 2 * Math.PI;
        double dist = minDist + random.nextDouble() * (maxDist - minDist);
        double x = base.getX() + dist * Math.cos(angle);
        double z = base.getZ() + dist * Math.sin(angle);
        return new Location(base.getWorld(), x, base.getY(), z);
    }

    private void cancelTask(Map<UUID, BukkitTask> taskMap, UUID uuid) {
        BukkitTask task = taskMap.remove(uuid);
        if (task != null) task.cancel();
    }

    private void removeHauntVillager(UUID playerUUID) {
        UUID villagerUUID = hauntVillagers.remove(playerUUID);
        if (villagerUUID != null) {
            Entity entity = Bukkit.getEntity(villagerUUID);
            if (entity != null) entity.remove();
        }
    }

    public void cleanup() {
        soundTasks.values().forEach(BukkitTask::cancel);
        tntTasks.values().forEach(BukkitTask::cancel);
        hauntTasks.values().forEach(BukkitTask::cancel);
        soundTasks.clear();
        tntTasks.clear();
        hauntTasks.clear();
        hauntVillagers.forEach((playerUUID, villagerUUID) -> {
            Entity e = Bukkit.getEntity(villagerUUID);
            if (e != null) e.remove();
        });
        hauntVillagers.clear();
        playerData.clear();
    }
}