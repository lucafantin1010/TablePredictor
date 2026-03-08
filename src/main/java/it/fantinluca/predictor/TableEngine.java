package it.fantinluca.predictor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Engine responsible for processing match results and generating the sorted league table.
 */
public class TableEngine {

    private final List<Match> processedMatches;

    /**
     * Initializes the TableEngine with an empty list of processed matches.
     */
    public TableEngine() {
        this.processedMatches = new ArrayList<>();
    }

    /**
     * Processes a single match, updating team statistics and storing the match record.
     *
     * @param match The match to process.
     * @param teams The map containing all league teams.
     */
    public void processMatch(Match match, Map<String, Team> teams) {
        if (!match.isPlayed()) {
            return;
        }

        Team homeTeam = teams.get(match.getHomeTeam());
        Team awayTeam = teams.get(match.getAwayTeam());

        if (homeTeam == null || awayTeam == null) {
            System.err.println("Error: One of the teams in the match was not found!");
            return;
        }

        homeTeam.setMatchesPlayed(homeTeam.getMatchesPlayed() + 1);
        awayTeam.setMatchesPlayed(awayTeam.getMatchesPlayed() + 1);

        homeTeam.setGoalsFor(homeTeam.getGoalsFor() + match.getHomeScore());
        homeTeam.setGoalsAgainst(homeTeam.getGoalsAgainst() + match.getAwayScore());

        awayTeam.setGoalsFor(awayTeam.getGoalsFor() + match.getAwayScore());
        awayTeam.setGoalsAgainst(awayTeam.getGoalsAgainst() + match.getHomeScore());

        if (match.getHomeScore() > match.getAwayScore()) {
            homeTeam.setPoints(homeTeam.getPoints() + 3);
        } else if (match.getHomeScore() < match.getAwayScore()) {
            awayTeam.setPoints(awayTeam.getPoints() + 3);
        } else {
            homeTeam.setPoints(homeTeam.getPoints() + 1);
            awayTeam.setPoints(awayTeam.getPoints() + 1);
        }

        processedMatches.add(match);
    }

    /**
     * Prints the league table sorted by Serie A rules.
     *
     * @param teams The map containing all league teams.
     */
    public void printTable(Map<String, Team> teams) {
        List<Team> table = new ArrayList<>(teams.values());

        table.sort(new SerieAComparator(processedMatches));

        System.out.println("\n=======================================================");
        System.out.printf("%-3s | %-15s | %-3s | %-3s | %-3s | %-3s | %-3s\n",
                "Pos", "Team", "Pts", "Pld", "GF", "GA", "GD");
        System.out.println("=======================================================");

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