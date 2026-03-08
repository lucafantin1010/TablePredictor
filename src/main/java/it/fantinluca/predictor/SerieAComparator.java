package it.fantinluca.predictor;

import java.util.Comparator;
import java.util.List;

/**
 * Comparator for sorting Serie A teams according to official tie-breaker rules.
 * The criteria are evaluated in the following order:
 * 1. Total points
 * 2. Head-to-head points
 * 3. Head-to-head goal difference
 * 4. Overall goal difference
 * 5. Overall goals scored
 * 6. Alphabetical order
 */
public class SerieAComparator implements Comparator<Team> {

    private final List<Match> allMatches;

    /**
     * Constructs the comparator with the list of matches to evaluate head-to-head criteria.
     *
     * @param allMatches The list of all matches played so far.
     */
    public SerieAComparator(List<Match> allMatches) {
        this.allMatches = allMatches;
    }

    @Override
    public int compare(Team t1, Team t2) {
        if (t1.getPoints() != t2.getPoints()) {
            return Integer.compare(t2.getPoints(), t1.getPoints());
        }

        int[] h2hStats = calculateHeadToHead(t1.getName(), t2.getName());
        int h2hPointsDiff = h2hStats[0] - h2hStats[1];

        if (h2hPointsDiff != 0) {
            return h2hPointsDiff < 0 ? 1 : -1;
        }

        int h2hGdDiff = h2hStats[2] - h2hStats[3];
        if (h2hGdDiff != 0) {
            return h2hGdDiff < 0 ? 1 : -1;
        }

        if (t1.getGoalDifference() != t2.getGoalDifference()) {
            return Integer.compare(t2.getGoalDifference(), t1.getGoalDifference());
        }

        if (t1.getGoalsFor() != t2.getGoalsFor()) {
            return Integer.compare(t2.getGoalsFor(), t1.getGoalsFor());
        }

        return t1.getName().compareToIgnoreCase(t2.getName());
    }

    /**
     * Calculates head-to-head statistics between two specific teams.
     *
     * @param team1Name The name of the first team.
     * @param team2Name The name of the second team.
     * @return An integer array containing [t1Points, t2Points, t1GoalDifference, t2GoalDifference].
     */
    private int[] calculateHeadToHead(String team1Name, String team2Name) {
        int t1Points = 0;
        int t2Points = 0;
        int t1Goals = 0;
        int t2Goals = 0;

        for (Match match : allMatches) {
            if (!match.isPlayed()) continue;

            boolean isT1Home = match.getHomeTeam().equals(team1Name) && match.getAwayTeam().equals(team2Name);
            boolean isT2Home = match.getHomeTeam().equals(team2Name) && match.getAwayTeam().equals(team1Name);

            if (isT1Home) {
                t1Goals += match.getHomeScore();
                t2Goals += match.getAwayScore();
                if (match.getHomeScore() > match.getAwayScore()) t1Points += 3;
                else if (match.getHomeScore() < match.getAwayScore()) t2Points += 3;
                else { t1Points += 1; t2Points += 1; }
            } else if (isT2Home) {
                t2Goals += match.getHomeScore();
                t1Goals += match.getAwayScore();
                if (match.getHomeScore() > match.getAwayScore()) t2Points += 3;
                else if (match.getHomeScore() < match.getAwayScore()) t1Points += 3;
                else { t1Points += 1; t2Points += 1; }
            }
        }

        return new int[]{t1Points, t2Points, t1Goals - t2Goals, t2Goals - t1Goals};
    }
}