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
import java.util.zip.GZIPInputStream;

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

    private String getResponseFromAPI(String url){
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("x-rapidapi-key", APIkey)
                    .header("x-rapidapi-host", "tennisapi1.p.rapidapi.com")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<byte[]> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofByteArray());
            String encoding = response.headers().firstValue("Content-Encoding").orElse("identity");

            byte[] responseBody = response.body();
            String decodedResponse;

            if ("gzip".equalsIgnoreCase(encoding)) {
                try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(responseBody));
                     BufferedReader bf = new BufferedReader(new InputStreamReader(gis, StandardCharsets.UTF_8))) {
                    StringBuilder output = new StringBuilder();
                    String line;
                    while ((line = bf.readLine()) != null) {
                        output.append(line);
                    }
                    decodedResponse = output.toString();
                }
            } else {
                decodedResponse = new String(responseBody, StandardCharsets.UTF_8);
            }

            return decodedResponse;
        }
        catch(Exception e){
            System.out.println("Failed to get data from API. " + e);
            return "Error";
        }

    }

    private void saveRankingToJSON(String name, String response){
        try(BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(name), StandardCharsets.UTF_8))){
            writer.write(response);
        } catch (IOException e) {
            System.out.println("Failed to save data to JSON [" + e + "]");
        }
    }

    public void getWTARanking() {
        String response = getResponseFromAPI("https://tennisapi1.p.rapidapi.com/api/tennis/rankings/wta");
        saveRankingToJSON("src/main/resources/API/WTAranking.json", response);
    }

    public void getATPRanking() {
        String response = getResponseFromAPI("https://tennisapi1.p.rapidapi.com/api/tennis/rankings/atp");
        saveRankingToJSON("src/main/resources/API/ATPranking.json", response);
    }

    public String getPlayerLastTournaments(String teamId) {
        return getResponseFromAPI("https://tennisapi1.p.rapidapi.com/api/tennis/team/" + teamId + "/tournaments/last");
    }

    public void getPlayerPhoto(String teamId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://tennisapi1.p.rapidapi.com/api/tennis/team/" + teamId + "/image"))
                    .header("X-RapidAPI-Key", APIkey)
                    .header("X-RapidAPI-Host", "tennisapi1.p.rapidapi.com")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<byte[]> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofByteArray());

            byte[] imageBytes = response.body();
            String filePath = "frontend/src/assets/playerPhotos/" + teamId + ".png";
            Path path = Paths.get(filePath);
            Files.write(path, imageBytes);
        }
        catch (Exception e){
            System.out.println("Failed to get data from API. " + e);
        }

    }

    public String getPlayerNearEvent(String teamId) {
        return getResponseFromAPI("https://tennisapi1.p.rapidapi.com/api/tennis/team/" + teamId + "/events/near");
    }
}
