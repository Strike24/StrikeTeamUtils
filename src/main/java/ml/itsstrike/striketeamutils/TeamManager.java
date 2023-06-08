package ml.itsstrike.striketeamutils;

import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public final class TeamManager {
    private final Server server;
    private final Scoreboard mainScoreboard;

    /**
     * Constructs a new TeamManager with the given server.
     *
     * @param server the server instance
     */
    public TeamManager(final @NotNull Server server) {
        this.server = server;
        this.mainScoreboard = server.getScoreboardManager().getMainScoreboard();
    }

    /**
     * Retrieves a team by its name.
     *
     * @param teamName the name of the team
     * @return the team with the given name, or null if not found
     */
    public @Nullable Team getTeam(final @NotNull String teamName) {
        return mainScoreboard.getTeam(teamName);
    }

    /**
     * Retrieves the team that the player is assigned to.
     *
     * @param player the player
     * @return the team of the player, or null if not assigned to any team
     */
    public @Nullable Team getPlayerTeam(final @NotNull Player player) {
        return mainScoreboard.getEntryTeam(player.getName());
    }

    /**
     * Creates a new team with the given name and color.
     *
     * @param teamName the name of the team to create
     * @param color    the color of the team
     * @return the newly created team
     * @throws IllegalStateException if a team with the same name already exists
     */
    public @NotNull Team create(final @NotNull String teamName, final @NotNull ChatColor color) {
        if (teamExists(teamName)) {
            throw new IllegalStateException("Temporary Exception: duplicated team.");
        }

        final Team newTeam = mainScoreboard.registerNewTeam(teamName);
        newTeam.displayName(Component.text(teamName));
        newTeam.setColor(color);
        return newTeam;
    }

    /**
     * Removes a team with the given name.
     *
     * @param teamName the name of the team to remove
     */
    public void remove(final @NotNull String teamName) {
        final Team team = getTeam(teamName);
        if (team != null) {
            team.unregister();
        }
    }

    /**
     * Adds a player to the specified team.
     *
     * @param teamName    the name of the team
     * @param playerToAdd the player to add to the team
     */
    public void addPlayer(final @NotNull String teamName, final @NotNull Player playerToAdd) {
        final Team team = getTeam(teamName);
        if (team != null) {
            team.addPlayer(playerToAdd);
        }
    }

    /**
     * Removes a player from the specified team.
     *
     * @param teamName       the name of the team
     * @param playerToRemove the player to remove from the team
     */
    public void removePlayer(final @NotNull String teamName, final @NotNull Player playerToRemove) {
        final Team team = getTeam(teamName);
        if (team != null) {
            team.removePlayer(playerToRemove);
        }
        playerToRemove.setGlowing(false);
    }

    /**
     * Removes the specified team.
     *
     * @param team the team to remove
     */
    public void remove(final @NotNull Team team) {
        team.unregister();
    }

    /**
     * Adds a player to the specified team.
     *
     * @param team         the team to add the player to
     * @param playerToAdd  the player to add to the team
     */
    public void addPlayer(final @NotNull Team team, final @NotNull Player playerToAdd) {
        team.addPlayer(playerToAdd);
    }

    /**
     * Removes a player from the specified team.
     *
     * @param team   the team to remove the player from
     * @param player the player to remove from the team
     */
    public void removePlayer(final @NotNull Team team, final @NotNull Player player) {
        team.removePlayer(player);
        player.setGlowing(false);
    }

    /**
     * Retrieves a set of all teams.
     *
     * @return a set of all teams
     */
    public Set<Team> listTeams() {
        return mainScoreboard.getTeams();
    }

    /**
     * Randomly assigns players to teams.
     */
    public void playersRandomTeams() {
        final List<Player> playerList = server.getOnlinePlayers().stream()
                .filter(player -> !player.hasPermission("stu.random.exempt"))
                .collect(Collectors.toList());

        Collections.shuffle(playerList, new Random());
        playerList.forEach(player -> {
            listTeams().forEach(team -> {
                team.removePlayer(player);
            });

            addPlayer(getNextTeam(), player);
        });
    }

    /**
     * Checks if a team with the given name exists.
     *
     * @param teamName the name of the team
     * @return true if the team exists, false otherwise
     */
    public boolean teamExists(String teamName) {
        return getTeam(teamName) != null;
    }

    /**
     * Retrieves the team with the fewest number of players.
     *
     * @return the team with the fewest players, or null if no teams exist
     */
    private Team getNextTeam() {
        if (mainScoreboard.getTeams().isEmpty()) {
            return null;
        }

        Team selectedTeam = null;
        int smallestSize = Integer.MAX_VALUE;

        for (final Team team : mainScoreboard.getTeams()) {
            int teamSize = team.getSize();

            if (teamSize < smallestSize) {
                smallestSize = teamSize;
                selectedTeam = team;
            }
        }

        if (selectedTeam == null) {
            selectedTeam = mainScoreboard.getTeams().iterator().next();
        }

        return selectedTeam;
    }
}
