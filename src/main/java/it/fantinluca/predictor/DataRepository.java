package it.fantinluca.predictor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DataRepository {

    /**
     * Reads a JSON file and converts it into a List of Match objects.
     * @param filePath The path to the JSON file.
     * @return A List containing all the matches.
     */
    public List<Match> loadMatchesFromJson(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            // Initialize Gson
            Gson gson = new Gson();

            // Tell Gson exactly what kind of List we want to create
            Type listType = new TypeToken<ArrayList<Match>>(){}.getType();

            // from text to Objects
            return gson.fromJson(reader, listType);

        } catch (Exception e) {
            System.err.println("Error reading the JSON file: " + e.getMessage());
            return new ArrayList<>(); // Return empty list to avoid crashing
        }
    }
}