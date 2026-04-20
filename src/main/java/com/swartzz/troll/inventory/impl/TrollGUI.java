package com.swartzz.troll.inventory.impl;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.swartzz.troll.TrollPlugin;
import com.swartzz.troll.TrollType;
import com.swartzz.troll.inventory.InventoryButton;
import com.swartzz.troll.inventory.InventoryGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class TrollGUI extends InventoryGUI {

    private final TrollPlugin plugin;
    private final Player target;

    private static final int[] TROLL_SLOTS = {
            10, 11, 12, 13, 14,
            19, 20, 21, 22, 23
    };

    public TrollGUI(TrollPlugin plugin, Player target) {
        this.plugin = plugin;
        this.target = target;
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 45,
                ChatColor.DARK_RED + "Trolling: " + ChatColor.RED + target.getName());
    }

    @Override
    public void decorate(Player viewer) {
        ItemStack filler = buildFiller();
        for (int i = 0; i < 45; i++) {
            final ItemStack f = filler.clone();
            addButton(i, new InventoryButton()
                    .creator(p -> f)
                    .consumer(e -> {}));
        }

        TrollType[] types = TrollType.values();
        for (int i = 0; i < types.length && i < TROLL_SLOTS.length; i++) {
            final TrollType type = types[i];
            addButton(TROLL_SLOTS[i], new InventoryButton()
                    .creator(p -> buildTrollItem(type))
                    .consumer(e -> {
                        Player clicker = (Player) e.getWhoClicked();
                        plugin.getTrollManager().toggleTroll(target, type);
                        decorate(clicker);
                    }));
        }

        addButton(40, new InventoryButton()
                .creator(p -> buildClose())
                .consumer(e -> e.getWhoClicked().closeInventory()));

        super.decorate(viewer);
    }

    private ItemStack buildTrollItem(TrollType type) {
        boolean active = plugin.getTrollManager().isActive(target.getUniqueId(), type);
        ItemStack item = XMaterial.matchXMaterial(type.getMaterialName())
                .map(XMaterial::parseItem)
                .orElse(new ItemStack(Material.STONE));
        if (item == null) item = new ItemStack(Material.STONE);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        ChatColor nameColor = active ? ChatColor.GREEN : ChatColor.RED;
        meta.setDisplayName(nameColor + "" + ChatColor.BOLD + type.getDisplayName());

        List<String> lore = new ArrayList<>();
        for (String line : type.getDescription()) {
            lore.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        lore.add("");
        lore.add(ChatColor.GRAY + "Status: " + (active
                ? ChatColor.GREEN + "Enabled"
                : ChatColor.RED + "Disabled"));
        lore.add((active ? ChatColor.RED : ChatColor.GREEN) + "» Click to "
                + (active ? "disable" : "enable") + "!");
        meta.setLore(lore);

        if (active) {
            XEnchantment.matchXEnchantment("UNBREAKING")
                    .ifPresent(e -> meta.addEnchant(e.getEnchant(), 1, true));
        }

        item.setItemMeta(meta);
        return item;
    }

    private ItemStack buildFiller() {
        ItemStack item = XMaterial.matchXMaterial("GRAY_STAINED_GLASS_PANE")
                .map(XMaterial::parseItem)
                .orElse(new ItemStack(Material.GLASS_PANE));
        if (item == null) item = new ItemStack(Material.GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack buildClose() {
        ItemStack item = XMaterial.matchXMaterial("BARRIER")
                .map(XMaterial::parseItem)
                .orElse(new ItemStack(Material.BARRIER));
        if (item == null) item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Close");
            item.setItemMeta(meta);
        }
        return item;
    }
}