package ml.itsstrike.striketeamutils;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class TeamManager {
    public static Team getTeam(String teamName) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        return manager.getMainScoreboard().getTeam(teamName);
    }

    public static Team create(String teamName, ChatColor color) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        manager.getMainScoreboard().getTeams().forEach(team -> {
            if (team.getName().equalsIgnoreCase(teamName)) {
                return;
            }
        });
        Team newTeam = manager.getMainScoreboard().registerNewTeam(teamName);
        newTeam.displayName(Component.text(teamName));
        newTeam.setColor(color);
        return newTeam;
    }

    public static void remove(String teamName) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        manager.getMainScoreboard().getTeams().forEach(team -> {
            if (team.getName().equalsIgnoreCase(teamName)) {
                team.unregister();
                return;
            }
        });
    }

    public static void addPlayer(String teamName, Player playerToAdd) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        manager.getMainScoreboard().getTeams().forEach(team -> {
            if (team.getName().equalsIgnoreCase(teamName)) {
                team.addPlayer(playerToAdd);
                return;
            }
        });
    }


    public static void removePlayer(String teamName, Player playerToRemove) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        manager.getMainScoreboard().getTeams().forEach(team -> {
            if (team.getName().equalsIgnoreCase(teamName)) {
                team.removePlayer(playerToRemove);
                return;
            }
        });
        playerToRemove.setGlowing(false);
    }

    public static void remove(Team team) {
        team.unregister();
    }

    public static void addPlayer(Team team, Player playerToAdd) {
        team.addPlayer(playerToAdd);
    }


    public static void removePlayer(Team team, Player playerToRemove) {
        team.removePlayer(playerToRemove);
        playerToRemove.setGlowing(false);
    }

    public static Set<Team> listTeams() {
        return Bukkit.getScoreboardManager().getMainScoreboard().getTeams();
    }

    public static void playersRandomTeams() {

        List<Player> playerList = Bukkit.getOnlinePlayers().stream().filter(player -> !player.hasPermission("stu.random.exempt")).collect(Collectors.toList());

        Collections.shuffle(playerList, new Random());

        playerList.forEach(player -> {
            listTeams().stream().forEach(team -> {
                team.removePlayer(player);
            });
            addPlayer(getNextTeam(), player);
        });
    }

    public static boolean teamExist(String teamName) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        return manager.getMainScoreboard().getTeam(teamName) != null;
    }

    private static Team getNextTeam() {

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Set<Team> teams = manager.getMainScoreboard().getTeams();

        if (teams.isEmpty()) return null;


        int amountBefore = -1;
        Team beforeTeam = null;

        boolean chosen = false;

        for (Team team : teams) {
            if (amountBefore == -1) {
                amountBefore = team.getSize();
                beforeTeam = team;
                continue;
            }


            beforeTeam = team;

            if (team.getSize() < amountBefore) {
                chosen = true;
                break;
            }


            amountBefore = team.getSize();
        }


        return chosen ? beforeTeam : teams.stream().findFirst().get();
    }

}
