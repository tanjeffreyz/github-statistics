package disk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;


public class FileManager {
    private static String OUTPUT_DIR = "output";

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

    public void saveOutput(String fileName, JsonObject json) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Path target = Paths.get(OUTPUT_DIR, fileName + ".json");
        FileWriter fileWriter = null;

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
}
