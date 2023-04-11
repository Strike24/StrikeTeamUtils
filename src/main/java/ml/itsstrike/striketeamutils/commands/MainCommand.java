package ml.itsstrike.striketeamutils.commands;

import ml.itsstrike.striketeamutils.GlowManager;
import ml.itsstrike.striketeamutils.StrikeTeamUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import revxrsal.commands.annotation.AutoComplete;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Command({"teamutils", "teamu", "tu"})
@CommandPermission("striketeamutils.use")
public class MainCommand {
    private final List<Team> teamNames = new ArrayList<>();
    public MainCommand(StrikeTeamUtils plugin) {
        teamNames.addAll(Bukkit.getScoreboardManager().getMainScoreboard().getTeams());
        plugin.getHandler().getAutoCompleter().registerSuggestion("teamNames", (args, sender, command) -> {
            return teamNames.stream().map(Team::getName).toList();
        });

        plugin.getHandler().getAutoCompleter().registerSuggestion("colors", (args, sender, command) -> {
            return List.of("AQUA", "BLACK", "BLUE", "DARK_AQUA", "DARK_BLUE", "DARK_GRAY", "DARK_GREEN", "DARK_PURPLE", "DARK_RED", "GOLD", "GRAY", "GREEN", "LIGHT_PURPLE", "RED", "WHITE", "YELLOW");
        });
        plugin.getHandler().register(this);
    }
    @Subcommand("create")
    @AutoComplete("YourTeamName @colors true|false")
    public void createTeam(Player p, String teamName, String colorName, @Optional String glow, @Optional String prefix) {
        NamedTextColor color = NamedTextColor.NAMES.valueOr(colorName, NamedTextColor.RED);
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        manager.getMainScoreboard().getTeams().forEach(team -> {
            if (team.getName().equalsIgnoreCase(teamName)) {
                p.sendMessage("§cTeam already exists. Please choose a different name.");
                return;
            }
        });
        Team newTeam = manager.getMainScoreboard().registerNewTeam(teamName);
        newTeam.displayName(Component.text(teamName));
        newTeam.color(color);
        if (prefix != null) {
            newTeam.prefix(Component.text("" + prefix));
        }
        if (glow.equalsIgnoreCase("true")) {
            newTeam.getPlayers().forEach(player -> GlowManager.setGlowing((Player) player, true));
        }
        teamNames.add(newTeam);
        p.sendMessage(Component.text("§aTeam created successfully. §8| " + newTeam.displayName().examinableName()));
    }

    @Subcommand("remove")
    @AutoComplete("@teamNames")
    public void removeTeam(Player p, String teamName) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Team team = manager.getMainScoreboard().getTeam(teamName);
        if (team == null) {
            p.sendMessage("§cTeam not found. Please check the name and try again.");
            return;
        }
        teamNames.remove(team);
        p.sendMessage("§cTeam removed successfully. §8| " + team.displayName().examinableName());
        team.unregister();}

    @Subcommand("addOnly")
    @AutoComplete("@teamNames")
    public void addOnly (BukkitCommandActor actor, String teamName, Player playerToAdd) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Team team = manager.getMainScoreboard().getTeam(teamName);
        if (team == null) {
            actor.getSender().sendMessage("§cTeam not found. Please check the name and try again.");
            return;
        }
        team.addEntry(playerToAdd.getName());
        actor.getSender().sendMessage("§aPlayer §f" + playerToAdd.getDisplayName() + "§a added to team successfully. §8| " + team.displayName().examinableName());
    }

    @Subcommand("add")
    @AutoComplete("@teamNames all|members|admins")
    public void addToTeams(Player p, String teamName, @Optional String type) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Team team = manager.getMainScoreboard().getTeam(teamName);
        if (team == null) {
            p.sendMessage("§cTeam not found. Please check the name and try again.");
            return;
        }
        switch (type) {
            case "all" -> {
                Collection<? extends Player> players = Bukkit.getOnlinePlayers();
                players.forEach(player -> team.addEntry(player.getName()));
                p.sendMessage("§aAll players added to team successfully. §8| " + team.displayName().examinableName());
            }
            case "members" -> {
                Collection<? extends Player> players = Bukkit.getOnlinePlayers();
                players.forEach(player -> {
                    if (!player.hasPermission("striketeamutils.admin")) {
                        team.addEntry(player.getName());
                    }
                });
                p.sendMessage("§aAll members added to team successfully. §8| " + team.displayName().examinableName());
            }
            case "admins" -> {
                Collection<? extends Player> players = Bukkit.getOnlinePlayers();
                players.forEach(player -> {
                    if (player.hasPermission("striketeamutils.admin")) {
                        team.addEntry(player.getName());
                    }
                });
                p.sendMessage("§aAll admins added to team successfully. §8| " + team.displayName().examinableName());
            }
            default -> {
                p.sendMessage("§cInvalid type. Please choose one of the following: all, members, admins");
            }
        }
    }
}
