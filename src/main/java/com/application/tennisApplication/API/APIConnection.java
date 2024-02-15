package com.application.tennisApplication.API;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

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

    public void getWTARanking() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://tennisapi1.p.rapidapi.com/api/tennis/rankings/wta"))
                .header("X-RapidAPI-Key", APIkey)
                .header("X-RapidAPI-Host", "tennisapi1.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        try(BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream("src/main/resources/API/WTAranking.json"), StandardCharsets.UTF_8))){
            writer.write(response.body());
        } catch (IOException e) {
            System.out.println("Failed to save data to JSON [" + e + "]");
        }
    }

    public void getATPRanking() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://tennisapi1.p.rapidapi.com/api/tennis/rankings/atp"))
                .header("X-RapidAPI-Key", APIkey)
                .header("X-RapidAPI-Host", "tennisapi1.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        try(BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream("src/main/resources/API/ATPranking.json"), StandardCharsets.UTF_8))){
            writer.write(response.body());
        } catch (IOException e) {
            System.out.println("Failed to save data to JSON [" + e + "]");
        }
    }

    public String getPlayerLastTournaments(String teamId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://tennisapi1.p.rapidapi.com/api/tennis/team/" + teamId + "/tournaments/last"))
                .header("X-RapidAPI-Key", "704e532d3amshf06af8f8d4e4b4bp1602e1jsn6eb95ee30724")
                .header("X-RapidAPI-Host", "tennisapi1.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}
