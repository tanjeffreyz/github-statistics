package query;

import com.google.gson.JsonObject;
import disk.FileManager;
import java.util.HashMap;
import java.util.Map;


/**
 * Stores various different stats to be displayed by GitHub Overview.
 */
public class Statistics {
    private final Map<String, Integer> STATS;
    private final FileManager FILE_MANAGER;

    public Statistics(FileManager fileManager) {
        FILE_MANAGER = fileManager;
        STATS = new HashMap<>();
        String[] keys = {
                "issues",
                "pullRequests",
                "totalContributions",
                "repositories",
                "forks",
                "stars"
        };
        for (String key : keys) {
            STATS.put(key, 0);
        }
    }

    /**
     * Adds VALUE to the given statistic KEY.
     * @param key       Statistic name
     * @param value     Value to increment by
     */
    public void addTo(String key, int value) {
        if (STATS.containsKey(key)) {
            STATS.put(key, STATS.get(key) + value);
        } else {
            System.err.format("'%s' is not a valid statistic", key);
        }
    }

    /**
     * Saves final statistics into a JSON file in "/output".
     */
    public void export() {
        JsonObject json = new JsonObject();
        for (String key : STATS.keySet()) {
            json.addProperty(key, STATS.get(key));
        }
        FILE_MANAGER.saveOutput("stats", json);
    }
}
