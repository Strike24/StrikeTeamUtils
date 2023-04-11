package ml.itsstrike.striketeamutils;

import lombok.Getter;
import ml.itsstrike.striketeamutils.commands.MainCommand;
import ml.itsstrike.striketeamutils.listeners.GlowingListener;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.bukkit.BukkitCommandHandler;

public final class StrikeTeamUtils extends JavaPlugin {
    @Getter BukkitCommandHandler handler;
    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getConsoleSender().sendMessage("§aStrikeTeamUtils has been enabled successfully.");
        loadManagers();
        loadCommands();
        loadListeners();

        getServer().getOnlinePlayers().forEach(player -> {
            if (player.hasPermission("striketeamutils.admin")) {
                player.sendMessage("§aStrikeTeamUtils has been enabled successfully.");
                player.playSound(player.getLocation(), "minecraft:block.note_block.pling", 1, 1);
            }
        });
    }

    private void loadCommands() {
        new MainCommand(this);
        handler.registerBrigadier();
    }
    private void loadManagers() {
        handler = BukkitCommandHandler.create(this);
    }

    private void loadListeners() {
    getServer().getPluginManager().registerEvents(new GlowingListener(), this);
    }

}
