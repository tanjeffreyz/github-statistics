package query;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;


public class Client {
    private HttpClient CLIENT;

    public Client() {
        CLIENT = HttpClient.newHttpClient();
    }

    public CompletableFuture<JsonObject> asyncRequest(String url, String query)
            throws URISyntaxException {
        String accessToken = System.getenv("ACCESS_TOKEN");
        if (accessToken == null) {
            accessToken = "";
        }

        JsonObject body = new JsonObject();
        body.addProperty("query", query);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "bearer " + accessToken)
                .POST(HttpRequest.BodyPublishers.ofString(
                        body.toString(),
                        StandardCharsets.UTF_8))
                .build();

        return CLIENT
                .sendAsync(req, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(JsonParser::parseString)
                .thenApply(JsonElement::getAsJsonObject);
    }
}
