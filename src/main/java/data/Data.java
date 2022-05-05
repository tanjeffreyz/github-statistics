package data;

import com.google.gson.JsonObject;
import disk.FileManager;

public abstract class Data {
    protected final FileManager FILE_MANAGER;
    protected String target;

    public Data(FileManager fileManager) {
        FILE_MANAGER = fileManager;
        target = "data-superclass";
    }

    /**
     * Packages and returns the data as a JSON object.
     * @return      JsonObject
     */
    protected abstract JsonObject toJson();

    /**
     * Saves the stored information as a JSON file in "/outputs".
     */
    public void export() {
        FILE_MANAGER.saveOutput(target, toJson());
    }
}
