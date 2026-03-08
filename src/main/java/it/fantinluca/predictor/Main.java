package it.fantinluca.predictor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Entry point for local testing without the web server.
 */
public class Main {

    public static void main(String[] args) {
        String apiToken = System.getenv("FOOTBALL_API_TOKEN");

        if (apiToken == null || apiToken.isEmpty()) {
            System.err.println("ERROR: Please set the FOOTBALL_API_TOKEN environment variable.");
            return;
        }

        System.out.println("Fetching data from API...");
        FootballDataClient client = new FootballDataClient(apiToken);
        String json = client.fetchMatchesJson();

        if (json.contains("\"error\"")) {
            System.err.println("Failed to fetch data: " + json);
            return;
        }

        System.out.println("Parsing matches...");
        FootballDataParser parser = new FootballDataParser();
        List<Match> allMatches = parser.parseMatches(json);

        Map<String, Team> league = new HashMap<>();
        int teamIdCounter = 1;

        for (Match m : allMatches) {
            league.putIfAbsent(m.getHomeTeam(), new Team(teamIdCounter++, m.getHomeTeam(), 0, 0, 0, 0));
            league.putIfAbsent(m.getAwayTeam(), new Team(teamIdCounter++, m.getAwayTeam(), 0, 0, 0, 0));
        }

        TableEngine engine = new TableEngine();

        for (Match m : allMatches) {
            engine.processMatch(m, league);
        }

        System.out.println("--- CURRENT SERIE A TABLE ---");
        engine.printTable(league);
    }
}