package it.fantinluca.predictor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses the raw JSON response from the external API into Match objects.
 */
public class FootballDataParser {

    /**
     * Converts the API JSON string into a list of Match domain objects,
     * extracting team names, crest URLs, and match results safely.
     *
     * @param jsonString The raw JSON string from the API.
     * @return A list of parsed Match objects.
     */
    public List<Match> parseMatches(String jsonString) {
        List<Match> matches = new ArrayList<>();

        try {
            JsonObject root = JsonParser.parseString(jsonString).getAsJsonObject();
            if (!root.has("matches")) {
                return matches;
            }

            JsonArray matchesArray = root.getAsJsonArray("matches");

            for (JsonElement element : matchesArray) {
                JsonObject matchObj = element.getAsJsonObject();

                // Safe extraction of team objects
                JsonObject homeObj = matchObj.has("homeTeam") ? matchObj.getAsJsonObject("homeTeam") : new JsonObject();
                JsonObject awayObj = matchObj.has("awayTeam") ? matchObj.getAsJsonObject("awayTeam") : new JsonObject();

                String homeTeam = homeObj.has("name") && !homeObj.get("name").isJsonNull() ? homeObj.get("name").getAsString() : "Unknown Home";
                String awayTeam = awayObj.has("name") && !awayObj.get("name").isJsonNull() ? awayObj.get("name").getAsString() : "Unknown Away";

                int matchday = matchObj.has("matchday") && !matchObj.get("matchday").isJsonNull() ? matchObj.get("matchday").getAsInt() : 0;

                Match match = new Match(homeTeam, awayTeam, matchday);

                // Safe extraction of crest URLs with fallback
                String defaultCrest = "https://ui-avatars.com/api/?background=random&color=fff&rounded=true&bold=true&name=";

                if (homeObj.has("crest") && !homeObj.get("crest").isJsonNull()) {
                    match.setHomeTeamCrest(homeObj.get("crest").getAsString());
                } else {
                    match.setHomeTeamCrest(defaultCrest + homeTeam.replace(" ", "+"));
                }

                if (awayObj.has("crest") && !awayObj.get("crest").isJsonNull()) {
                    match.setAwayTeamCrest(awayObj.get("crest").getAsString());
                } else {
                    match.setAwayTeamCrest(defaultCrest + awayTeam.replace(" ", "+"));
                }

                String status = matchObj.has("status") && !matchObj.get("status").isJsonNull() ? matchObj.get("status").getAsString() : "SCHEDULED";

                if ("FINISHED".equals(status) && matchObj.has("score")) {
                    match.setPlayed(true);
                    JsonObject fullTime = matchObj.getAsJsonObject("score").getAsJsonObject("fullTime");

                    if (fullTime != null && fullTime.has("home") && !fullTime.get("home").isJsonNull() &&
                            fullTime.has("away") && !fullTime.get("away").isJsonNull()) {
                        match.setHomeScore(fullTime.get("home").getAsInt());
                        match.setAwayScore(fullTime.get("away").getAsInt());
                    }
                }

                matches.add(match);
            }
        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
        }

        return matches;
    }
}