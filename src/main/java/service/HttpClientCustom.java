package service;

import entity.CatsGrade;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;

public class HttpClientCustom {
    private static final String CATS_URL =
            "https://raw.githubusercontent.com/netology-code/jd-homeworks/master/http/task1/cats";

    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();

        HttpClient client = getHttpClient();
        HttpRequest request = getHttpRequest();
        try {
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            if (response.statusCode() == 200) {
                try (InputStream jsonResponse = response.body()) {
                    List<CatsGrade> catsGradeList = mapper.readValue(jsonResponse, new TypeReference<List<CatsGrade>>() {
                    });
                    catsGradeList.stream()
                            .filter(c -> c.getUpvotes() != null && c.getUpvotes() > 0)
                            .sorted(Comparator.comparingInt(CatsGrade::getUpvotes))
                            .forEach(System.out::println);
                }
            } else {
                System.err.println(response.statusCode());
            }
        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static HttpClient getHttpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();
    }

    private static HttpRequest getHttpRequest() {
        return HttpRequest.newBuilder(URI.create(CATS_URL))
                .GET()
                .setHeader("Accept", "application/json")
                .timeout(Duration.ofSeconds(30))
                .build();
    }
}
