package com.swartzz.troll.inventory.impl;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.profiles.builder.XSkull;
import com.cryptomorin.xseries.profiles.objects.Profileable;
import com.swartzz.troll.TrollPlugin;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PlayerListGUI extends InventoryGUI {

    private final TrollPlugin plugin;
    private final int page;

    private static final int PAGE_SIZE = 45;

    public PlayerListGUI(TrollPlugin plugin, int page) {
        this.plugin = plugin;
        this.page = page;
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 54, ChatColor.DARK_RED + "Select a Player");
    }

    @Override
    public void decorate(Player viewer) {
        ItemStack filler = buildFiller();
        for (int i = PAGE_SIZE; i < 54; i++) {
            final ItemStack f = filler.clone();
            addButton(i, new InventoryButton().creator(p -> f).consumer(e -> {}));
        }

        List<Player> players = new ArrayList<>((Collection<? extends Player>) Bukkit.getOnlinePlayers());
        players.remove(viewer);

        int totalPages = (int) Math.ceil((double) players.size() / PAGE_SIZE);
        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, players.size());
        List<Player> pageSlice = start < players.size() ? players.subList(start, end) : new ArrayList<>();

        for (int i = 0; i < PAGE_SIZE; i++) {
            if (i < pageSlice.size()) {
                final Player target = pageSlice.get(i);
                addButton(i, new InventoryButton()
                        .creator(p -> buildPlayerHead(target))
                        .consumer(e -> {
                            Player clicker = (Player) e.getWhoClicked();
                            TrollGUI trollGUI = new TrollGUI(plugin, target);
                            plugin.getGuiManager().openGUI(trollGUI, clicker);
                        }));
            } else {
                addButton(i, new InventoryButton()
                        .creator(p -> new ItemStack(Material.AIR))
                        .consumer(e -> {}));
            }
        }

        if (page > 0) {
            ItemStack prev = buildArrow(ChatColor.YELLOW + "« Previous Page");
            addButton(45, new InventoryButton()
                    .creator(p -> prev)
                    .consumer(e -> plugin.getGuiManager()
                            .openGUI(new PlayerListGUI(plugin, page - 1), (Player) e.getWhoClicked())));
        }

        ItemStack close = buildClose();
        addButton(49, new InventoryButton()
                .creator(p -> close)
                .consumer(e -> e.getWhoClicked().closeInventory()));

        if (page < totalPages - 1) {
            ItemStack next = buildArrow(ChatColor.YELLOW + "Next Page »");
            addButton(53, new InventoryButton()
                    .creator(p -> next)
                    .consumer(e -> plugin.getGuiManager()
                            .openGUI(new PlayerListGUI(plugin, page + 1), (Player) e.getWhoClicked())));
        }

        super.decorate(viewer);
    }

    private ItemStack buildPlayerHead(Player player) {
        ItemStack skull = XSkull.createItem().profile(Profileable.detect(player.getName())).apply();
        ItemMeta meta = skull.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + player.getName());
            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + "Click to open troll menu!"
            ));
            skull.setItemMeta(meta);
        }
        return skull;
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

    private ItemStack buildArrow(String name) {
        ItemStack item = XMaterial.matchXMaterial("ARROW")
                .map(XMaterial::parseItem)
                .orElse(new ItemStack(Material.ARROW));
        if (item == null) item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
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