package data;

import com.google.gson.JsonObject;
import disk.FileManager;

import java.io.File;
import java.nio.file.Path;
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
    protected Map<String, JsonObject> getOutputs() {
        // List all files in this Catalog's directory
        List<String> allFiles = new ArrayList<>();
        Path thisDir = FileManager.getOutputPath(NAME);
        File[] allOwners = thisDir.toFile().listFiles();
        if (allOwners != null) {
            for (File owner : allOwners) {
                File[] outputFiles = owner.listFiles();
                if (outputFiles != null) {
                    for (File output : outputFiles) {
                        Path path = getDataPath(
                                output.getParentFile().getName(),
                                output.getName().replace(".json", "")
                        );
                        allFiles.add(path.toString());
                    }
                }
            }
        }

        // Compile file paths to be saved
        Map<String, JsonObject> result = new HashMap<>();
        for (JsonObject repo : REPOS) {
            Path path = getDataPath(
                    repo.get("owner").getAsString(),
                    repo.get("name").getAsString()
            );
            result.put(path.toString(), repo);
        }

        // Delete repositories that were not queried
        for (String file : allFiles) {
            if (!result.containsKey(file)) {
                DELETED.add(file);
            }
        }

        return result;
    }
}
