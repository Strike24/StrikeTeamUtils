package ml.itsstrike.striketeamutils.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import ml.itsstrike.striketeamutils.TeamManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

public class TeamColorPlaceholder extends PlaceholderExpansion {
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
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        Player p = player.getPlayer();
        if (p == null) return "&f";
        if (params.equalsIgnoreCase("teamcolor")) {
            Team team = TeamManager.getPlayerTeam(p);
            if (team == null) return "&f";
            return "&" + team.getColor().getChar();
        }

        return null;
    }
}
