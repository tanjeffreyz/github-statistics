package concurrency;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import query.Query;
import data.Statistics;
import java.util.*;
import java.util.concurrent.ExecutionException;


/**
 * Compiles various public and private stats across all owned and contributed repositories.
 */
public class RepositoryStatsJob extends Job {
    private final Statistics DATA;
    private final Set<String> IGNORED_REPOS;
    private final Set<String> REPOS;
    private final List<String> OWNED_REPOS;
    private final List<String> CONTRIB_REPOS;

    private String ownedCursor;
    private String contribCursor;
    private int numRepos;
    private int stars;
    private int forks;
    private int closedIssues;

    public RepositoryStatsJob(Query query, Statistics data) {
        super(query);
        ownedCursor = "null";
        contribCursor = "null";
        numRepos = 0;
        stars = 0;
        forks = 0;
        closedIssues = 0;
        DATA = data;
        REPOS = new HashSet<>();
        OWNED_REPOS = new ArrayList<>();
        CONTRIB_REPOS = new ArrayList<>();

        // Compile ignored repositories
        IGNORED_REPOS = new HashSet<>();
        String ignored = System.getenv("IGNORED_REPOSITORIES");
        if (ignored != null) {
            for (String name : ignored.split(",")) {
                IGNORED_REPOS.add(name.strip());
            }
        }

        RESPONSES.put("repos", QUERY.repositoryStats(ownedCursor, contribCursor));
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
        ownedCursor = "null";
        if (ownedNext) {
            ownedCursor = String.format("\"%s\"", pageInfo.get("endCursor").getAsString());
        }

        JsonArray nodes = owned.get("nodes").getAsJsonArray();
        for (JsonElement r : nodes) {
            JsonObject repo = r.getAsJsonObject();
            String name = repo.get("nameWithOwner").getAsString();
            if (!REPOS.contains(name) && !IGNORED_REPOS.contains(name)) {
                stars += repo.get("stargazers").getAsJsonObject()
                        .get("totalCount").getAsInt();
                closedIssues += repo.get("issues").getAsJsonObject()
                        .get("totalCount").getAsInt();
                forks += repo.get("forkCount").getAsInt();
                numRepos++;
                REPOS.add(name);
                OWNED_REPOS.add(name);
            }
        }

        // Process contributed repositories
        pageInfo = contrib.get("pageInfo").getAsJsonObject();
        boolean contribNext = pageInfo.get("hasNextPage").getAsBoolean();
        contribCursor = "null";
        if (contribNext) {
            contribCursor = String.format("\"%s\"", pageInfo.get("endCursor").getAsString());
        }

        nodes = contrib.get("nodes").getAsJsonArray();
        for (JsonElement r : nodes) {
            JsonObject repo = r.getAsJsonObject();
            String name = repo.get("nameWithOwner").getAsString();
            if (!REPOS.contains(name) && !IGNORED_REPOS.contains(name)) {
                stars += repo.get("stargazers").getAsJsonObject()
                        .get("totalCount").getAsInt();
                numRepos++;
                REPOS.add(name);
                CONTRIB_REPOS.add(name);
            }
        }

        // Set up next run if needed
        if (!contribNext && !ownedNext) {
            done = true;
            clearResponses();
        } else {
            RESPONSES.put("repos", QUERY.repositoryStats(ownedCursor, contribCursor));
        }
    }

    private String getHeaderBlock(String header) {
        String whitespace = " ".repeat(4);
        StringBuilder sb = new StringBuilder();

        sb.append("/".repeat(Math.max(0, header.length() + 2 * whitespace.length() + 4)));
        String border = sb.toString();
        sb.append("\n");

        sb.append("//").append(whitespace).append(header).append(whitespace).append("//");
        sb.append("\n");

        sb.append(border);
        return sb.toString();
    }

    @Override
    public void finish() {
        DATA.addTo("issues", closedIssues);
        DATA.addTo("stars", stars);
        DATA.addTo("forks", forks);
        DATA.addTo("repositories", numRepos);

        // Print parsed repositories
        System.out.println(getHeaderBlock("Owned Repositories"));
        for (String repo : OWNED_REPOS) {
            System.out.println(repo);
        }
        System.out.println("\n");
        System.out.println(getHeaderBlock("Contributed Repositories"));
        for (String repo : CONTRIB_REPOS) {
            System.out.println(repo);
        }
    }
}
