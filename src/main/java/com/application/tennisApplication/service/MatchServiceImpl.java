package com.application.tennisApplication.service;

import com.application.tennisApplication.model.Match;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MatchServiceImpl implements MatchService {
    @Autowired
    PlayerService playerService;

    @Override
    public Match getMatchInfo(JsonNode node) {
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
        round = playerService.translateRound(round);

        int winner = node.path("winnerCode").asInt();

        String status = node.path("status").path("type").asText();
        switch (status) {
            case "finished" -> status = "zakończony";
            case "notstarted" -> status = "zaplanowany";
            case "interrupted" -> status = "przerwany";
            case "inprogress" -> status = "w trakcie";
        }

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
