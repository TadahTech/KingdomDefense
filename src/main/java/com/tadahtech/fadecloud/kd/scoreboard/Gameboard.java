package com.tadahtech.fadecloud.kd.scoreboard;

import com.google.common.collect.Maps;
import com.tadahtech.fadecloud.kd.game.Game;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Map;
import java.util.UUID;

/**
 * Created by Timothy Andis
 */
public class Gameboard {

    private Game game;
    private Map<UUID, BufferedObjective> objectives = Maps.newHashMap();

    public Gameboard(Game game) {
        this.game = game;
    }

    public void flip() {
        game.getPlayers().stream().forEach(info -> {
            Player player = info.getBukkitPlayer();
            BufferedObjective objective = objectives.get(player.getUniqueId());
            if (objective == null) {
                Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                objective = new BufferedObjective(scoreboard);
                player.setScoreboard(scoreboard);
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                objective.setTitle(ChatColor.translateAlternateColorCodes('&', "&8&l(&bK&fD&8&l)"));
                this.objectives.put(player.getUniqueId(), objective);
            }
            objective.setLine(15, color("&8&m-------"));
            objective.setLine(13, ChatColor.GRAY + "Online: ");
            objective.setLine(12, ChatColor.GREEN.toString() + Bukkit.getOnlinePlayers().size() + "/" + game.getMap().getMax());
            objective.setLine(11, color("&8&m&l-"));
            objective.setLine(10, ChatColor.GRAY + "Coins: ");
            objective.setLine(9, ChatColor.YELLOW.toString() + info.getCoins());
            objective.setLine(8, color("&8&m&l-&r"));
            objective.setLine(7, color("&7Map"));
            objective.setLine(6, ChatColor.RED + game.getMap().getName());
            objective.setLine(5, color("&8&m&o-&r"));
            objective.setLine(4, color("&7Stage"));
            objective.setLine(3, game.getState().format());
            objective.setLine(2, color("&8&m-------&r"));
            objective.flip();
        });
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public void add(PlayerInfo info) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        info.getBukkitPlayer().setScoreboard(scoreboard);
        BufferedObjective objective = new BufferedObjective(scoreboard);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setTitle(ChatColor.translateAlternateColorCodes('&', "&8&l(&bK&fD&8&l)"));
        this.objectives.putIfAbsent(info.getUuid(), objective);
    }
}
