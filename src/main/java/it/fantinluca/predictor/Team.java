package it.fantinluca.predictor;

public class Team {
    private int id;
    private String name;
    private int points;
    private int goalsFor;
    private int goalsAgainst;
    private int matchesPlayed;


    public Team(int id, String name, int points, int goalsFor, int goalsAgainst, int matchesPlayed) {
        this.id = id;
        this.name = name;
        this.points = points;
        this.goalsFor = goalsFor;
        this.goalsAgainst = goalsAgainst;
        this.matchesPlayed = matchesPlayed;
    }

    public int getGoalDifference() {
        return goalsFor - goalsAgainst;
    }

    // TODO: Generate Getters and Setters here
}