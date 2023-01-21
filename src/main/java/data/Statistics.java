package data;

import com.google.gson.JsonObject;
import disk.FileManager;
import java.util.HashMap;
import java.util.Map;


/**
 * Stores various different stats to be displayed by GitHub Overview.
 */
public class Statistics extends Data {
    private final Map<String, Integer> STATS;

    public Statistics(String name, FileManager fileManager) {
        super(name, fileManager);
        STATS = new HashMap<>();
        String[] keys = {
                "issues",
                "pullRequests",
                "totalContributions",
                "repositories",
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
    @Override
    protected Map<String, JsonObject> getOutputs() {
        JsonObject json = new JsonObject();
        for (String key : STATS.keySet()) {
            json.addProperty(key, STATS.get(key));
        }
        Map<String, JsonObject> result = new HashMap<>();
        result.put(NAME, json);
        return result;
    }
}
