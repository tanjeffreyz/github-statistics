package query;

import com.google.gson.JsonObject;
import disk.FileManager;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Query {
    public static final String TARGET = "https://api.github.com/graphql";
    private Client client;
    private FileManager fileManager;

    public Query() {
        client = new Client();
        fileManager = new FileManager();
    }

    public CompletableFuture<JsonObject> repositories(String ownedCursor, String contributedCursor) {
        String query = fileManager.loadQuery("repositories")
                .replaceFirst("__OWNED_CURSOR__", ownedCursor)
                .replaceFirst("__CONTRIBUTED_CURSOR__", contributedCursor);
        return client.asyncRequest(TARGET, query);
    }

    public CompletableFuture<JsonObject> contributionYears() {
        return client.asyncRequest(TARGET, fileManager.loadQuery("contribution_years"));
    }

    public CompletableFuture<JsonObject> totalContributions(List<Integer> years) {
        String template = fileManager.loadQuery("contributions_per_year");
        StringBuilder sb = new StringBuilder();
        sb.append("{ viewer {");
        for (int year : years) {
            sb.append(template.replace("__FROM__", String.valueOf(year))
                    .replace("__TO__", String.valueOf(year + 1)));
        }
        sb.append("} }");
        return client.asyncRequest(TARGET, sb.toString());
    }
}
