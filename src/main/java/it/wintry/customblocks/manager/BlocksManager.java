package it.wintry.customblocks.manager;

import com.google.common.collect.Maps;
import it.wintry.customblocks.CustomBlocks;
import it.wintry.customblocks.objects.CustomBlock;
import it.wintry.customblocks.objects.CustomChair;
import it.wintry.customblocks.objects.CustomFurniture;
import lombok.Getter;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;

import java.util.Map;
import java.util.Optional;

@Getter
public class BlocksManager {

    private final CustomBlocks plugin;
    private final Map<String, CustomBlock> cachedBlocks = Maps.newConcurrentMap();
    private final Map<String, CustomFurniture> cachedFurniture = Maps.newConcurrentMap();
    private final Map<Location, CustomChair> cachedChairs;

    public BlocksManager(CustomBlocks plugin) {
        this.plugin = plugin;
        this.cachedChairs = plugin.getSql().getChairs();
        load();
    }

    private void load() {
        for (String blockName : plugin.getConfig().getConfigurationSection("blocks").getKeys(false)) {
            CustomBlock block = new CustomBlock(plugin.getConfig().getString("blocks." + blockName + ".name"),
                    Instrument.valueOf(plugin.getConfig().getString("blocks." + blockName + ".instrument").toUpperCase()),
                    new Note(plugin.getConfig().getInt("blocks." + blockName + ".note")),
                    plugin.getConfig().getInt("blocks." + blockName + ".model-data"));

            cachedBlocks.put(plugin.getConfig().getString("blocks." + blockName + ".name"), block);
        }

        for (String furnitureName : plugin.getConfig().getConfigurationSection("furniture").getKeys(false)) {
            CustomFurniture furniture = new CustomFurniture(plugin.getConfig().getString("furniture." + furnitureName + ".name"),
                    Material.valueOf(plugin.getConfig().getString("furniture." + furnitureName + ".item.material").toUpperCase()),
                    Material.valueOf(plugin.getConfig().getString("furniture." + furnitureName + ".furniture.material").toUpperCase()),
                    plugin.getConfig().getInt("furniture." + furnitureName + ".item.model-data"),
                    plugin.getConfig().getInt("furniture." + furnitureName + ".furniture.model-data"));

            cachedFurniture.put(plugin.getConfig().getString("furniture." + furnitureName + ".name"), furniture);
        }
    }

    public Optional<CustomBlock> getCustomBlockByName(String blockName) {
        return Optional.ofNullable(cachedBlocks.get(blockName));
    }

    public Optional<CustomBlock> getByNote(Note note) {
        return cachedBlocks.values().stream().filter(block -> block.getNote().equals(note)).findFirst();
    }

    public Optional<CustomFurniture> getCustomFurnitureByName(String furnitureName) {
        return Optional.ofNullable(cachedFurniture.get(furnitureName));
    }

    public Optional<CustomChair> getChairByLocation(Location location) {
        return Optional.ofNullable(cachedChairs.get(location));
    }
}
