package it.fantinluca.predictor;

import java.util.Comparator;

public class StandardTableComparator implements Comparator<Team> {

    @Override
    public int compare(Team t1, Team t2) {

        // 1. Points (Descending order: we compare t2 to t1 so the highest goes first)
        if (t1.getPoints() != t2.getPoints()) {
            return Integer.compare(t2.getPoints(), t1.getPoints());
        }

        // 2. Goal Difference (Descending order)
        if (t1.getGoalDifference() != t2.getGoalDifference()) {
            return Integer.compare(t2.getGoalDifference(), t1.getGoalDifference());
        }

        // 3. Goals For (Descending order)
        if (t1.getGoalsFor() != t2.getGoalsFor()) {
            return Integer.compare(t2.getGoalsFor(), t1.getGoalsFor());
        }

        // 4. Alphabetical order as a fallback (Ascending order: t1 compared to t2)
        return t1.getName().compareToIgnoreCase(t2.getName());
    }
}