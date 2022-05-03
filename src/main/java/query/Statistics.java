package query;

import java.util.HashMap;
import java.util.Map;


/**
 * Stores various different stats to be displayed by GitHub Overview.
 */
public class Statistics {
    private final Map<String, Integer> STATS;

    public Statistics() {
        STATS = new HashMap<>();
        String[] keys = {
                "issues",
                "pullRequests",
                "totalContributions",
                "repositories"
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
        }
    }

    /**
     * Saves final statistics into a JSON file in "/output".
     */
    public void export() {

    }
}
