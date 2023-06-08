package ml.itsstrike.striketeamutils.commands;

import ml.itsstrike.striketeamutils.TeamManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.AutoComplete;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.autocomplete.AutoCompleter;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.EntitySelector;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.Arrays;

@Command({"teamutils", "teamu", "tu"})
@CommandPermission("striketeamutils.use")
public final class MainCommand {
    @NotNull private final TeamManager teamManager;
    @NotNull private final MiniMessage miniMessage;
    @NotNull private final Server server;

    public MainCommand(final @NotNull Server server,
                       final @NotNull AutoCompleter autoCompleter,
                       final @NotNull TeamManager teamManager) {
        this.server = server;
        this.teamManager = teamManager;
        this.miniMessage = MiniMessage.miniMessage();

        autoCompleter.registerSuggestion(
                "teamNames",
                (args, sender, command) -> server.getScoreboardManager().getMainScoreboard().getTeams()
                        .stream()
                        .map(Team::getName)
                        .toList()
        );

        autoCompleter.registerSuggestion(
                "colors",
                (args, sender, command) -> Arrays.stream(ChatColor.values())
                        .map(ChatColor::name)
                        .toList()
        );
    }

    @Subcommand("createteam")
    @AutoComplete("YourTeamName @colors")
    public void createTeam(Player p, String team, String color) {
        if (teamManager.teamExists(team)) {
            p.sendMessage(miniMessage.deserialize("<red>Team already exists, please choose another name."));
            return;
        }

        if (!ChatColor.valueOf(color).isColor()) {
            p.sendMessage(miniMessage.deserialize("<red>Color doesn't exists. Please check the name again."));
            return;
        }

        final Team teamCreated = teamManager.create(team, ChatColor.valueOf(color));
        p.sendMessage(miniMessage.deserialize("<green>Team created successfully. <dark_gray>| " + teamCreated.getName()));
    }

    @Subcommand("list")
    public void listTeams(Player p) {
        Component message = miniMessage.deserialize("<green>Teams:").appendNewline();
        for (final Team team : teamManager.listTeams()) {
            message = message.append(miniMessage.deserialize("<dark_gray>- <white>" + team.getName()).appendNewline());
        }

        p.sendMessage(message);
    }

    @Subcommand("listplayers")
    public void listPlayers(Player p, String team) {
        if (!teamManager.teamExists(team)) {
            p.sendMessage(miniMessage.deserialize("<red>Team doesn't exists, please check the name again."));
            return;
        }

        Component message = miniMessage.deserialize("<green>Players in team <white>" + team + "<green>:").appendNewline();
        for (final OfflinePlayer player : teamManager.getTeam(team).getPlayers()) {
            message = message.append(miniMessage.deserialize("<dark_gray>- <white>" + player.getName()).appendNewline());
        }

        p.sendMessage(message);
    }

    @Subcommand("removeteam")
    @AutoComplete("@teamNames")
    public void removeTeam(Player p, String team) {
        if (!teamManager.teamExists(team)) {
            p.sendMessage(miniMessage.deserialize("<red>Team doesn't exists, please check the name again."));
            return;
        }

        teamManager.remove(team);
        p.sendMessage(miniMessage.deserialize("<red>The team <white>" + team + " was removed."));
    }

    @Subcommand("addPlayer")
    @AutoComplete("@teamNames")
    public void addPlayer(BukkitCommandActor actor, String team, EntitySelector<Player> players) {
        players.forEach(player -> {
            teamManager.addPlayer(team, player);
            actor.getSender().sendMessage(miniMessage.deserialize("<green>Player <white>" + player.getName() + "<green> was added to the team."));
        });
    }

    @Subcommand("removePlayer")
    @AutoComplete("@teamNames")
    public void removePlayer(BukkitCommandActor actor, String team, EntitySelector<Player> players) {
        players.forEach(player -> {
            teamManager.removePlayer(team, player);
            actor.getSender().sendMessage(miniMessage.deserialize("<red>Player <white>" + player.getName() + "<red> was removed from the team."));
        });
    }

    @Subcommand("organizePlayers")
    public void organizePlayers(BukkitCommandActor actor) {
        if (teamManager.listTeams().isEmpty()) {
            actor.getSender().sendMessage(miniMessage.deserialize("<red>There are no teams in this server."));
            return;
        }
        teamManager.playersRandomTeams();
        actor.getSender().sendMessage(miniMessage.deserialize("<green>Players organized successfully between the teams."));
    }

    @Subcommand("makeGlow")
    @AutoComplete("@teamNames")
    public void makeGlow(BukkitCommandActor actor, String team) {
        if (!teamManager.teamExists(team)) {
            actor.getSender().sendMessage("§cTeam doesn't exists. Please check the name again.");
            return;
        }

        teamManager.getTeam(team).getPlayers().forEach(player -> {
            if (player.isOnline()) {
                ((Player) player).setGlowing(true);
            }
        });

        actor.getSender().sendMessage(miniMessage.deserialize("<green>Team <white>" + team + "<green> is now glowing."));
    }

    @Subcommand("makeNotGlow")
    @AutoComplete("@teamNames")
    public void makeNotGlow(BukkitCommandActor actor, String team) {
        if (!teamManager.teamExists(team)) {
            actor.getSender().sendMessage(miniMessage.deserialize("<red>Team doesn't exists. Please check the name again!"));
            return;
        }

        teamManager.getTeam(team).getPlayers().forEach(player -> {
            if (player.isOnline()) {
                ((Player) player).setGlowing(false);
            }
        });

        actor.getSender().sendMessage(miniMessage.deserialize("<green>Team <white>" + team + "<green> is no longer glowing."));
    }

    @Subcommand("tpTeam")
    @AutoComplete("@teamNames")
    public void tpTeam(BukkitCommandActor actor, String team, Player player) {
        if (!teamManager.teamExists(team)) {
            actor.getSender().sendMessage(miniMessage.deserialize("<red>That team does not exist, please check the name again!"));
            return;
        }

        teamManager.getTeam(team).getPlayers().forEach(target -> {
            if (target.isOnline()) {
                ((Player) target).teleport(player);
            }
        });

        actor.getSender().sendMessage(miniMessage.deserialize("<green>Team <white>" + team + "<green> has been teleported to <white>" + player.getName() + "<green>."));
    }

    @DefaultFor({"teamutils", "teamu", "tu"})
    @Subcommand("help")
    public void helpCommand(BukkitCommandActor actor) {
        CommandSender sender = actor.getSender();
        Component message = miniMessage.deserialize("""
                <green>StrikeTeamUtils Help:
                <dark_gray>- <white>/teamutils createteam <teamName> <colorName>
                <dark_gray>- <white>/teamutils list
                <dark_gray>- <white>/teamutils listplayers <teamName>
                <dark_gray>- <white>/teamutils removeteam <teamName>
                <dark_gray>- <white>/teamutils addPlayer <teamName> <playerName>
                <dark_gray>- <white>/teamutils removePlayer <teamName> <playerName>
                <dark_gray>- <white>/teamutils organizePlayers
                <dark_gray>- <white>/teamutils makeGlow <teamName>
                <dark_gray>- <white>/teamutils makeNotGlow <teamName>
                <dark_gray>- <white>/teamutils tpTeam <teamName> <playerName>
                <dark_gray>- <white>/teamutils help
                <dark_gray>- <white>Wiki -<aqua> https://github.com/Strike24/StrikeTeamUtils/wiki/StrikeTeamUtils-Official-Wiki-%F0%9F%93%96""");
    }

    @Subcommand("runcommand")
    @AutoComplete("@teamNames")
    public void runCommandAsTeam(BukkitCommandActor actor, String teamName, String command) {
        if (command.startsWith("/")) {
            actor.getSender().sendMessage(
                    miniMessage.deserialize("<red>Please don't use '/' in the command.")
                            .appendNewline()
                            .append(miniMessage.deserialize("<green>Example: <white>kill %player% <red>instead of <white>/kill %player%"))
            );
            return;
        }

        if (!teamManager.teamExists(teamName)) {
            Component message = miniMessage.deserialize("<red>That team doesn't existPlease check the name again!")
                    .appendNewline()
                    .append(miniMessage.deserialize("<green>Teams:"));

            for (Team team : teamManager.listTeams()) {
                message = message.append(miniMessage.deserialize("<dark_gray>- <white>" + team.getName())).appendNewline();
            }
            return;
        }

        teamManager.getTeam(teamName).getPlayers().forEach(player -> {
            if (player.isOnline()) {
                server.dispatchCommand(server.getConsoleSender(), command.replace("%player%", player.getName()));
            }
        });

        actor.getSender().sendMessage(miniMessage.deserialize("<green>Command <white>" + command + "<green> has been executed as team <white>" + teamName + "<green>."));
    }
}
