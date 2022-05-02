package disk;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;


public class FileManager {
    public FileManager() {

    }

    public String loadQuery(String queryName) {
        Path path = Paths.get("queries", queryName + ".txt");
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
}
