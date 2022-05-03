package query;

import com.google.gson.JsonObject;
import disk.FileManager;
import java.util.List;
import java.util.concurrent.CompletableFuture;


/**
 * Sends various asynchronous queries to GitHub's GraphQL API.
 */
public class Query {
    public static final String TARGET = "https://api.github.com/graphql";
    private final Client CLIENT;
    private final FileManager FILE_MANAGER;

    public Query() {
        CLIENT = new Client();
        FILE_MANAGER = new FileManager();
    }

    /**
     * Requests a page of repository information.
     * @param ownedCursor           Cursor to start from for querying owned repos
     * @param contributedCursor     Cursor to start from when querying contributed repos
     * @return                      JSON response
     */
    public CompletableFuture<JsonObject> repositories(String ownedCursor, String contributedCursor) {
        String query = FILE_MANAGER.loadQuery("repositories")
                .replaceFirst("__OWNED_CURSOR__", ownedCursor)
                .replaceFirst("__CONTRIBUTED_CURSOR__", contributedCursor);
        return CLIENT.asyncRequest(TARGET, query);
    }

    /**
     * Requests a list of all the years the user has been contributing for.
     * @return      JSON response
     */
    public CompletableFuture<JsonObject> contributionYears() {
        return CLIENT.asyncRequest(TARGET, FILE_MANAGER.loadQuery("contribution_years"));
    }

    /**
     * Requests total contributions for each year in YEARS.
     * @param years     List of years to query
     * @return          JSON response
     */
    public CompletableFuture<JsonObject> totalContributions(List<Integer> years) {
        String template = FILE_MANAGER.loadQuery("contributions_per_year");
        StringBuilder sb = new StringBuilder();
        sb.append("{ viewer {");
        for (int year : years) {
            sb.append(template.replace("__FROM__", String.valueOf(year))
                    .replace("__TO__", String.valueOf(year + 1)));
        }
        sb.append("} }");
        return CLIENT.asyncRequest(TARGET, sb.toString());
    }
}
