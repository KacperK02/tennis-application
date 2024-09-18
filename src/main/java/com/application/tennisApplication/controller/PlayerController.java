package com.application.tennisApplication.controller;

import com.application.tennisApplication.API.APIConnection;
import com.application.tennisApplication.model.Match;
import com.application.tennisApplication.model.Player;
import com.application.tennisApplication.model.Tournament;
import com.application.tennisApplication.model.User;
import com.application.tennisApplication.repository.PlayerRepository;
import com.application.tennisApplication.service.FollowService;
import com.application.tennisApplication.service.PlayerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
@CrossOrigin
public class PlayerController {

    @Autowired
    PlayerController playerController;
    @Autowired
    PlayerService playerService;
    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    FollowService followService;

    private void setFollowingOfPlayers(User user, List<Player> players){
        boolean isFollowing = false;
        for (Player player : players){
            if (user != null) {
                isFollowing = followService.isPlayerFollowedByUser(player.getPlayerID(), user.getUserID());
            }
            player.setFollowing(isFollowing);
        }

        players.sort(Comparator.comparingInt(Player::getRanking));
    }

    @GetMapping("/getAllWTAPlayers")
    @ResponseBody
    public List<Player> getWTAPlayers(){
        List <Player> players = playerService.getAllWTAPlayers();
        players.sort(Comparator.comparingInt(Player::getRanking));
        return players;
    }

    @GetMapping("/getAllATPPlayers")
    @ResponseBody
    public List<Player> getATPPlayers(){
        List <Player> players = playerService.getAllATPPlayers();
        players.sort(Comparator.comparingInt(Player::getRanking));
        return players;
    }

    @GetMapping("/player/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable int id) {
        Optional<Player> playerOptional = playerService.getPlayerById(id);
        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();
            return ResponseEntity.ok(player);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/player/{id}/lastMatch")
    public ResponseEntity<Match> getLastPlayerMatch(@PathVariable int id) throws JsonProcessingException {
        Optional<Player> playerOptional = playerService.getPlayerById(id);
        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();
            APIConnection apiConnection = new APIConnection();
            String response = apiConnection.getPlayerNearEvent(String.valueOf(player.getTeamid()));
            Match lastMatch = lastPlayerMatch(response);
            return ResponseEntity.ok(lastMatch);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/player/{id}/lastTournaments")
    public ResponseEntity<List<Tournament>> getPlayerLastTournaments(@PathVariable int id) throws IOException {
        Optional<Player> playerOptional = playerService.getPlayerById(id);
        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();
            APIConnection apiConnection = new APIConnection();
            String response = apiConnection.getPlayerLastTournaments(String.valueOf(player.getTeamid()));
            List <Tournament> tournaments = getPlayerTournaments(response);
            return ResponseEntity.ok(tournaments);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/player/photoExists/{teamID}")
    public ResponseEntity<Boolean> checkPhotoExists(@PathVariable String teamID) {
        String photoPath = "frontend/src/assets/playerPhotos/" + teamID + ".png";
        File file = new File(photoPath);
        if (file.exists()) {
            return ResponseEntity.ok(true);  // Zdjęcie istnieje
        } else {
            return ResponseEntity.ok(false);  // Zdjęcie nie istnieje
        }
    }

    @PostMapping("/player/fetchPhoto/{teamID}")
    public ResponseEntity<Void> fetchPlayerPhoto(@PathVariable String teamID) {
        APIConnection apiConnection = new APIConnection();
        apiConnection.getPlayerPhoto(teamID);
        return ResponseEntity.ok().build();
    }

    private List<Tournament> getPlayerTournaments(String response) throws IOException {
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

    private List<String> getPlayerInfo(Player player, String seed){
        List<String> playerInfo = new ArrayList<>();
        playerInfo.add(player.getName());
        playerInfo.add(player.getCountry());
        playerInfo.add(seed);
        return playerInfo;
    }

    private Match lastPlayerMatch(String response) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response);
        JsonNode node = jsonNode.path("previousEvent");

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
