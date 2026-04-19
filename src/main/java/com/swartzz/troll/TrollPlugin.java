package com.swartzz.troll;

import com.swartzz.troll.commands.TrollCommand;
import com.swartzz.troll.inventory.gui.GUIListener;
import com.swartzz.troll.inventory.gui.GUIManager;
import com.swartzz.troll.listeners.TrollListener;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class TrollPlugin extends JavaPlugin {

    private GUIManager guiManager;
    private TrollManager trollManager;

    @Override
    public void onEnable() {
        this.guiManager = new GUIManager();
        this.trollManager = new TrollManager(this);

        getServer().getPluginManager().registerEvents(new GUIListener(guiManager), this);
        getServer().getPluginManager().registerEvents(new TrollListener(this), this);

        TrollCommand trollCommand = new TrollCommand(this);
        getCommand("troll").setExecutor(trollCommand);
        getCommand("troll").setTabCompleter(trollCommand);
    }

    @Override
    public void onDisable() {
        trollManager.cleanup();
    }
}