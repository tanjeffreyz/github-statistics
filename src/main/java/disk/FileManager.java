package disk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Manages loading and saving various files.
 */
public class FileManager {
    private static final String OUTPUT_DIR = "output";

    /**
     * Loads the given query file from "/resources".
     * @param queryName     Name of the query to load
     * @return              String contents of the query
     */
    public String loadQuery(String queryName) {
        Path path = Paths.get(queryName + ".query");
        StringBuilder sb = new StringBuilder();
        InputStream in = getClass().getClassLoader().getResourceAsStream(path.toString());
        if (in != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                String line = reader.readLine();
                while (line != null) {
                    sb.append(line);
                    line = reader.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * Saves a JSON object to FILENAME.json in "/output".
     * @param fileName      Name of file to save to, ".json" extension is automatically appended
     * @param json          JSON object to save
     */
    public void saveOutput(String fileName, JsonObject json) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Path target = Paths.get(OUTPUT_DIR, fileName + ".json");
        FileWriter fileWriter = null;

        // Make directory if it doesn't exist
        File dir = target.toFile().getParentFile();
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }

        try {
            fileWriter = new FileWriter(target.toString());
            fileWriter.write(gson.toJson(json));
            fileWriter.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.flush();
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Deletes the given output file as well as its parent directory if empty
     * @param fileName      Path to file to delete
     */
    public void deleteOutput(String fileName) {
        Path target = Paths.get(OUTPUT_DIR, fileName + ".json");
        File file = target.toFile();
        File dir = file.getParentFile();
        file.delete();
        dir.delete();
    }

    /**
     * Returns the given path relative to the OUTPUT_DIR.
     * @param paths     A path or list of names that make up a path.
     * @return          The path relative to OUTPUT_DIR.
     */
    public static Path getOutputPath(String... paths) {
        return Paths.get(OUTPUT_DIR, paths);
    }
}
