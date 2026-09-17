package com.jenrall.battleroyale.lobby;

import com.jenrall.battleroyale.BattleRoyaleCore;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LobbyManager {

    private final BattleRoyaleCore plugin;
    private final Map<UUID, PlayerState> playerStates = new HashMap<>();

    private Location lobbySpawn;
    private World lobbyWorld;
    private int minPlayers;
    private int maxPlayers;
    private int countdownSeconds;
    private boolean countdownActive = false;
    private BukkitTask countdownTask;

    public enum PlayerState {
        IN_LOBBY,
        IN_QUEUE,
        IN_GAME,
        SPECTATING
    }

    public LobbyManager(BattleRoyaleCore plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    private void loadConfig() {
        FileConfiguration config = plugin.getConfig();
        String worldName = config.getString("lobby.world", "lobby");
        lobbyWorld = Bukkit.getWorld(worldName);

        if (lobbyWorld == null) {
            lobbyWorld = new WorldCreator(worldName)
                .environment(World.Environment.NORMAL)
                .type(WorldType.FLAT)
                .generateStructures(false)
                .createWorld();
        }

        lobbySpawn = new Location(
            lobbyWorld,
            config.getDouble("lobby.spawn.x", 0.5),
            config.getDouble("lobby.spawn.y", 64.0),
            config.getDouble("lobby.spawn.z", 0.5),
            (float) config.getDouble("lobby.spawn.yaw", 0.0),
            (float) config.getDouble("lobby.spawn.pitch", 0.0)
        );

        minPlayers = config.getInt("lobby.min-players", 10);
        maxPlayers = config.getInt("lobby.max-players", 100);
        countdownSeconds = config.getInt("lobby.countdown", 60);
    }

    public void handleJoin(Player player) {
        playerStates.put(player.getUniqueId(), PlayerState.IN_LOBBY);

        player.teleport(lobbySpawn);
        player.setGameMode(GameMode.ADVENTURE);
        player.setInvulnerable(true);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setLevel(0);
        player.setExp(0);
        player.getInventory().clear();

        LobbyItems.giveItems(player);

        player.sendMessage("§a» به لابی خوش آمدی!");
        player.sendTitle("§e✈ Battle Royale", "§7منتظر شروع بازی باش", 10, 40, 10);

        checkQueueStatus();
    }

    public void handleQuit(Player player) {
        playerStates.remove(player.getUniqueId());
        checkQueueStatus();
    }

    private void checkQueueStatus() {
        int online = getLobbyPlayerCount();

        if (online >= minPlayers && !countdownActive) {
            startCountdown();
        } else if (online < minPlayers && countdownActive) {
            stopCountdown();
        }
    }

    private int getLobbyPlayerCount() {
        return (int) Bukkit.getOnlinePlayers().stream()
            .filter(p -> playerStates.getOrDefault(p.getUniqueId(), PlayerState.IN_LOBBY) == PlayerState.IN_LOBBY)
            .count();
    }

    private void startCountdown() {
        countdownActive = true;
        final int[] timeLeft = {countdownSeconds};

        countdownTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (timeLeft[0] <= 0) {
                startGame();
                countdownTask.cancel();
                countdownActive = false;
                return;
            }

            String timeStr = String.format("%02d:%02d", timeLeft[0] / 60, timeLeft[0] % 60);
            String title = "§e✈ شروع بازی";
            String subtitle = "§7" + timeStr + " §7| §f" + getLobbyPlayerCount() + "§7/§f" + minPlayers;

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (playerStates.get(p.getUniqueId()) == PlayerState.IN_LOBBY) {
                    p.sendTitle(title, subtitle, 0, 25, 0);
                    p.setLevel(timeLeft[0]);
                }
            }

            if (timeLeft[0] <= 10) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                }
            }

            timeLeft[0]--;
        }, 0L, 20L);
    }

    private void stopCountdown() {
        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }
        countdownActive = false;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (playerStates.get(p.getUniqueId()) == PlayerState.IN_LOBBY) {
                p.sendTitle("§cلغو شد", "§7بازیکن کافی نیست", 5, 30, 10);
                p.setLevel(0);
            }
        }
    }

    private void startGame() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (playerStates.get(p.getUniqueId()) == PlayerState.IN_LOBBY) {
                playerStates.put(p.getUniqueId(), PlayerState.IN_GAME);
                p.sendMessage("§a» بازی شروع شد!");
                // TODO: انتقال به هواپیما
            }
        }
    }

    public void setLobbySpawn(Location location) {
        this.lobbySpawn = location;
        FileConfiguration config = plugin.getConfig();
        config.set("lobby.spawn.x", location.getX());
        config.set("lobby.spawn.y", location.getY());
        config.set("lobby.spawn.z", location.getZ());
        config.set("lobby.spawn.yaw", location.getYaw());
        config.set("lobby.spawn.pitch", location.getPitch());
        plugin.saveConfig();
    }

    public PlayerState getState(Player player) {
        return playerStates.getOrDefault(player.getUniqueId(), PlayerState.IN_LOBBY);
    }

    public Location getLobbySpawn() { return lobbySpawn; }
    public World getLobbyWorld() { return lobbyWorld; }
    public boolean isCountdownActive() { return countdownActive; }
}
