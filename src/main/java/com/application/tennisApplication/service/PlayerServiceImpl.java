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
    public Optional<Player> getPlayerByTeamId(int teamid) {
        return Optional.ofNullable(playerRepository.getPlayerByTeamid(teamid));
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
    public String translateRound(String englishRound) {
        String round = englishRound;
        switch (englishRound) {
            case "Final" -> round = "FINAŁ";
            case "Semifinals" -> round = "półfinał";
            case "Quarterfinals" -> round = "ćwierćfinał";
            case "Round of 16" -> round = "1/8 finału";
            case "Round of 32" -> round = "1/16 finału";
            case "Round of 64" -> round = "1/32 finału";
            case "Round of 128" -> round = "1/64 finału";
            case "Match for 3rd place" -> round = "Mecz o 3. miejsce";
        }
        return round;
    }

    @Override
    public HashMap<String, String> getPlayerMatchStats(JsonNode node, String player) {
        //aces, double faults, 1st service, 1st serve point won, 2nd service points won, fh winners, bh winners,
        // fh ue, bh ue, break points converted
        HashMap<String, String> playerStats = new HashMap<>();
        String stat;

        for (int i = 0; i < node.size(); i++) {
            if (Objects.equals(node.path(i).path("groupName").asText(), "Service")) {
                stat = node.path(i).path("statisticsItems").path(0).path(player).asText(null);
                playerStats.put("aces", Objects.requireNonNullElse(stat, "-"));

                stat = node.path(i).path("statisticsItems").path(1).path(player).asText(null);
                playerStats.put("doubleFaults", Objects.requireNonNullElse(stat, "-"));

                stat = node.path(i).path("statisticsItems").path(2).path(player).asText(null);
                playerStats.put("firstService", Objects.requireNonNullElse(stat, "-"));

                stat = node.path(i).path("statisticsItems").path(4).path(player).asText(null);
                playerStats.put("firstServicePointsWon", Objects.requireNonNullElse(stat, "-"));

                stat = node.path(i).path("statisticsItems").path(5).path(player).asText(null);
                playerStats.put("secondServicePointsWon", Objects.requireNonNullElse(stat, "-"));
            }

            if (Objects.equals(node.path(i).path("groupName").asText(), "Winners")) {
                stat = node.path(i).path("statisticsItems").path(0).path(player).asText(null);
                playerStats.put("forhendWinners", Objects.requireNonNullElse(stat, "-"));

                stat = node.path(i).path("statisticsItems").path(1).path(player).asText(null);
                playerStats.put("bekhendWinners", Objects.requireNonNullElse(stat, "-"));
            }

            if (Objects.equals(node.path(i).path("groupName").asText(), "Unforced errors")) {
                stat = node.path(i).path("statisticsItems").path(0).path(player).asText(null);
                playerStats.put("forhendUnforcedErrors", Objects.requireNonNullElse(stat, "-"));

                stat = node.path(i).path("statisticsItems").path(1).path(player).asText(null);
                playerStats.put("bekhendUnforcedErrors", Objects.requireNonNullElse(stat, "-"));
            }

            if (Objects.equals(node.path(i).path("groupName").asText(), "Return")) {
                stat = node.path(i).path("statisticsItems").path(3).path(player).asText(null);
                playerStats.put("breakPointsConverted", Objects.requireNonNullElse(stat, "-"));
            }
        }

        return playerStats;
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
                else round = translateRound(round);
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
        playerInfo.add(String.valueOf(player.getTeamid()));
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

        int id = node.path("id").asInt();

        String nameOfTournament = node.path("season").path("name").asText();
        if (nameOfTournament.equals("")) nameOfTournament = node.path("tournament").path("name").asText();

        String surface = node.path("tournament").path("uniqueTournament").path("groundType").asText();
        switch(surface) {
            case "Hardcourt outdoor" -> surface = "twarda";
            case "Hardcourt indoor" -> surface = "twarda (hala)";
            case "Red clay" -> surface = "ziemna";
            case "Red clay indoor" -> surface = "ziemna (hala)";
            case "grass" -> surface = "trawiasta";
        }

        String rankOfTournament;
        int points = node.path("tournament").path("uniqueTournament").path("tennisPoints").asInt();
        if (points == 2000) {
            rankOfTournament = "Wielki Szlem";
        }
        else {
            if (points == 0) rankOfTournament = "-";
            else rankOfTournament = String.valueOf(points);
        }

        String round = node.path("roundInfo").path("name").asText();
        round = translateRound(round);

        int winner = node.path("winnerCode").asInt();

        String status = node.path("status").path("type").asText();
        switch (status) {
            case "finished" -> status = "zakończony";
            case "notstarted" -> status = "zaplanowany";
            case "interrupted" -> status = "przerwany";
            case "inprogress" -> status = "w trakcie";
        }

        int firstPlayerTeamId = node.path("homeTeam").path("id").asInt();
        int secondPlayerTeamId = node.path("awayTeam").path("id").asInt();

        List<String> firstPlayerInfo = new ArrayList<>();
        List<String> secondPlayerInfo = new ArrayList<>();

        if (node.path("homeTeam").path("nameCode").asText().contains("/")) { //sprawdzenie czy jest to mecz deblowy
            firstPlayerTeamId = node.path("homeTeam").path("subTeams").path(0).path("id").asInt();
            Player player1 = playerRepository.getPlayerByTeamid(firstPlayerTeamId);
            firstPlayerTeamId = node.path("homeTeam").path("subTeams").path(1).path("id").asInt();
            Player player2 = playerRepository.getPlayerByTeamid(firstPlayerTeamId);

            secondPlayerTeamId = node.path("awayTeam").path("subTeams").path(0).path("id").asInt();
            Player player3 = playerRepository.getPlayerByTeamid(secondPlayerTeamId);
            secondPlayerTeamId = node.path("awayTeam").path("subTeams").path(1).path("id").asInt();
            Player player4 = playerRepository.getPlayerByTeamid(secondPlayerTeamId);

            if (player1 != null && player2 != null) {
                firstPlayerInfo.add(player1.getName() + " / " + player2.getName());
                firstPlayerInfo.add(player1.getCountry() + " / " + player2.getCountry());
            }
            else {
                firstPlayerInfo.add(node.path("homeTeam").path("subTeams").path(0).path("name").asText() + " / " + node.path("homeTeam").path("subTeams").path(1).path("name").asText());
                firstPlayerInfo.add("- / -");
            }
            firstPlayerInfo.add(node.path("homeTeamSeed").asText());

            if (player3 != null && player4 != null) {
                secondPlayerInfo.add(player3.getName() + " / " + player4.getName());
                secondPlayerInfo.add(player3.getCountry() + " / " + player4.getCountry());
            }
            else {
                secondPlayerInfo.add(node.path("awayTeam").path("subTeams").path(0).path("name").asText() + " / " + node.path("homeTeam").path("subTeams").path(1).path("name").asText());
                secondPlayerInfo.add("- / -");
            }
            secondPlayerInfo.add(node.path("awayTeamSeed").asText());
        }
        else { // mecz singlowy
            Player player1 = playerRepository.getPlayerByTeamid(firstPlayerTeamId);
            Player player2 = playerRepository.getPlayerByTeamid(secondPlayerTeamId);
            String seed1 = node.path("homeTeamSeed").asText();
            String seed2 = node.path("awayTeamSeed").asText();

            if (player1 != null) {
                firstPlayerInfo = getPlayerInfo(player1, seed1);
            }
            else {
                firstPlayerInfo.add(node.path("homeTeam").path("name").asText());
                firstPlayerInfo.add("-");
                firstPlayerInfo.add(seed1);
            }

            if (player2 != null) {
                secondPlayerInfo = getPlayerInfo(player2, seed2);
            }
            else {
                secondPlayerInfo.add(node.path("awayTeam").path("name").asText());
                secondPlayerInfo.add("-");
                secondPlayerInfo.add(seed2);
            }

        }

        if (Objects.equals(whichMatch, "next")) {
            return new Match(id, nameOfTournament, rankOfTournament, surface, round, status, firstPlayerInfo, secondPlayerInfo, -1, null, null, null, -1);
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

        if (!status.equals("w trakcie")) {
            return new Match(id, nameOfTournament, rankOfTournament, surface, round, status, firstPlayerInfo, secondPlayerInfo, winner, firstPlayerScore, secondPlayerScore, null, -1);
        }

        List<Integer> gamePoints = new ArrayList<>();
        gamePoints.add(node.path("homeScore").path("point").asInt());
        gamePoints.add(node.path("awayScore").path("point").asInt());

        int numberOfGames = firstPlayerScore.stream().mapToInt(Integer::intValue).sum() + secondPlayerScore.stream().mapToInt(Integer::intValue).sum();
        for (int i = 0; i < firstPlayerScore.size(); i++) {
            if (firstPlayerScore.get(i) == 7 || secondPlayerScore.get(i) == 7) numberOfGames--;
        }
        int servesFirst = node.path("firstToServe").asInt();
        int service = -1;
        if (numberOfGames % 2 == 0 && servesFirst == 1) service = 1;
        if (numberOfGames % 2 == 0 && servesFirst == 2) service = 2;
        if (numberOfGames % 2 == 1 && servesFirst == 1) service = 2;
        if (numberOfGames % 2 == 1 && servesFirst == 2) service = 1;

        return new Match(id, nameOfTournament, rankOfTournament, surface, round, status, firstPlayerInfo, secondPlayerInfo, winner, firstPlayerScore, secondPlayerScore, gamePoints, service);
    }
}
