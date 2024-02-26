package com.application.tennisApplication.API;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class APIConnection {

    private String APIkey;
    public APIConnection() {
        try (InputStream inputStream = APIConnection.class.getResourceAsStream("/API/key.txt")) {
            assert inputStream != null;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))){
                APIkey = br.readLine().strip();
            }
        }
        catch (IOException e){
            System.out.println("File with key to API not found");
        }
    }

    private String getResponseFromAPI(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("X-RapidAPI-Key", APIkey)
                .header("X-RapidAPI-Host", "tennisapi1.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    private void saveRankingToJSON(String name, String response){
        try(BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(name), StandardCharsets.UTF_8))){
            writer.write(response);
        } catch (IOException e) {
            System.out.println("Failed to save data to JSON [" + e + "]");
        }
    }

    public void getWTARanking() throws IOException, InterruptedException {
        String response = getResponseFromAPI("https://tennisapi1.p.rapidapi.com/api/tennis/rankings/wta");
        saveRankingToJSON("src/main/resources/API/WTAranking.json", response);
    }

    public void getATPRanking() throws IOException, InterruptedException {
        String response = getResponseFromAPI("https://tennisapi1.p.rapidapi.com/api/tennis/rankings/atp");
        saveRankingToJSON("src/main/resources/API/ATPranking.json", response);
    }

    public String getPlayerLastTournaments(String teamId) throws IOException, InterruptedException {
        return getResponseFromAPI("https://tennisapi1.p.rapidapi.com/api/tennis/team/" + teamId + "/tournaments/last");
    }

    public void getPlayerPhoto(String teamId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://tennisapi1.p.rapidapi.com/api/tennis/team/" + teamId + "/image"))
                .header("X-RapidAPI-Key", APIkey)
                .header("X-RapidAPI-Host", "tennisapi1.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<byte[]> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofByteArray());

        byte[] imageBytes = response.body();
        String filePath = "src/main/resources/static/playerPhotos/" + teamId + ".png";
        Path path = Paths.get(filePath);
        Files.write(path, imageBytes);
    }

    public String getPlayerNearEvent(String teamId) throws IOException, InterruptedException {
        return getResponseFromAPI("https://tennisapi1.p.rapidapi.com/api/tennis/team/" + teamId + "/events/near");
    }
}
