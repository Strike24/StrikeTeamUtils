package ml.itsstrike.striketeamutils.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import ml.itsstrike.striketeamutils.TeamManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

public final class TeamColorPlaceholder extends PlaceholderExpansion {
    @NotNull private final TeamManager teamManager;

    public TeamColorPlaceholder(final @NotNull TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "stu";
    }

    @Override
    public @NotNull String getAuthor() {
        return "strike";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        final Player p = player.getPlayer();

        if (p == null) return "&f";
        if (params.equalsIgnoreCase("teamcolor")) {
            final Team team = teamManager.getPlayerTeam(p);
            return team == null ? "&f" : "&" + team.getColor().getChar();
        }

        return null;
    }
}
