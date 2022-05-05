package concurrency;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import query.Query;
import query.Statistics;
import java.util.*;
import java.util.concurrent.ExecutionException;


/**
 * Compiles various stats across all owned and contributed repositories.
 */
public class AllRepositoriesJob extends Job {
    private final Set<String> IGNORED_REPOS;
    private final Set<String> REPOS;

    private String ownedCursor;
    private String contribCursor;
    private int numRepos;
    private int stars;
    private int forks;
    private int closedIssues;

    public AllRepositoriesJob(Query query) {
        super(query);
        ownedCursor = "null";
        contribCursor = "null";
        numRepos = 0;
        stars = 0;
        forks = 0;
        closedIssues = 0;
        REPOS = new HashSet<>();

        // Compile ignored repositories
        IGNORED_REPOS = new HashSet<>();
        String ignored = System.getenv("IGNORED_REPOSITORIES");
        if (ignored != null) {
            for (String name : ignored.split(";")) {
                IGNORED_REPOS.add(name.strip());
            }
        }

        RESPONSES.put("repos", QUERY.repositories(ownedCursor, contribCursor));
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
            String name = repo.get("nameWithOwner").getAsString();
            if (!IGNORED_REPOS.contains(name)) {
                stars += repo.get("stargazers").getAsJsonObject()
                        .get("totalCount").getAsInt();
                closedIssues += repo.get("issues").getAsJsonObject()
                        .get("totalCount").getAsInt();
                forks += repo.get("forkCount").getAsInt();
                numRepos++;
                REPOS.add(name);
            }
        }

        // Process contributed repositories
        pageInfo = contrib.get("pageInfo").getAsJsonObject();
        boolean contribNext = pageInfo.get("hasNextPage").getAsBoolean();
        contribCursor = contribNext ? pageInfo.get("endCursor").getAsString() : "null";

        nodes = contrib.get("nodes").getAsJsonArray();
        for (JsonElement r : nodes) {
            JsonObject repo = r.getAsJsonObject();
            String name = repo.get("nameWithOwner").getAsString();
            if (!IGNORED_REPOS.contains(name)) {
                stars += repo.get("stargazers").getAsJsonObject()
                        .get("totalCount").getAsInt();
                forks += repo.get("forkCount").getAsInt();
                numRepos++;
                REPOS.add(name);
            }
        }

        // Set up next run if needed
        if (!contribNext && !ownedNext) {
            done = true;
            clearResponses();
        } else {
            RESPONSES.put("repos", QUERY.repositories(ownedCursor, contribCursor));
        }
    }

    @Override
    public void finish(Statistics stats) {
        stats.addTo("issues", closedIssues);
        stats.addTo("stars", stars);
        stats.addTo("forks", forks);
        stats.addTo("repositories", numRepos);
    }
}
