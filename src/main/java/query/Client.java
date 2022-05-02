package query;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.*;
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

        HttpRequest req = HttpRequest.newBuilder()
                .uri(new URI(url))
                .header("Authorization", "bearer " + accessToken)
                .GET()
                .build();

        return CLIENT
                .sendAsync(req, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(JsonParser::parseString)
                .thenApply(JsonElement::getAsJsonObject);
    }
}
