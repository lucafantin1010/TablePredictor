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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getGoalsFor() {
        return goalsFor;
    }

    public void setGoalsFor(int goalsFor) {
        this.goalsFor = goalsFor;
    }

    public int getGoalsAgainst() {
        return goalsAgainst;
    }

    public void setGoalsAgainst(int goalsAgainst) {
        this.goalsAgainst = goalsAgainst;
    }

    public int getMatchesPlayed() {
        return matchesPlayed;
    }

    public void setMatchesPlayed(int matchesPlayed) {
        this.matchesPlayed = matchesPlayed;
    }


}