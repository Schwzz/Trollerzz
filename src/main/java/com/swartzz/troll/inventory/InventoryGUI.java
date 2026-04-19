package com.swartzz.troll.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public abstract class InventoryGUI implements InventoryHandler {

    private Inventory inventory;
    private final Map<Integer, InventoryButton> buttonMap = new HashMap<>();

    public Inventory getInventory() {
        if (this.inventory == null) {
            this.inventory = createInventory();
        }
        return this.inventory;
    }

    public void addButton(int slot, InventoryButton button) {
        this.buttonMap.put(slot, button);
    }

    public void decorate(Player player) {
        buttonMap.forEach((slot, button) -> {
            if (button.getIconCreator() != null) {
                ItemStack icon = button.getIconCreator().apply(player);
                getInventory().setItem(slot, icon);
            }
        });
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getClickedInventory() == null) return;
        if (!event.getClickedInventory().equals(getInventory())) return;
        int slot = event.getSlot();
        InventoryButton button = buttonMap.get(slot);
        if (button != null && button.getEventConsumer() != null) {
            button.getEventConsumer().accept(event);
        }
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        decorate((Player) event.getPlayer());
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
    }

    protected abstract Inventory createInventory();
}