package com.jenrall.battleroyale;

import com.jenrall.battleroyale.lobby.LobbyListener;
import com.jenrall.battleroyale.lobby.LobbyManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class BattleRoyaleCore extends JavaPlugin {

    private LobbyManager lobbyManager;

    @Override
    public void onEnable() {
        // ذخیره کانفیگ پیش‌فرض
        saveDefaultConfig();

        // ساخت مدیر لابی
        this.lobbyManager = new LobbyManager(this);

        getLogger().info("Battle Royale Plugin has been enabled!");

        // ثبت لیسنر لابی
        getServer().getPluginManager().registerEvents(
            new LobbyListener(this), this
        );

        // ثبت کامند
        this.getCommand("br").setExecutor(new BattleRoyaleCommand(this));
    }

    @Override
    public void onDisable() {
        getLogger().info("Battle Royale Plugin has been disabled!");
    }

    public LobbyManager getLobbyManager() {
        return lobbyManager;
    }
}
