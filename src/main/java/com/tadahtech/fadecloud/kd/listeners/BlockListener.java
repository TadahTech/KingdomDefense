package com.tadahtech.fadecloud.kd.listeners;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.map.Island;
import com.tadahtech.fadecloud.kd.map.structures.Structure;
import com.tadahtech.fadecloud.kd.map.structures.WorldEditAssit;
import com.tadahtech.fadecloud.kd.menu.menus.UpgradeStructureMenu;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * Created by Timothy Andis (TadahTech) on 7/28/2015.
 */
public class BlockListener implements Listener {

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        Player player = event.getPlayer();
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        if (info == null) {
            return;
        }
        Island island = info.getCurrentTeam().getIsland();
        Optional<Structure> maybe = island.getStructure(location);
        if (!maybe.isPresent()) {
            if (island.inCastle(location)) {
                event.setCancelled(true);
                info.getBukkitPlayer().sendMessage(ChatColor.RED + "You can't break that here!");
            }
            return;
        }
        int y = location.getBlockY();
        if (y < island.getLowest()) {
            return;
        }
        event.setCancelled(true);
        info.getBukkitPlayer().sendMessage(ChatColor.RED + "You can't break structures!");
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        ItemStack inhand = event.getItemInHand();
        Location location = event.getBlock().getLocation();
        Player player = event.getPlayer();
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        Island island = info.getCurrentTeam().getIsland();
        if (!island.getRegion().canBuild(location)) {
            return;
        }
        if (island.inCastle(location)) {
            event.setCancelled(true);
            info.sendMessage(ChatColor.RED + "You cannot place in your castle!");
            return;
        }
        Optional<Structure> maybe = island.getStructure(location);
        if (!maybe.isPresent()) {
            if (inhand == null || inhand.getType() == Material.AIR) {
                return;
            }
            Structure structure = info.getCurrentStructure();
            if (structure == null) {
                return;
            }
            info.setCurrentStructure(null);
            WorldEditAssit.pasteStructure(island, location, structure, 1);
            info.sendMessage(structure.getName() + ChatColor.YELLOW + " created! " + (4 - island.getCount(structure.getStructureType())) + " more can be made.");
            return;
        }
        int y = location.getBlockY();
        if (y < island.getLowest()) {
            return;
        }
        event.setCancelled(true);
        info.getBukkitPlayer().sendMessage(ChatColor.RED + "You can't place that here!");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!player.isSneaking()) {
            return;
        }
        if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }
        Location location = player.getLocation();
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        if (info.getCurrentTeam() == null) {
            return;
        }
        Optional<Structure> maybe = info.getCurrentTeam().getIsland().getStructure(location);
        if (maybe.isPresent()) {
            Structure structure = maybe.get();
            new UpgradeStructureMenu(structure).open(player);
        }
    }
}