package org.brutality.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;

public class GetOrderedTeamsHelper {
    public static void handleScoreboard(Scoreboard scoreboard, int n, String str) {
        var orderedTeams = getOrderedTeams(scoreboard);
        if (orderedTeams != null && n >= 1 && n <= orderedTeams.size()) {
            ScorePlayerTeam team = (ScorePlayerTeam)orderedTeams.get(n - 1);
            team.setNamePrefix(str);
            team.setNameSuffix("");
        }
    }

    public static List<String> computeList(Scoreboard scoreboard) {
        List list = new ArrayList();
        var orderedTeams = getOrderedTeams(scoreboard);
        if (orderedTeams == null) {
            return list;
        }
        for (ScorePlayerTeam team : orderedTeams) {
            list.add(team.getColorPrefix() + team.getColorSuffix());
        }

        return list;
    }

    public static int computeInt(Scoreboard scoreboard, String str) {
        var orderedTeams = getOrderedTeams(scoreboard);
        if (orderedTeams == null) {
            return -1;
        }
        for (int i = 0; i < orderedTeams.size(); i++) {
            ScorePlayerTeam team = (ScorePlayerTeam)orderedTeams.get(i);
            String str2 = ExecuteCommandHelper.computeString6(team.getColorPrefix() + team.getColorSuffix());
            if (str2.contains(str)) {
                return i + 1;
            }
        }

        return -1;
    }

    public static int computeInt2(Scoreboard scoreboard, String str) {
        var orderedTeams = getOrderedTeams(scoreboard);
        if (orderedTeams == null) {
            return -1;
        }
        for (int i = 0; i < orderedTeams.size(); i++) {
            ScorePlayerTeam team = (ScorePlayerTeam)orderedTeams.get(i);
            if ((team.getColorPrefix() + team.getColorSuffix()).contains(str)) {
                return i + 1;
            }
        }

        return -1;
    }

    private static List<ScorePlayerTeam> getOrderedTeams(Scoreboard scoreboard) {
        var teams = scoreboard.getTeams().stream().filter(team -> team.getTeamName().contains("team_")).collect(Collectors.toList());
        if (teams.isEmpty()) {
            return null;
        }
        try {
            teams.sort(Comparator.comparingInt(team2 -> Integer.parseInt(team2.getTeamName().replace("team_", ""))));
        } catch (Exception ignored) {
        }

        return teams;
    }

    public static String computeString(Scoreboard scoreboard, int n) {
        var orderedTeams = getOrderedTeams(scoreboard);
        return orderedTeams != null && n >= 1 && n <= orderedTeams.size() ? computeList(scoreboard).get(n - 1) : null;
    }
}
