package data;

import com.google.gson.JsonObject;
import disk.FileManager;

import java.util.Map;

public abstract class Data {
    protected final String NAME;
    protected final FileManager FILE_MANAGER;

    public Data(String name, FileManager fileManager) {
        NAME = name;
        FILE_MANAGER = fileManager;
    }

    /**
     * Packages and returns the data as a map of file names to JSON objects.
     * @return      JsonObject
     */
    protected abstract Map<String, JsonObject> toJson();

    /**
     * Saves the stored information as a JSON file in "/outputs".
     */
    public void export() {
        Map<String, JsonObject> outputs = toJson();
        for (String fileName : outputs.keySet()) {
            FILE_MANAGER.saveOutput(fileName, outputs.get(fileName));
        }
    }
}
