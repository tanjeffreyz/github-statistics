package data;

import com.google.gson.JsonObject;
import disk.FileManager;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Data {
    protected final String NAME;
    protected final FileManager FILE_MANAGER;
    protected final Set<String> DELETED;

    public Data(String name, FileManager fileManager) {
        NAME = name;
        FILE_MANAGER = fileManager;
        DELETED = new HashSet<>();
    }

    /**
     * Packages and returns the data as a map of file names to JSON objects.
     * @return      Map of file paths to JSON objects
     */
    protected abstract Map<String, JsonObject> getOutputs();

    /**
     * Saves the stored information as a JSON file in "/outputs".
     */
    public void export() {
        Map<String, JsonObject> outputs = getOutputs();
        for (String fileName : DELETED) {
            FILE_MANAGER.deleteOutput(fileName);
        }
        for (String fileName : outputs.keySet()) {
            FILE_MANAGER.saveOutput(fileName, outputs.get(fileName));
        }
    }

    /**
     * Returns PATHS relative to this Data object's home directory.
     * @param paths     Paths to join
     * @return          A path relative to this Data's home directory
     */
    public Path getDataPath(String... paths) {
        return Paths.get(NAME, paths);
    }
}
