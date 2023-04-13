package ml.itsstrike.striketeamutils.commands;

import ml.itsstrike.striketeamutils.StrikeTeamUtils;
import ml.itsstrike.striketeamutils.TeamManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import revxrsal.commands.annotation.AutoComplete;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.EntitySelector;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.Arrays;

@Command({"teamutils", "teamu", "tu"})
@CommandPermission("striketeamutils.use")
public class MainCommand {
    public MainCommand(StrikeTeamUtils plugin) {
        plugin.getHandler().getAutoCompleter().registerSuggestion("teamNames", (args, sender, command) -> {
            return Bukkit.getScoreboardManager().getMainScoreboard().getTeams().stream().map(Team::getName).toList();
        });

        plugin.getHandler().getAutoCompleter().registerSuggestion("colors", (args, sender, command) -> {
            return Arrays.stream(ChatColor.values()).map(ChatColor::name).toList();
        });
        plugin.getHandler().register(this);
    }

    @Subcommand("createteam")
    @AutoComplete("YourTeamName @colors")
    public void createTeam(Player p, String teamName, String colorName) {
        if (TeamManager.teamExist(teamName)) {
            p.sendMessage(Component.text("§cTeam already exists. Please choose another name."));
            return;
        }
        if (!ChatColor.valueOf(colorName).isColor()) {
            p.sendMessage(Component.text("§cColor doesn't exists. Please check the name again."));
            return;
        }
        Team teamCreated = TeamManager.create(teamName, ChatColor.valueOf(colorName));
        p.sendMessage(Component.text("§aTeam created successfully. §8| " + teamCreated.getName()));
    }

    @Subcommand("list")
    public void listTeams(Player p) {
        p.sendMessage(Component.text("§aTeams:"));
        TeamManager.listTeams().forEach(team -> {
            p.sendMessage(Component.text("§8- §f" + team.getName()));
        });
    }

    @Subcommand("listplayers")
    public void listPlayers(Player p, String teamName) {
        if (!TeamManager.teamExist(teamName)) {
            p.sendMessage(Component.text("§cTeam doesn't exists. Please check the name again."));
            return;
        }
        p.sendMessage(Component.text("§aPlayers in team §f" + teamName + "§a:"));
        TeamManager.getTeam(teamName).getPlayers().forEach(player -> {
                    p.sendMessage(Component.text("§8- §f" + player.getName()));
                }
        );
    }

    @Subcommand("removeteam")
    @AutoComplete("@teamNames")
    public void removeTeam(Player p, String teamName) {
        if (!TeamManager.teamExist(teamName)) {
            p.sendMessage(Component.text("§cTeam doesn't exists. Please check the name again."));
            return;
        }
        TeamManager.remove(teamName);
        p.sendMessage("§cTeam removed successfully.");

    }

    @Subcommand("addPlayer")
    @AutoComplete("@teamNames")
    public void addPlayer(BukkitCommandActor actor, String teamName, EntitySelector<Player> playersToAdd) {
        playersToAdd.forEach(playerToAdd -> {
            TeamManager.addPlayer(teamName, playerToAdd);
            actor.getSender().sendMessage("§aPlayer §f" + playerToAdd.getName() + "§a added to team successfully. ");
        });
    }

    @Subcommand("removePlayer")
    @AutoComplete("@teamNames")
    public void removePlayer(BukkitCommandActor actor, String teamName, EntitySelector<Player> playersToAdd) {
        playersToAdd.forEach(playerToRemove -> {
            TeamManager.removePlayer(teamName, playerToRemove);
            actor.getSender().sendMessage("§aPlayer §f" + playerToRemove.getName() + "§a removed from team successfully. ");
        });
    }

    @Subcommand("organizePlayers")
    public void organizePlayers(BukkitCommandActor actor) {
        if (TeamManager.listTeams().isEmpty()) {
            actor.getSender().sendMessage("§cThere are no teams in this server.");
            return;
        }
        TeamManager.playersRandomTeams();
        actor.getSender().sendMessage("§aPlayers organized successfully between the teams.");
    }

    @Subcommand("makeGlow")
    @AutoComplete("@teamNames")
    public void makeGlow(BukkitCommandActor actor, String teamName) {
        if (!TeamManager.teamExist(teamName)) {
            actor.getSender().sendMessage("§cTeam doesn't exists. Please check the name again.");
            return;
        }
        TeamManager.getTeam(teamName).getPlayers().forEach(player -> {
            if (player.isOnline()) {
                ((Player) player).setGlowing(true);
            }
        });
        actor.getSender().sendMessage("§aTeam §f" + teamName + "§a is now glowing.");
    }

    @Subcommand("makeNotGlow")
    @AutoComplete("@teamNames")
    public void makeNotGlow(BukkitCommandActor actor, String teamName) {
        if (!TeamManager.teamExist(teamName)) {
            actor.getSender().sendMessage("§cTeam doesn't exists. Please check the name again!");
            return;
        }
        TeamManager.getTeam(teamName).getPlayers().forEach(player -> {
            if (player.isOnline()) {
                ((Player) player).setGlowing(false);
            }
        });
        actor.getSender().sendMessage("§aTeam §f" + teamName + "§a is no longer glowing.");
    }


}
