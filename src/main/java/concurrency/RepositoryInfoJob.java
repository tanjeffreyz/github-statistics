package concurrency;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import data.Catalog;
import query.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Compiles various information about public owned and contributed repositories.
 */
public class RepositoryInfoJob extends Job{
    private final Catalog DATA;
    private final List<JsonObject> REPOS;
    private static final Set<String> IGNORED_LANGUAGES = Set.of("html", "css", "scss");

    private String ownedCursor;
    private String contribCursor;

    public RepositoryInfoJob(Query query, Catalog data) {
        super(query);
        ownedCursor = "null";
        contribCursor = "null";
        REPOS = new ArrayList<>();
        DATA = data;

        RESPONSES.put("repos", QUERY.repositoryInfo(ownedCursor, contribCursor));
    }

    @Override
    protected void main() throws InterruptedException, ExecutionException {
        JsonObject res = RESPONSES.get("repos").get()
                .get("data").getAsJsonObject();
        JsonObject owned = res.get("viewer").getAsJsonObject()
                .get("repositories").getAsJsonObject();
        JsonObject contrib = res.get("viewer").getAsJsonObject()
                .get("repositoriesContributedTo").getAsJsonObject();

        // Process owned repositories
        JsonObject pageInfo = owned.get("pageInfo").getAsJsonObject();
        boolean ownedNext = pageInfo.get("hasNextPage").getAsBoolean();
        ownedCursor = ownedNext ? pageInfo.get("endCursor").getAsString() : "null";

        JsonArray nodes = owned.get("nodes").getAsJsonArray();
        for (JsonElement r : nodes) {
            JsonObject repo = r.getAsJsonObject();
            filterLanguages(repo);
            REPOS.add(repo);
        }

        // Process contributed repositories
        pageInfo = contrib.get("pageInfo").getAsJsonObject();
        boolean contribNext = pageInfo.get("hasNextPage").getAsBoolean();
        contribCursor = contribNext ? pageInfo.get("endCursor").getAsString() : "null";

        nodes = contrib.get("nodes").getAsJsonArray();
        for (JsonElement r : nodes) {
            JsonObject repo = r.getAsJsonObject();
            filterLanguages(repo);
            REPOS.add(repo);
        }

        // Set up next run if needed
        if (!contribNext && !ownedNext) {
            done = true;
            clearResponses();
        } else {
            RESPONSES.put("repos", QUERY.repositoryInfo(ownedCursor, contribCursor));
        }
    }

    @Override
    public void finish() {
        for (JsonObject repo : REPOS) {
            String owner = repo.get("owner").getAsJsonObject()
                    .get("login").getAsString();
            int stars = repo.get("stargazers").getAsJsonObject()
                    .get("totalCount").getAsInt();
            int forks = repo.get("forkCount").getAsInt();
            JsonArray languages = repo.get("languages").getAsJsonObject()
                    .get("nodes").getAsJsonArray();
            repo.addProperty("owner", owner);
            repo.remove("stargazers");
            repo.addProperty("stars", stars);
            repo.remove("forkCount");
            repo.addProperty("forks", forks);
            repo.add("languages", languages);

            DATA.append(repo);
        }
    }

    private static void filterLanguages(JsonObject repo) {
        JsonArray nodes = repo.get("languages").getAsJsonObject()
                .get("nodes").getAsJsonArray();

        // Keep languages that are not in IGNORED_LANGUAGES
        JsonArray filtered = new JsonArray();
        for (JsonElement node : nodes) {
            String name = node.getAsJsonObject().get("name").getAsString().toLowerCase();
            if (!IGNORED_LANGUAGES.contains(name)) {
                filtered.add(node);
            }
        }

        // Replace old languages with filtered list
        repo.get("languages").getAsJsonObject().add("nodes", filtered);
    }
}
