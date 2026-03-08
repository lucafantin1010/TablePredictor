package it.fantinluca.predictor;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Main Servlet serving the clean, parsed list of matches to the frontend.
 */
@WebServlet(name = "ApiServlet", urlPatterns = {"/api/standings"})
public class ApiServlet extends HttpServlet {

    private FootballDataClient apiClient;
    private FootballDataParser parser;
    private Gson gson;

    @Override
    public void init() {
        String apiToken = System.getenv("FOOTBALL_API_TOKEN");

        if (apiToken == null || apiToken.isEmpty()) {
            System.err.println("CRITICAL: FOOTBALL_API_TOKEN environment variable is missing!");
        }

        this.apiClient = new FootballDataClient(apiToken);
        this.parser = new FootballDataParser();
        this.gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String rawJson = apiClient.fetchMatchesJson();

        // Parse the raw JSON into our domain objects
        List<Match> matches = parser.parseMatches(rawJson);

        // Convert the clean list back to a lightweight JSON for the frontend
        String cleanJson = gson.toJson(matches);

        try (PrintWriter out = response.getWriter()) {
            out.print(cleanJson);
            out.flush();
        }
    }
}