package ml.itsstrike.striketeamutils;

import ml.itsstrike.striketeamutils.commands.MainCommand;
import ml.itsstrike.striketeamutils.placeholders.TeamColorPlaceholder;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import revxrsal.commands.bukkit.BukkitCommandHandler;

public final class StrikeTeamUtils extends JavaPlugin {
    private BukkitCommandHandler handler;
    private TeamService teamService;

    @Override
    public void onEnable() {
        getSLF4JLogger().info("StrikeTeamUtils has been enabled successfully.");

        handler = BukkitCommandHandler.create(this);

        teamService = new TeamService(getServer());
        getServer().getServicesManager().register(TeamService.class, teamService, this, ServicePriority.Highest);
        // Access the TeamService using getServer::getServicesManager::getRegistration();

        registerCommands();

        getServer().getOnlinePlayers().forEach(player -> {
            if (player.hasPermission("striketeamutils.admin")) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<green>StrikeTeamUtils has been enabled successfully."));
                player.playSound(player.getLocation(), "minecraft:block.note_block.pling", 1, 1);
            }
        });

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null)
            new TeamColorPlaceholder(teamService.getTeamManager()).register();
    }

    private void registerCommands() {
        handler.registerBrigadier();
        handler.register(
                new MainCommand(getServer(), handler.getAutoCompleter(), teamService.getTeamManager())
        );
    }

    @Contract(pure = true)
    public BukkitCommandHandler getHandler() {
        return handler;
    }

}
