package concurrency;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import query.Query;
import query.Statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * Compiles various user statistics not bound to any specific repository.
 */
public class UserJob extends Job {
    private final List<Integer> CONTRIB_YEARS;
    private int pullRequests;
    private int commits;
    private int pullRequestReviews;
    private int totalContributions;

    public UserJob(Query query) {
        super(query);
        CONTRIB_YEARS = new ArrayList<>();
        pullRequests = 0;
        commits = 0;
        pullRequestReviews = 0;
        totalContributions = 0;

        RESPONSES.put("years", QUERY.contributionYears());
    }

    @Override
    protected void main() throws InterruptedException, ExecutionException {
        if (CONTRIB_YEARS.size() == 0) {
            JsonArray years = RESPONSES.get("years").get()
                    .get("data").getAsJsonObject()
                    .get("user").getAsJsonObject()
                    .get("contributionsCollection").getAsJsonObject()
                    .get("contributionYears").getAsJsonArray();
            for (JsonElement y : years) {
                CONTRIB_YEARS.add(y.getAsInt());
            }
            RESPONSES.put("totalContribs", QUERY.totalContributions(CONTRIB_YEARS));
        } else {
            JsonObject res = RESPONSES.get("totalContribs").get()
                    .get("data").getAsJsonObject()
                    .get("viewer").getAsJsonObject();
            for (int yr : CONTRIB_YEARS) {
                JsonObject year = res.get("y" + yr).getAsJsonObject();
                pullRequests += year.get("totalPullRequestContributions").getAsInt();
                commits += year.get("totalCommitContributions").getAsInt();
                pullRequestReviews += year.get("totalPullRequestReviewContributions").getAsInt();
                totalContributions += year.get("contributionCalendar").getAsJsonObject()
                        .get("totalContributions").getAsInt();
            }
            done = true;
            clearResponses();
        }
    }

    @Override
    public void finish(Statistics stats) {
        stats.addTo("totalContributions", totalContributions);
        stats.addTo("pullRequests", pullRequests + pullRequestReviews);
    }
}
