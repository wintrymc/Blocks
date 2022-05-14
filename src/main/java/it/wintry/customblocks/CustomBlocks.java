package it.wintry.customblocks;

import it.wintry.customblocks.commands.CustomBlocksCommand;
import it.wintry.customblocks.listeners.BlockPlaceBreakListener;
import it.wintry.customblocks.listeners.InteractDismountListener;
import it.wintry.customblocks.manager.BlocksManager;
import it.wintry.customblocks.sql.SQLManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class CustomBlocks extends JavaPlugin {

    private SQLManager sql;
    private BlocksManager manager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        sql = new SQLManager(this);
        manager = new BlocksManager(this);
        getServer().getPluginManager().registerEvents(new InteractDismountListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceBreakListener(this), this);
        getCommand("customblocks").setExecutor(new CustomBlocksCommand(this));
    }

    @Override
    public void onDisable() {
        sql.saveChairs();
        sql.onDisable();
    }
}
