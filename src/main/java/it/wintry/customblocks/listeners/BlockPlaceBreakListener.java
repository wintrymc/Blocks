package it.wintry.customblocks.listeners;

import it.wintry.customblocks.CustomBlocks;
import it.wintry.customblocks.objects.CustomBlock;
import it.wintry.customblocks.objects.CustomFurniture;
import it.wintry.customblocks.utils.Utils;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketPlayOutBlockChange;
import net.minecraft.world.level.World;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Optional;

@RequiredArgsConstructor
public class BlockPlaceBreakListener implements Listener {

    private final CustomBlocks plugin;

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        ItemStack itemStack = e.getItemInHand();
        Block block = e.getBlock();
        if (!itemStack.hasItemMeta() || !itemStack.getItemMeta().hasCustomModelData()) return;
        if (itemStack.getType() == Material.PURPUR_BLOCK) {
            Optional<CustomBlock> customBlock = plugin.getManager().getCustomBlockByName(ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()));
            if (customBlock.isPresent()) {
                block.setType(Material.NOTE_BLOCK);
                NoteBlock noteBlock = (NoteBlock) block.getBlockData();
                noteBlock.setInstrument(customBlock.get().getInstrument());
                noteBlock.setNote(customBlock.get().getNote());
                block.setBlockData(noteBlock);
            }
        }

        Optional<CustomFurniture> customFurniture = plugin.getManager().getCustomFurnitureByName(ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()));
        if (customFurniture.isPresent()) {
            ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(block.getLocation().add(0.5, -1.37, 0.5), EntityType.ARMOR_STAND);
            armorStand.setVisible(false);
            ItemStack furnitureItem = new ItemStack(customFurniture.get().getFurnitureMaterial());
            ItemMeta itemMeta = furnitureItem.getItemMeta();
            itemMeta.setCustomModelData(customFurniture.get().getFurnitureModelData());
            furnitureItem.setItemMeta(itemMeta);
            armorStand.getEquipment().setHelmet(furnitureItem);
            armorStand.setBasePlate(false);
            armorStand.setCollidable(false);
            armorStand.setSwimming(false);
            armorStand.setGravity(false);
            armorStand.setMarker(true);
            armorStand.setInvulnerable(false);
            block.setType(Material.BARRIER);
        }
        ItemStack currentItem = player.getInventory().getItem(player.getInventory().getHeldItemSlot());
        currentItem.setAmount(currentItem.getAmount() - 1);
    }

    @EventHandler
    public void onBlockStateChange(BlockPhysicsEvent e) {
        Block block = e.getBlock();
        World nmsWorld = ((CraftWorld) e.getBlock().getLocation().getWorld()).getHandle();
        PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(nmsWorld, new BlockPosition(block.getX(), block.getY(), block.getZ()));
        for (Player online : Bukkit.getOnlinePlayers())
            ((CraftPlayer) online).getHandle().b.a(packet);
        e.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        if (block.getType() != Material.NOTE_BLOCK || e.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        NoteBlock noteBlock = (NoteBlock) block.getBlockData();
        Optional<CustomBlock> customBlock = plugin.getManager().getByNote(noteBlock.getNote());
        if (customBlock.isEmpty()) return;
        ItemStack itemStack = new ItemStack(Material.PURPUR_BLOCK);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setCustomModelData(customBlock.get().getModelData());
        itemMeta.setDisplayName(Utils.colorize(customBlock.get().getName()));
        itemStack.setItemMeta(itemMeta);
        e.setDropItems(false);
        block.getWorld().dropItemNaturally(block.getLocation(), itemStack);
    }
}
