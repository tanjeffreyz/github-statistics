import concurrency.Job;
import concurrency.RepositoriesJob;
import concurrency.UserJob;
import disk.FileManager;
import query.Query;
import query.Statistics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        FileManager fileManager = new FileManager();
        Statistics statistics = new Statistics(fileManager);
        Query query = new Query(fileManager);
        List<Job> jobs = new ArrayList<>();

        jobs.add(new RepositoriesJob(query));
        jobs.add(new UserJob(query));

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
                    job.finish(statistics);
                    finished[index] = true;
                    numFinished++;
                }
            }
            index = (index + 1) % jobs.size();
        }

        statistics.export();
    }
}
