package it.wintry.customblocks.listeners;

import it.wintry.customblocks.CustomBlocks;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

@RequiredArgsConstructor
public class InteractDismountListener implements Listener {

    private final CustomBlocks plugin;

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Block clickedBlock = e.getClickedBlock();
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || clickedBlock == null) return;
        if (clickedBlock.getType() == Material.NOTE_BLOCK && player.getGameMode() != GameMode.CREATIVE
                && player.isSneaking() && player.getInventory().getItem(player.getInventory().getHeldItemSlot()) != null) {
            e.setCancelled(true);
        }
        if (plugin.getManager().getChairByLocation(clickedBlock.getLocation()).isPresent() && ((Entity) e.getPlayer()).isOnGround()) {
            clickedBlock.getWorld().spawn(clickedBlock.getLocation().add(0.5, 0, 0.5).subtract(0, 0, 0), Arrow.class, arrowChair -> {
                arrowChair.setSilent(true);
                arrowChair.setGravity(false);
                arrowChair.setCustomName("chair");
                arrowChair.setCustomNameVisible(false);
                arrowChair.addPassenger(player);
            });
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDismount(EntityDismountEvent e) {
        if (e.getEntity() instanceof Player p && e.getDismounted() instanceof Arrow dismounted && dismounted.getCustomName() != null && dismounted.getCustomName().equals("chair")) {
            dismounted.removePassenger(p);
            p.teleport(p.getEyeLocation().add(0, 0.2, 0));
            dismounted.remove();
        }
    }
}
