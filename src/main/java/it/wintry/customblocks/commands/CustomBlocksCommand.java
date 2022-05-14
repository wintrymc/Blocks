package it.wintry.customblocks.commands;

import it.wintry.customblocks.CustomBlocks;
import it.wintry.customblocks.objects.CustomChair;
import it.wintry.customblocks.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@RequiredArgsConstructor
public class CustomBlocksCommand implements CommandExecutor {

    private final CustomBlocks plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Utils.colorize("&cDevi essere un giocatore per usare questo comando!"));
            return true;
        }
        if (!player.hasPermission("customblocks.admin")) {
            player.sendMessage(Utils.colorize("&cNon hai i permessi per usare questo comando."));
            return true;
        }
        if (args.length == 0) {
            player.sendMessage(Utils.colorize("&7Digita /customblocks <blocks,furniture>."));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "blocks" -> {
                Inventory blocks = Bukkit.createInventory(null, 36, Utils.colorize("&6Blocchi"));
                for (String string : plugin.getConfig().getConfigurationSection("blocks").getKeys(false)) {
                    ItemStack itemStack = new ItemStack(Material.PURPUR_BLOCK);
                    ItemMeta meta = itemStack.getItemMeta();
                    meta.setDisplayName(Utils.colorize(plugin.getConfig().getString("blocks." + string + ".name")));
                    meta.setCustomModelData(plugin.getConfig().getInt("blocks." + string + ".model-data"));
                    itemStack.setItemMeta(meta);
                    blocks.addItem(itemStack);
                }
                player.openInventory(blocks);
                player.sendMessage(Utils.colorize("&aHai aperto l'inventario dei blocchi personalizzati!"));
            }
            case "furniture" -> {
                Inventory furniture = Bukkit.createInventory(null, 36, Utils.colorize("&6Arredamento"));
                for (String string : plugin.getConfig().getConfigurationSection("furniture").getKeys(false)) {
                    ItemStack itemStack = new ItemStack(Material.valueOf(plugin.getConfig().getString("furniture." + string + ".item.material").toUpperCase()));
                    ItemMeta meta = itemStack.getItemMeta();
                    meta.setDisplayName(Utils.colorize(plugin.getConfig().getString("furniture." + string + ".name")));
                    meta.setCustomModelData(plugin.getConfig().getInt("furniture." + string + ".item.model-data"));
                    itemStack.setItemMeta(meta);
                    furniture.addItem(itemStack);
                }
                player.openInventory(furniture);
                player.sendMessage(Utils.colorize("&aHai aperto l'inventario dell'arredamento personalizzato!"));
            }
            case "chair" -> {
                Block block = player.getTargetBlockExact(5);
                if (block == null) {
                    player.sendMessage(Utils.colorize("&cDevi puntare un blocco per eseguire questo comando."));
                    return true;
                }
                if (plugin.getManager().getChairByLocation(block.getLocation()).isPresent()) {
                    plugin.getManager().getCachedChairs().remove(block.getLocation());
                    player.sendMessage(Utils.colorize("&aLa sedia è stata rimossa."));
                } else {
                    Location location = block.getLocation();
                    CustomChair chair = new CustomChair(location.getWorld(), (int) location.getX(), (int) location.getY(), (int) location.getZ());
                    plugin.getManager().getCachedChairs().put(location, chair);
                    player.sendMessage(Utils.colorize("&aLa sedia è stata impostata."));
                }
            }
            default -> player.sendMessage(Utils.colorize("&7Digita /customblocks <blocks,furniture,chair>."));
        }
        return true;
    }
}
