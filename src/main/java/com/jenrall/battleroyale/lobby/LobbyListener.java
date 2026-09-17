package com.jenrall.battleroyale.lobby;

import com.jenrall.battleroyale.BattleRoyaleCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LobbyListener implements Listener {

    private final BattleRoyaleCore plugin;

    public LobbyListener(BattleRoyaleCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        plugin.getLobbyManager().handleJoin(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        plugin.getLobbyManager().handleQuit(event.getPlayer());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (plugin.getLobbyManager().getState(player) == LobbyManager.PlayerState.IN_LOBBY) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPvP(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player victim) {
            if (plugin.getLobbyManager().getState(victim) == LobbyManager.PlayerState.IN_LOBBY) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (plugin.getLobbyManager().getState(event.getPlayer()) == LobbyManager.PlayerState.IN_LOBBY) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (plugin.getLobbyManager().getState(event.getPlayer()) == LobbyManager.PlayerState.IN_LOBBY) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (plugin.getLobbyManager().getState(event.getPlayer()) == LobbyManager.PlayerState.IN_LOBBY) {
            event.setCancelled(true);
        }
    }
}
