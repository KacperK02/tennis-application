package com.application.tennisApplication.service;

import com.application.tennisApplication.API.APIConnection;
import com.application.tennisApplication.model.Match;
import com.application.tennisApplication.model.Player;
import com.application.tennisApplication.model.Tournament;
import com.application.tennisApplication.repository.PlayerRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class PlayerServiceImpl implements PlayerService{
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private FollowService followService;

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

    @Override
    public List<Tournament> getPlayerTournaments(String response) throws JsonProcessingException {
        List <Tournament> tournaments = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response);
        JsonNode jsonNode2 = jsonNode.path("uniqueTournaments");

        for (JsonNode node : jsonNode2){
            String name = node.path("name").asText();
            boolean winner = node.path("winner").asBoolean();

            String round;
            if (winner){
                round = "WYGRANA";
            }
            else {
                round = node.path("round").asText();
                if (round == null || round.isEmpty()) round = "przegrana";
            }

            int points;
            String rank;
            if (node.path("uniqueTournament").path("tennisPoints").isMissingNode()){
                rank = "-";
            } else {
                points = node.path("uniqueTournament").path("tennisPoints").asInt();
                if (points == 2000) rank = "Wielki Szlem";
                else rank = String.valueOf(points);
            }

            tournaments.add(new Tournament(name, round, rank));
        }
        Collections.reverse(tournaments);
        return tournaments;
    }

    @Override
    public List<String> getPlayerInfo(Player player, String seed) {
        List<String> playerInfo = new ArrayList<>();
        playerInfo.add(player.getName());
        playerInfo.add(player.getCountry());
        playerInfo.add(seed);
        return playerInfo;
    }

    @Override
    public Match getPlayerMatch(String response, String whichMatch) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response);
        JsonNode node;
        if (Objects.equals(whichMatch, "previous")) {
            node = jsonNode.path("previousEvent");
        }
        else {
            node = jsonNode.path("nextEvent");
        }

        if (node.isEmpty()) return null;

        String nameOfTournament = node.path("season").path("name").asText();
        String rankOfTournament;
        int points = node.path("tournament").path("uniqueTournament").path("tennisPoints").asInt();
        if (points == 2000) rankOfTournament = "Wielki Szlem";
        else rankOfTournament = String.valueOf(points);
        String round = node.path("roundInfo").path("name").asText();
        String status = node.path("status").path("type").asText();
        int winner = node.path("winnerCode").asInt();

        int firstPlayerTeamId = node.path("homeTeam").path("id").asInt();
        int secondPlayerTeamId = node.path("awayTeam").path("id").asInt();
        Player player1 = playerRepository.getPlayerByTeamid(firstPlayerTeamId);
        Player player2 = playerRepository.getPlayerByTeamid(secondPlayerTeamId);
        String seed1 = node.path("homeTeamSeed").asText();
        String seed2 = node.path("awayTeamSeed").asText();
        List<String> firstPlayerInfo = getPlayerInfo(player1, seed1);
        List<String> secondPlayerInfo = getPlayerInfo(player2, seed2);

        if (Objects.equals(whichMatch, "next")) {
            return new Match(nameOfTournament, rankOfTournament, round, status, firstPlayerInfo, secondPlayerInfo, -1, null, null);
        }

        List <Integer> firstPlayerScore = new ArrayList<>();
        firstPlayerScore.add(node.path("homeScore").path("period1").asInt());
        firstPlayerScore.add(node.path("homeScore").path("period2").asInt());
        firstPlayerScore.add(node.path("homeScore").path("period3").asInt());
        firstPlayerScore.add(node.path("homeScore").path("period4").asInt());
        firstPlayerScore.add(node.path("homeScore").path("period5").asInt());

        List <Integer> secondPlayerScore = new ArrayList<>();
        secondPlayerScore.add(node.path("awayScore").path("period1").asInt());
        secondPlayerScore.add(node.path("awayScore").path("period2").asInt());
        secondPlayerScore.add(node.path("awayScore").path("period3").asInt());
        secondPlayerScore.add(node.path("awayScore").path("period4").asInt());
        secondPlayerScore.add(node.path("awayScore").path("period5").asInt());

        return new Match(nameOfTournament, rankOfTournament, round, status, firstPlayerInfo, secondPlayerInfo, winner, firstPlayerScore, secondPlayerScore);
    }
}
