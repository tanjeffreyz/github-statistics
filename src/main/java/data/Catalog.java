package data;

import com.google.gson.JsonObject;
import disk.FileManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data type for creating and exporting list-like data.
 */
public class Catalog extends Data {
    private final List<JsonObject> REPOS;

    public Catalog(String name, FileManager fileManager) {
        super(name, fileManager);
        REPOS = new ArrayList<>();
    }

    public void append(JsonObject jsonObject) {
        REPOS.add(jsonObject);
    }

    @Override
    protected Map<String, JsonObject> toJson() {
        Map<String, JsonObject> result = new HashMap<>();
        for (JsonObject repo : REPOS) {
            String fileName = NAME
                    + File.separator
                    + repo.get("owner").getAsString()
                    + File.separator
                    + repo.get("name").getAsString();
            result.put(fileName, repo);
        }
        return result;
    }
}
