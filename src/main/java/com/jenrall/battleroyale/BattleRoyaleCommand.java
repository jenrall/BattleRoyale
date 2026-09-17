package com.jenrall.battleroyale;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BattleRoyaleCommand implements CommandExecutor {

    private final BattleRoyaleCore plugin;

    public BattleRoyaleCommand(BattleRoyaleCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (args.length == 0) {
            sender.sendMessage("§e» Battle Royale Plugin v1.0.0");
            sender.sendMessage("§7/br help §8- نمایش راهنما");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "help" -> {
                sender.sendMessage("§6=== Battle Royale Help ===");
                sender.sendMessage("§e/br help §7- راهنما");
                sender.sendMessage("§e/br setspawn §7- تنظیم اسپاون لابی (ادمین)");
                sender.sendMessage("§e/br reload §7- ری‌لود کانفیگ (ادمین)");
            }
            case "setspawn" -> {
                if (!sender.hasPermission("br.admin")) {
                    sender.sendMessage("§c» دسترسی نداری!");
                    return true;
                }
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("§c» فقط بازیکن می‌تونه این کار رو بکنه!");
                    return true;
                }
                plugin.getLobbyManager().setLobbySpawn(player.getLocation());
                player.sendMessage("§a» اسپاون لابی تنظیم شد!");
            }
            case "reload" -> {
                if (!sender.hasPermission("br.admin")) {
                    sender.sendMessage("§c» دسترسی نداری!");
                    return true;
                }
                plugin.reloadConfig();
                sender.sendMessage("§a» کانفیگ ری‌لود شد!");
            }
            default -> sender.sendMessage("§c» کامند ناشناخته! از /br help استفاده کن.");
        }

        return true;
    }
}
