package concurrency;

import com.google.gson.JsonObject;
import query.Query;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


/**
 * A process that queries asynchronously but writes to Statistics synchronously
 */
public abstract class Job {
    protected final Map<String, CompletableFuture<JsonObject>> RESPONSES;
    protected final Query QUERY;
    protected boolean done;

    public Job() {
        QUERY = null;
        RESPONSES = new HashMap<>();
        done = false;
    }

    public Job(Query query) {
        QUERY = query;
        RESPONSES = new HashMap<>();
        done = false;
    }

    /**
     * Runs the Job, updates DONE only if Job is completely finished.
     */
    public void run() {
        try {
            main();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Main data processing function of the Job.
     * @throws InterruptedException     Error when one of the queries is interrupted.
     * @throws ExecutionException       Thrown if trying to get result of aborted query.
     */
    protected abstract void main() throws InterruptedException, ExecutionException;

    /**
     * Checks on whether the Job is ready to proceed.
     * @return      True if job is ready to proceed, False otherwise.
     */
    public boolean ready() {
        for (CompletableFuture<JsonObject> res : RESPONSES.values()) {
            if (!res.isDone()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Clears the list of responses, usually used after each run.
     */
    protected void clearResponses() {
        RESPONSES.clear();
    }

    /**
     * Checks whether the Job is ready to finish.
     * @return      True if job is ready to finish, False otherwise
     */
    public boolean done() {
        return done;
    }

    /**
     * Performs the synchronous part of the Job, updates DATA.
     */
    public abstract void finish();
}
