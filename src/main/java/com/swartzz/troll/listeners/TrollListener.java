package com.swartzz.troll.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
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
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TrollListener implements Listener {

    private final TrollPlugin plugin;

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

    private static final Set<String> GAPPLE_MATERIALS = new HashSet<>(Arrays.asList(
            "GOLDEN_APPLE", "ENCHANTED_GOLDEN_APPLE", "GOLDEN_CARROT"
    ));

    private static final Set<String> MEAT_MATERIALS = new HashSet<>(Arrays.asList(
            "PORK_CHOP", "COOKED_PORKCHOP",
            "BEEF", "COOKED_BEEF",
            "CHICKEN", "COOKED_CHICKEN",
            "MUTTON", "COOKED_MUTTON",
            "RABBIT", "COOKED_RABBIT",
            "COD", "COOKED_COD",
            "SALMON", "COOKED_SALMON",
            "TROPICAL_FISH", "PUFFERFISH",
            "ROTTEN_FLESH"
    ));

    private static final Set<String> SHIELD_MATERIALS = new HashSet<>(Arrays.asList(
            "SHIELD"
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

        if (event.getHand() != null && event.getHand() == EquipmentSlot.OFF_HAND) return;

        if (plugin.getTrollManager().isActive(player.getUniqueId(), TrollType.FLIP)) {
            Block block = event.getClickedBlock();
            if (block != null) {
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    if (player.isOnline()) {
                        Location loc = player.getLocation();
                        loc.setYaw(loc.getYaw() + 180f);
                        player.teleport(loc);
                    }
                }, 2L);
            }
        }

        if (plugin.getTrollManager().isActive(player.getUniqueId(), TrollType.SHIELD_DROP)) {
            ItemStack offhand = player.getInventory().getItemInOffHand();
            ItemStack mainhand = player.getInventory().getItemInMainHand();
            boolean offhandShield = !offhand.getType().isAir()
                    && XMaterial.matchXMaterial(offhand.getType()).name().equals("SHIELD");
            boolean mainhandShield = !mainhand.getType().isAir()
                    && XMaterial.matchXMaterial(mainhand.getType()).name().equals("SHIELD");
            if (offhandShield || mainhandShield) {
                switch (event.getAction()) {
                    case RIGHT_CLICK_AIR:
                    case RIGHT_CLICK_BLOCK: {
                        ItemStack shield = offhandShield ? offhand : mainhand;
                        if (offhandShield) {
                            player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
                        } else {
                            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                        }
                        player.getWorld().dropItemNaturally(player.getLocation(), shield);
                        player.updateInventory();
                        break;
                    }
                    default:
                        break;
                }
            }
        }
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

    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null || item.getType().isAir()) return;

        XMaterial xMat = XMaterial.matchXMaterial(item.getType());
        String matName = xMat.name();

        if (plugin.getTrollManager().isActive(player.getUniqueId(), TrollType.GAPPLE_TRAP)) {
            if (GAPPLE_MATERIALS.contains(matName)) {
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    if (!player.isOnline()) return;
                    XPotion.matchXPotion("BLINDNESS")
                            .map(xp -> xp.buildPotionEffect(200, 0))
                            .ifPresent(player::addPotionEffect);
                    XPotion.matchXPotion("WEAKNESS")
                            .map(xp -> xp.buildPotionEffect(200, 1))
                            .ifPresent(player::addPotionEffect);
                }, 1L);
            }
        }

        if (plugin.getTrollManager().isActive(player.getUniqueId(), TrollType.VEGETARIAN_CURSE)) {
            if (MEAT_MATERIALS.contains(matName)) {
                ItemStack kelpReplacement = XMaterial.matchXMaterial("DRIED_KELP")
                        .map(XMaterial::parseItem)
                        .orElse(null);
                if (kelpReplacement != null) {
                    event.setReplacement(kelpReplacement);
                }
            }
        }
    }
}