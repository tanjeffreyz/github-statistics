import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import disk.FileManager;
import query.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
            fileManager.saveOutput("test_repositories", json);

            response = query.contributionYears();
            json = response.get();
            fileManager.saveOutput("test_contribyears", json);

            List<Integer> years = new ArrayList<>();
            for (JsonElement jsonElement : json.get("data").getAsJsonObject()
                    .get("user").getAsJsonObject()
                    .get("contributionsCollection").getAsJsonObject()
                    .get("contributionYears").getAsJsonArray()) {
                years.add(jsonElement.getAsInt());
            }
            response = query.totalContributions(years);
            json = response.get();
            fileManager.saveOutput("test_total_contributions", json);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
