import com.google.gson.JsonObject;
import query.Client;
import disk.FileManager;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class Main {
    public static void main(String[] args) {
        Client client = new Client();
        FileManager fileManager = new FileManager();

        try {
            System.out.println(fileManager.loadQuery("test"));
            CompletableFuture<JsonObject> response = client.asyncRequest(
                    "https://api.github.com/graphql",
                    fileManager.loadQuery("test")
            );
            JsonObject json = response.get();
            json.addProperty("testDate", (new Date()).toString());
            System.out.println(json);
            fileManager.saveOutput("test1", json);
        } catch (URISyntaxException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void testSaveFile() {
        System.out.println("test");
    }
}
