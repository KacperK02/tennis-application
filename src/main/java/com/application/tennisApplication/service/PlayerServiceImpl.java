package com.application.tennisApplication.service;

import com.application.tennisApplication.model.Player;
import com.application.tennisApplication.repository.PlayerRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PlayerServiceImpl implements PlayerService{
    @Autowired
    private PlayerRepository playerRepository;

    @Override
    public void savePlayersFromFile() throws IOException {
        List<Player> players = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(new File("src/main/resources/API/WTAranking.json"));

        int numberOfPlayers = 500;
        for (int j = 0; j < 2; j++){
            for (int i = 0; i < numberOfPlayers; i++){
                String name = jsonNode.get("rankings").get(i).get("rowName").asText();
                String gender = jsonNode.get("rankings").get(i).get("team").get("gender").asText();
                String country = jsonNode.path("rankings").get(i).path("team").path("country").path("name").asText();
                if (country == null || country.isEmpty()) country = ""; // one of ATP Players doesn't represent any country
                int ranking = jsonNode.get("rankings").get(i).get("ranking").asInt();
                int points = jsonNode.get("rankings").get(i).get("points").asInt();
                int tournamentsPlayed = jsonNode.get("rankings").get(i).get("tournamentsPlayed").asInt();
                int teamid = jsonNode.get("rankings").get(i).get("team").get("id").asInt();

                players.add(new Player(name, gender, country, ranking, points, tournamentsPlayed, teamid));
            }
            jsonNode = objectMapper.readTree(new File("src/main/resources/API/ATPranking.json"));
        }

        playerRepository.saveAll(players);
    }

    @Override
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    @Override
    public List<Player> getAllWTAPlayers() {
        return playerRepository.findByGender("F");
    }

    @Override
    public List<Player> getAllATPPlayers() {
        return playerRepository.findByGender("M");
    }

    @Override
    public Optional<Player> getPlayerById(int id) {
        return playerRepository.findById(id);
    }
}
