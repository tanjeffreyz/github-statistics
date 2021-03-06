import concurrency.Job;
import concurrency.RepositoryInfoJob;
import concurrency.RepositoryStatsJob;
import concurrency.UserStatsJob;
import data.Catalog;
import disk.FileManager;
import query.Query;
import data.Statistics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        FileManager fileManager = new FileManager();
        Query query = new Query(fileManager);

        // Initialize Data objects
        Statistics statistics = new Statistics("stats", fileManager);
        Catalog catalog = new Catalog("repositories", fileManager);

        // Initialize jobs
        List<Job> jobs = new ArrayList<>();
        jobs.add(new RepositoryStatsJob(query, statistics));
        jobs.add(new UserStatsJob(query, statistics));
        jobs.add(new RepositoryInfoJob(query, catalog));

        // Run jobs
        int index = 0;
        int numFinished = 0;
        boolean[] finished = new boolean[jobs.size()];
        Arrays.fill(finished, false);
        while (numFinished < jobs.size()) {
            if (!finished[index]) {
                Job job = jobs.get(index);
                if (!job.done()) {
                    if (job.ready()) {
                        job.run();
                    }
                } else {
                    job.finish();
                    finished[index] = true;
                    numFinished++;
                }
            }
            index = (index + 1) % jobs.size();
        }

        statistics.export();
        catalog.export();
    }
}
