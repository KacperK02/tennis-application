package com.application.tennisApplication.service;

import com.application.tennisApplication.API.APIConnection;
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
    @Autowired FollowService followService;

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

    @Override
    public void updateRanking() throws IOException {
        List <Player> oldRankingPlayers = getAllPlayers();
        List <Player> newRankingPlayers = new ArrayList<>();
        APIConnection apiConnection = new APIConnection();
        apiConnection.getWTARanking();
        apiConnection.getATPRanking();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(new File("src/main/resources/API/WTAranking.json"));

        int numberOfPlayers = 500;
        for (int j = 0; j < 2; j++){
            for (int i = 0; i < numberOfPlayers; i++){
                String name = jsonNode.get("rankings").get(i).get("rowName").asText();
                String gender = jsonNode.get("rankings").get(i).get("team").get("gender").asText();
                String country = jsonNode.path("rankings").get(i).path("team").path("country").path("name").asText();
                if (country == null || country.isEmpty()) country = ""; // one of ATP Players doesn't represent any country
                int ranking = jsonNode.path("rankings").get(i).path("ranking").asInt();
                int points = jsonNode.path("rankings").get(i).path("points").asInt();
                int tournamentsPlayed = jsonNode.path("rankings").get(i).path("tournamentsPlayed").asInt();
                int teamid = jsonNode.path("rankings").get(i).path("team").path("id").asInt();

                Player player = playerRepository.getPlayerByTeamid(teamid);
                if (player != null){
                    player.setName(name);
                    player.setGender(gender);
                    player.setRanking(ranking);
                    player.setCountry(country);
                    player.setPoints(points);
                    player.setTournamentsPlayed(tournamentsPlayed);
                    newRankingPlayers.add(player);
                    playerRepository.save(player);
                }
                else {
                    Player newPlayer = new Player(name, gender, country, ranking, points, tournamentsPlayed, teamid);
                    newRankingPlayers.add(newPlayer);
                    playerRepository.save(newPlayer);
                }

            }
            jsonNode = objectMapper.readTree(new File("src/main/resources/API/ATPranking.json"));
        }

        //delete Players who are not in new ranking
        for (Player player : oldRankingPlayers) {
            boolean deletePlayer = true;
            for (Player newPlayer : newRankingPlayers) {
                if (player.getPlayerID() == newPlayer.getPlayerID()){
                    deletePlayer = false;
                    break;
                }
            }
            if (deletePlayer){
                followService.deleteFollowsOfPlayer(player);
                playerRepository.delete(player);
            }
        }

    }

}
