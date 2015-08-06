package com.tadahtech.fadecloud.kd.map;

import com.google.common.collect.Maps;
import com.sk89q.worldedit.Vector;
import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.map.structures.GridLocation;
import com.tadahtech.fadecloud.kd.map.structures.Structure;
import com.tadahtech.fadecloud.kd.utils.Utils;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by Timothy Andis (TadahTech) on 7/27/2015.
 */
public class Island {

    private Region region, castleRegion;
    private Map<GridLocation, Structure> structures;
    private int lowest;
    private Vector min;

    public Island(Region region, Region castleRegion) {
        this.region = region;
        this.castleRegion = castleRegion;
        this.structures = new HashMap<>();
    }

    public Optional<Structure> getStructure(GridLocation location) {
        return Optional.ofNullable(structures.get(location));
    }

    public boolean inCastle(Location location) {
        return castleRegion.canBuild(location);
    }

    public void addStructure(GridLocation location, Structure structure) {
        structures.put(location, structure);
    }

    public Region getRegion() {
        return region;
    }

    public Region getCastleRegion() {
        return castleRegion;
    }

    public int getLowest() {
        return lowest;
    }

    public void setLowest(int lowest) {
        this.lowest = lowest;
    }

    public static Island load(ConfigurationSection file) {
        Logger logger = KingdomDefense.getInstance().getLogger();
        logger.info("Region Min: " + file.getString("region.min"));
        logger.info("Region Max: " + file.getString("region.max"));
        logger.info("Castle Min: " + file.getString("castle.min"));
        logger.info("Castle Max: " + file.getString("castle.max"));
        Location regionMin = Utils.locFromString(file.getString("region.min"));
        Location regionMax = Utils.locFromString(file.getString("region.max"));
        Location castleMin = Utils.locFromString(file.getString("castle.min"));
        Location castleMax = Utils.locFromString(file.getString("castle.max"));
        int lowest = file.getInt("lowest");
        Region region = new Region(regionMin, regionMax);
        Region castle = new Region(castleMin, castleMax);
        Island island = new Island(region, castle);
        island.setLowest(lowest);
        return island;
    }

    public Map<String, Object> save() {
        Map<String, Object> map = Maps.newHashMap();
        Map<String, Object> region = Maps.newHashMap();
        Map<String, Object> castle = Maps.newHashMap();
        region.putIfAbsent("min", Utils.locToString(this.region.getMin()));
        region.putIfAbsent("max", Utils.locToString(this.region.getMax()));
        map.putIfAbsent("region", region);
        castle.putIfAbsent("min", Utils.locToString(this.castleRegion.getMin()));
        castle.putIfAbsent("max", Utils.locToString(this.castleRegion.getMax()));
        map.putIfAbsent("castle", castle);
        map.putIfAbsent("lowest", getLowest());
        return map;
    }

    public int getCount(StructureType type) {
        return structures.values().stream().filter(structure -> structure.getStructureType() == type).collect(Collectors.toList()).size();
    }

    public Vector getMin() {
        if(min == null) {
            min = new Vector(region.getMin().getX(), 0, region.getMin().getZ());
        }
        return min;
    }
}
