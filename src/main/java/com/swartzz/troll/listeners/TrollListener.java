package com.swartzz.troll.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.swartzz.troll.TrollPlugin;
import com.swartzz.troll.TrollType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TrollListener implements Listener {

    private final TrollPlugin plugin;

    private static final Set<String> DOOR_MATERIALS = new HashSet<>(Arrays.asList(
            "OAK_DOOR", "SPRUCE_DOOR", "BIRCH_DOOR", "JUNGLE_DOOR",
            "ACACIA_DOOR", "DARK_OAK_DOOR", "MANGROVE_DOOR", "CHERRY_DOOR",
            "BAMBOO_DOOR", "CRIMSON_DOOR", "WARPED_DOOR", "IRON_DOOR"
    ));

    private static final Set<String> ORE_MATERIALS = new HashSet<>(Arrays.asList(
            "COAL_ORE", "DEEPSLATE_COAL_ORE",
            "IRON_ORE", "DEEPSLATE_IRON_ORE",
            "GOLD_ORE", "DEEPSLATE_GOLD_ORE", "NETHER_GOLD_ORE",
            "DIAMOND_ORE", "DEEPSLATE_DIAMOND_ORE",
            "EMERALD_ORE", "DEEPSLATE_EMERALD_ORE",
            "LAPIS_ORE", "DEEPSLATE_LAPIS_ORE",
            "REDSTONE_ORE", "DEEPSLATE_REDSTONE_ORE",
            "NETHER_QUARTZ_ORE", "ANCIENT_DEBRIS",
            "COPPER_ORE", "DEEPSLATE_COPPER_ORE"
    ));

    public TrollListener(TrollPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getTrollManager().onPlayerJoin(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getTrollManager().onPlayerQuit(event.getPlayer());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getTrollManager().isActive(player.getUniqueId(), TrollType.FLIP)) return;
        Block block = event.getClickedBlock();
        if (block == null) return;
        XMaterial xMat = XMaterial.matchXMaterial(block.getType());
        if (!DOOR_MATERIALS.contains(xMat.name())) return;
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                Location loc = player.getLocation();
                loc.setYaw(loc.getYaw() + 180f);
                player.teleport(loc);
            }
        }, 2L);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getTrollManager().isActive(player.getUniqueId(), TrollType.LUCK)) return;
        Block block = event.getBlock();
        XMaterial xMat = XMaterial.matchXMaterial(block.getType());
        if (!ORE_MATERIALS.contains(xMat.name())) return;
        event.setDropItems(false);
        ItemStack poisonPotato = XMaterial.matchXMaterial("POISONOUS_POTATO")
                .map(XMaterial::parseItem)
                .orElse(null);
        if (poisonPotato != null) {
            block.getWorld().dropItemNaturally(block.getLocation(), poisonPotato);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        if (!plugin.getTrollManager().isActive(player.getUniqueId(), TrollType.STICKY)) return;
        String title = event.getView().getTitle();
        if (title.contains("Trolling:") || title.equals("\u00A74Select a Player")) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity().hasMetadata("trollTNT")) {
            event.setCancelled(true);
        }
    }
}