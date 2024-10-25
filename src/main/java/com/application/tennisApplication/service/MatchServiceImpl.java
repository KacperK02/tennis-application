package com.application.tennisApplication.service;

import com.application.tennisApplication.model.Match;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MatchServiceImpl implements MatchService {

    @Override
    public int whoServes(JsonNode node, List<Integer> firstPlayerScore, List<Integer> secondPlayerScore, List<Integer> gamePoints) {
        int numberOfGames = firstPlayerScore.stream().mapToInt(Integer::intValue).sum() +
                secondPlayerScore.stream().mapToInt(Integer::intValue).sum();

        // Sprawdź, czy rozgrywany jest tie-break
        boolean isTieBreak = false;
        for (int i = 0; i < firstPlayerScore.size(); i++) {
            if ((firstPlayerScore.get(i) == 6 && secondPlayerScore.get(i) == 6) ||
                    firstPlayerScore.get(i) == 7 || secondPlayerScore.get(i) == 7) {
                isTieBreak = true;
                break;
            }
        }

        for (int i = 0; i < firstPlayerScore.size(); i++) {
            if (firstPlayerScore.get(i) == 7 || secondPlayerScore.get(i) == 7) numberOfGames--;
        }

        int servesFirst = node.path("firstToServe").asInt();
        int service = -1;

        if (isTieBreak) {
            int tieBreakPoints = gamePoints.get(0) + gamePoints.get(1);

            if (tieBreakPoints % 2 == 0) {
                service = servesFirst;  // W tie-breaku serwuje ten, który rozpoczął pierwszy
            } else {
                service = (servesFirst == 1) ? 2 : 1;  // Zmiana serwisu
            }
            // Od trzeciego punktu zmieniamy co dwa punkty
            if (tieBreakPoints > 1 && (tieBreakPoints - 1) % 4 < 2) {
                service = (service == 1) ? 2 : 1; // Zmiana co dwa punkty
            }
        } else {
            // Standardowa kolejność serwowania
            if (numberOfGames % 2 == 0 && servesFirst == 1) service = 1;
            if (numberOfGames % 2 == 0 && servesFirst == 2) service = 2;
            if (numberOfGames % 2 == 1 && servesFirst == 1) service = 2;
            if (numberOfGames % 2 == 1 && servesFirst == 2) service = 1;
        }

        return service;
    }

    @Override
    public List<Integer> getPlayerScore(JsonNode node, String player) {
        List <Integer> playerScore = new ArrayList<>();
        playerScore.add(node.path(player).path("period1").asInt());
        playerScore.add(node.path(player).path("period2").asInt());
        playerScore.add(node.path(player).path("period3").asInt());
        playerScore.add(node.path(player).path("period4").asInt());
        playerScore.add(node.path(player).path("period5").asInt());
        return playerScore;
    }

    @Override
    public String getSurface(JsonNode node) {
        String surface = node.path("tournament").path("uniqueTournament").path("groundType").asText();
        switch(surface) {
            case "Hardcourt outdoor" -> surface = "twarda";
            case "Hardcourt indoor" -> surface = "twarda (hala)";
            case "Red clay" -> surface = "ziemna";
            case "Red clay indoor" -> surface = "ziemna (hala)";
            case "grass" -> surface = "trawiasta";
        }
        return surface;
    }

    @Override
    public String getRankOfTournament(JsonNode node) {
        String rankOfTournament = "-";
        int points = node.path("tournament").path("uniqueTournament").path("tennisPoints").asInt();
        if (points == 2000) {
            rankOfTournament = "Wielki Szlem";
        }
        else {
            if (points == 0) rankOfTournament = "-";
            else rankOfTournament = String.valueOf(points);
        }
        return rankOfTournament;
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
    public String getMatchStatus(JsonNode node) {
        String status = node.path("status").path("type").asText();
        switch (status) {
            case "finished" -> status = "zakończony";
            case "notstarted" -> status = "zaplanowany";
            case "interrupted" -> status = "przerwany";
            case "inprogress" -> status = "w trakcie";
        }
        return status;
    }

    @Override
    public Match getMatchInfo(JsonNode node) {
        int id = node.path("id").asInt();

        String nameOfTournament = node.path("season").path("name").asText();
        if (nameOfTournament.equals("")) nameOfTournament = node.path("tournament").path("name").asText();

        String surface = getSurface(node);

        String rankOfTournament = getRankOfTournament(node);

        String round = node.path("roundInfo").path("name").asText();
        round = translateRound(round);

        int winner = node.path("winnerCode").asInt();

        String status = getMatchStatus(node);

        List<String> firstPlayerInfo = new ArrayList<>();
        List<String> secondPlayerInfo = new ArrayList<>();

        String playerName = node.path("homeTeam").path("name").asText();
        String playerCountry = node.path("homeTeam").path("country").path("name").asText();
        String playerSeed = node.path("homeTeamSeed").asText();
        String teamid = node.path("homeTeam").path("id").asText();
        firstPlayerInfo.add(playerName);
        firstPlayerInfo.add(playerCountry);
        firstPlayerInfo.add(playerSeed);
        firstPlayerInfo.add(teamid);

        playerName = node.path("awayTeam").path("name").asText();
        playerCountry = node.path("awayTeam").path("country").path("name").asText();
        playerSeed = node.path("awayTeamSeed").asText();
        teamid = node.path("awayTeam").path("id").asText();
        secondPlayerInfo.add(playerName);
        secondPlayerInfo.add(playerCountry);
        secondPlayerInfo.add(playerSeed);
        secondPlayerInfo.add(teamid);

        List <Integer> firstPlayerScore = getPlayerScore(node, "homeScore");
        List <Integer> secondPlayerScore = getPlayerScore(node, "awayScore");

        List<Integer> gamePoints = new ArrayList<>();
        gamePoints.add(node.path("homeScore").path("point").asInt());
        gamePoints.add(node.path("awayScore").path("point").asInt());

        // ustal serwującego
        int service = whoServes(node, firstPlayerScore, secondPlayerScore, gamePoints);

        return new Match(id, nameOfTournament, rankOfTournament, surface, round, status, firstPlayerInfo, secondPlayerInfo, winner, firstPlayerScore, secondPlayerScore, gamePoints, service);
    }
}
