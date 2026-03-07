package it.fantinluca.predictor;

import java.util.Map;

public class TableEngine {

    /**
     * Processes a single match and updates the statistics of the involved teams.
     * @param match The match that was played.
     * @param teams A Map containing all the teams in the league (Key: Team Name, Value: Team Object).
     */
    public void processMatch(Match match, Map<String, Team> teams) {

        // if the match hasn't been played yet, we do nothing!
        if (!match.isPlayed()) {
            return;
        }

        // Retrieve the two teams from our Map using their names
        Team homeTeam = teams.get(match.getHomeTeam());
        Team awayTeam = teams.get(match.getAwayTeam());

        // Safety check: ensure both teams actually exist in our "database"
        if (homeTeam == null || awayTeam == null) {
            System.err.println("Error: One of the teams in the match was not found!");
            return;
        }

        // 1. Update Matches Played
        homeTeam.setMatchesPlayed(homeTeam.getMatchesPlayed() + 1);
        awayTeam.setMatchesPlayed(awayTeam.getMatchesPlayed() + 1);

        // 2. Update Goals
        homeTeam.setGoalsFor(homeTeam.getGoalsFor() + match.getHomeScore());
        homeTeam.setGoalsAgainst(homeTeam.getGoalsAgainst() + match.getAwayScore());

        awayTeam.setGoalsFor(awayTeam.getGoalsFor() + match.getAwayScore());
        awayTeam.setGoalsAgainst(awayTeam.getGoalsAgainst() + match.getHomeScore());

        // 3. Update Points based on the result
        if (match.getHomeScore() > match.getAwayScore()) {
            // Home team wins
            homeTeam.setPoints(homeTeam.getPoints() + 3);
        } else if (match.getHomeScore() < match.getAwayScore()) {
            // Away team wins
            awayTeam.setPoints(awayTeam.getPoints() + 3);
        } else {
            // Draw
            homeTeam.setPoints(homeTeam.getPoints() + 1);
            awayTeam.setPoints(awayTeam.getPoints() + 1);
        }
    }

    /**
     * Prints the league table formatted  in the console.
     * @param teams The Map containing all the teams.
     */
    public void printTable(Map<String, Team> teams) {
        // Convert the Map values into a List so we can sort it
        java.util.List<Team> table = new java.util.ArrayList<>(teams.values());

        // Sort the list using Comparator
        table.sort(new StandardTableComparator());

        // Print the header using printf for alignment
        System.out.println("\n=======================================================");
        System.out.printf("%-3s | %-15s | %-3s | %-3s | %-3s | %-3s | %-3s\n",
                "Pos", "Team", "Pts", "Pld", "GF", "GA", "GD");
        System.out.println("=======================================================");

        // Loop through the sorted list and print each team
        int position = 1;
        for (Team team : table) {
            System.out.printf("%-3d | %-15s | %-3d | %-3d | %-3d | %-3d | %-3d\n",
                    position,
                    team.getName(),
                    team.getPoints(),
                    team.getMatchesPlayed(),
                    team.getGoalsFor(),
                    team.getGoalsAgainst(),
                    team.getGoalDifference());
            position++;
        }
        System.out.println("=======================================================\n");
    }
}