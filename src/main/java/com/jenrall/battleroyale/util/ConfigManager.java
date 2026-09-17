package com.jenrall.battleroyale.util;

import com.jenrall.battleroyale.BattleRoyaleCore;

public class ConfigManager {

    private final BattleRoyaleCore plugin;

    public ConfigManager(BattleRoyaleCore plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        plugin.reloadConfig();
    }

    public void save() {
        plugin.saveConfig();
    }
}
