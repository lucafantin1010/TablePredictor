package it.fantinluca.predictor;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Client to fetch data from the football-data.org API.
 */
public class FootballDataClient {

    private static final String API_URL = "https://api.football-data.org/v4/competitions/SA/matches";
    private final String apiToken;
    private final HttpClient httpClient;

    /**
     * Constructs the client with the provided API token.
     *
     * @param apiToken The authentication token for football-data.org.
     */
    public FootballDataClient(String apiToken) {
        this.apiToken = apiToken;
        // Create a modern Java HttpClient
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    /**
     * Fetches the Serie A matches JSON from the external API.
     *
     * @return A JSON String containing the matches, or an error JSON if it fails.
     */
    public String fetchMatchesJson() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("X-Auth-Token", this.apiToken)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                return "{ \"error\": \"API request failed with status: " + response.statusCode() + "\" }";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "{ \"error\": \"Exception occurred while fetching data.\" }";
        }
    }
}
