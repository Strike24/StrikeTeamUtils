package ml.itsstrike.striketeamutils;

import org.bukkit.Server;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * A service class that provides access to utility plugin instances for managing teams.
 * This class serves as a central access point to the TeamManager instance.
 *
 * <p>
 * Use the TeamService to retrieve the TeamManager instance for managing teams
 * in your plugin. The TeamManager provides methods for creating, modifying,
 * and removing teams in a server's scoreboard.
 * </p>
 *
 * <p>
 * This service class is designed to be used within a plugin and is not intended
 * for extension or subclassing.
 * </p>
 *
 * @author SadGhost
 * @since 1.0.0
 */
public final class TeamService {

    @NotNull private final TeamManager teamManager;

    /**
     * Constructs a TeamService with the given server instance.
     *
     * @param server the server instance
     */
    @Contract(pure = true)
    public TeamService(final @NotNull Server server) {
        teamManager = new TeamManager(server);
    }

    /**
     * Retrieves the TeamManager instance.
     *
     * <p>
     * The TeamManager instance provides methods for creating, modifying,
     * and removing teams in a server's scoreboard.
     * </p>
     *
     * @return the TeamManager instance
     * @since 1.0.0
     */
    @Contract(pure = true)
    public @NotNull TeamManager getTeamManager() {
        return teamManager;
    }
}
