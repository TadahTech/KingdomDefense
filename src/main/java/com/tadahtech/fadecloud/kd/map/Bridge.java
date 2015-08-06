package com.tadahtech.fadecloud.kd.map;

import com.google.common.collect.Maps;
import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by Timothy Andis
 */
public class Bridge {

    private static final List<Material> SKIP = Arrays.asList(Material.AIR, Material.GRASS, Material.DIRT, Material.STONE, Material.COAL_ORE, Material.IRON_ORE, Material.STATIONARY_LAVA, Material.WATER);
    private Location min, max;
    private Map<Location, BlockData> dataMap;
    private World world;
    private int minX, maxX, minY, maxY, minZ, maxZ;

    public Bridge(Location min, Location max) {
        this.min = min;
        this.max = max;
        this.minX = min.getBlockX();
        this.maxX = max.getBlockX();
        this.minY = min.getBlockY();
        this.maxY = max.getBlockY();
        this.minZ = min.getBlockZ();
        this.maxZ = max.getBlockZ();
        this.dataMap = Maps.newHashMap();
        this.world = max.getWorld();
        resize();
    }

    public Bridge(World world, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
        this.world = world;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.minZ = minZ;
        this.maxZ = maxZ;
        this.dataMap = Maps.newHashMap();
        resize();
    }

    public void resize() {
        int minX = (this.minX);
        int maxX = (this.maxX);
        int minZ = (this.minZ);
        int maxZ = (this.maxZ);
        int minY = this.minY;
        int maxY = this.maxY;
        if(minY > maxX) {
            this.maxY = minY;
            this.minY = maxY;
        }
        if(minX > maxX) {
            this.maxX = minX;
            this.minX = maxX;
        }
        if(minZ > maxZ) {
            this.maxZ = minZ;
            this.minZ = maxZ;
        }
        collect();
    }

    public void collect() {
        this.dataMap.clear();
        for(int x = minX; x <= maxX; x++) {
            for(int y = minY; y <= maxY; y++) {
                for(int z = minZ; z < maxZ; z++) {
                    Location location = new Location(world, x, y, z);
                    Block block = location.getBlock();
                    if(SKIP.contains(block.getType())) {
                        continue;
                    }
                    BlockData data = new BlockData(block);
                    this.dataMap.put(location, data);
                }
            }
        }
        KingdomDefense.getInstance().getLogger().info("Loaded Bridge Region..." + dataMap.size() + "\n" + dataMap);
    }

    public void place() {
        for(Entry<Location, BlockData> entry : dataMap.entrySet()) {
            entry.getValue().set(entry.getKey());
        }
    }

    public Map<String, Object> save() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("minX", minX);
        map.put("minY", minY);
        map.put("minZ", minZ);
        map.put("maxX", maxX);
        map.put("maxY", maxY);
        map.put("maxZ", maxZ);
        map.put("world", world.getName());
        map.put("new", true);
        return map;
    }

    public static Bridge load(ConfigurationSection section) {
        if(section.get("new") == null){
            return null;
        }
        int minX = section.getInt("minX");
        int maxX = section.getInt("maxX");
        int minY = section.getInt("minY");
        int maxY = section.getInt("maxY");
        int minZ = section.getInt("minZ");
        int maxZ = section.getInt("maxZ");
        World world = Bukkit.getWorld(section.getString("world"));
        return new Bridge(world, minX, maxX, minY, maxY, minZ, maxZ);
    }

    public void clear() {
        Game game = KingdomDefense.getInstance().getGame();
        if(game == null) {
            return;
        }
        this.dataMap.keySet().stream().forEach(location -> location.getBlock().setType(Material.AIR));
    }

    public Location getMax() {
        return max;
    }

    public Location getMin() {
        return min;
    }
}
