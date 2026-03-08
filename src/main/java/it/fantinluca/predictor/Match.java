package it.fantinluca.predictor;

/**
 * Represents a single football match including teams, scores, and crest URLs.
 */
public class Match {
    private String homeTeam;
    private String awayTeam;
    private String homeTeamCrest;
    private String awayTeamCrest;
    private int homeScore;
    private int awayScore;
    private int matchday;
    private boolean isPlayed;

    /**
     * Constructs a new Match.
     *
     * @param homeTeam The name of the home team.
     * @param awayTeam The name of the away team.
     * @param matchday The matchday number.
     */
    public Match(String homeTeam, String awayTeam, int matchday) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.matchday = matchday;
        this.isPlayed = false;
        this.homeScore = 0;
        this.awayScore = 0;
    }

    public String getHomeTeamCrest() { return homeTeamCrest; }
    public void setHomeTeamCrest(String homeTeamCrest) { this.homeTeamCrest = homeTeamCrest; }

    public String getAwayTeamCrest() { return awayTeamCrest; }
    public void setAwayTeamCrest(String awayTeamCrest) { this.awayTeamCrest = awayTeamCrest; }

    public String getHomeTeam() { return homeTeam; }
    public void setHomeTeam(String homeTeam) { this.homeTeam = homeTeam; }

    public String getAwayTeam() { return awayTeam; }
    public void setAwayTeam(String awayTeam) { this.awayTeam = awayTeam; }

    public int getHomeScore() { return homeScore; }
    public void setHomeScore(int homeScore) { this.homeScore = homeScore; }

    public int getAwayScore() { return awayScore; }
    public void setAwayScore(int awayScore) { this.awayScore = awayScore; }

    public int getMatchday() { return matchday; }
    public void setMatchday(int matchday) { this.matchday = matchday; }

    public boolean isPlayed() { return isPlayed; }
    public void setPlayed(boolean played) { this.isPlayed = played; }
}