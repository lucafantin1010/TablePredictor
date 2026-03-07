package it.fantinluca.predictor;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        // 1. Create our Teams (starting at day 0: 0 points, 0 goals, etc.)
        Team juventus = new Team(1, "Juventus", 0, 0, 0, 0);
        Team pisa = new Team(2, "Pisa", 0, 0, 0, 0);

        // 2. Put them in our "Database" (The Map)
        // The key MUST match the exact name we will use in the matches
        Map<String, Team> league = new HashMap<>();
        league.put("Juventus", juventus);
        league.put("Pisa", pisa);

        // 3. Print pre-match situation
        System.out.println("--- BEFORE THE MATCH ---");
        System.out.println("Juventus points: " + juventus.getPoints());
        System.out.println("Pisa points: " + pisa.getPoints());

        // 4. Create the match: Juventus vs Pisa, Matchday 1
        Match match1 = new Match("Juventus", "Pisa", 1);

        // Let's say Juventus wins 2-0
        match1.setHomeScore(2);
        match1.setAwayScore(0);
        match1.setPlayed(true);

        // 5. Fire up the Engine!
        TableEngine engine = new TableEngine();
        engine.processMatch(match1, league);

        // 6. Print post-match situation
        System.out.println("\n--- AFTER THE MATCH ---");
        System.out.println("Juventus points: " + juventus.getPoints() +
                " (Goals For: " + juventus.getGoalsFor() +
                ", Goals Against: " + juventus.getGoalsAgainst() + ")");

        System.out.println("Pisa points: " + pisa.getPoints() +
                " (Goals For: " + pisa.getGoalsFor() +
                ", Goals Against: " + pisa.getGoalsAgainst() + ")");

        System.out.println("Goal Difference for Juve: " + juventus.getGoalDifference());
        System.out.println("Goal Difference for Pisa: " + pisa.getGoalDifference());

        //print the standings
        engine.printTable(league);
    }
}