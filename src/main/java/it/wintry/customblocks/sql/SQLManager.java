package it.wintry.customblocks.sql;

import com.google.common.collect.Maps;
import it.wintry.customblocks.CustomBlocks;
import it.wintry.customblocks.objects.CustomChair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

public class SQLManager {

    private final CustomBlocks plugin;
    private final ConnectionPoolManager pool;

    public SQLManager(CustomBlocks plugin) {
        this.plugin = plugin;
        pool = new ConnectionPoolManager(plugin);
        makeTable();
    }

    private void makeTable() {
        try (Connection connection = pool.getConnection(); PreparedStatement ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `chairs` (world tinytext, x int, y int, z int)")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void onDisable() {
        pool.closePool();
    }

    public void saveChairs() {
        try (Connection connection = pool.getConnection(); PreparedStatement update = connection.prepareStatement("DELETE FROM chairs")) {
            update.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Collection<CustomChair> regionSet = plugin.getManager().getCachedChairs().values();
        if (regionSet.size() == 0) return;

        StringBuilder sql =
                new StringBuilder("INSERT INTO chairs (world, x, y, z) VALUES");
        for (int i = 0; i < regionSet.size(); i++) {
            sql.append(" (?, ?, ?, ?)");
            if (i != regionSet.size() - 1) sql.append(',');
        }
        try (Connection connection = pool.getConnection(); PreparedStatement update = connection.prepareStatement(sql.toString())) {
            int i = 0;
            for (CustomChair customChair : regionSet) {
                update.setString(4 * i + 1, customChair.getWorld().getName());
                update.setInt(4 * i + 2, customChair.getX());
                update.setInt(4 * i + 3, customChair.getY());
                update.setInt(4 * i + 4, customChair.getZ());
                i++;
            }
            update.execute();
        } catch (Exception e) {
            throw new CompletionException(e);
        }
    }

    public Map<Location, CustomChair> getChairs() {
        CompletableFuture<Map<Location, CustomChair>> completableFuture = CompletableFuture.supplyAsync(() -> {
            Map<Location, CustomChair> customChairs = Maps.newConcurrentMap();
            try (Connection connection = pool.getConnection(); PreparedStatement select = connection.prepareStatement("SELECT * FROM chairs")) {
                try (ResultSet rs = select.executeQuery()) {
                    while (rs.next()) {
                        World world = Bukkit.getWorld(rs.getString("world"));
                        CustomChair customChair = new CustomChair(world, rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
                        customChairs.put(new Location(world, rs.getInt("x"), rs.getInt("y"), rs.getInt("z")), customChair);
                    }
                }
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
            return customChairs;
        });

        try {
            return completableFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return Maps.newHashMap();
    }
}