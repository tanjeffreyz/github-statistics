import com.google.gson.JsonObject;
import disk.FileManager;
import query.Query;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class Main {
    public static void main(String[] args) {
        Query query = new Query();
        FileManager fileManager = new FileManager();

        try {
//            System.out.println(fileManager.loadQuery("test"));
//            CompletableFuture<JsonObject> response = client.asyncRequest(
//                    "https://api.github.com/graphql",
//                    fileManager.loadQuery("repositories")
//            );
            CompletableFuture<JsonObject> response = query.repositories("null", "null");
            JsonObject json = response.get();
            json.addProperty("testDate", (new Date()).toString());
//            System.out.println(json);
            fileManager.saveOutput("test", json);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
