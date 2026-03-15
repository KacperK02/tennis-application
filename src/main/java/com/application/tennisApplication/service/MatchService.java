package com.application.tennisApplication.service;

import com.application.tennisApplication.model.Match;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.List;

public interface MatchService {
    Match getMatchInfo(JsonNode node);
    int whoServes(JsonNode node, List<Integer> firstPlayerScore, List<Integer> secondPlayerScore, List<Integer> gamePoints);
    List<Integer> getPlayerScore(JsonNode node, String player);
    String getSurface(JsonNode node);
    String getRankOfTournament(JsonNode node);
    String translateRound(String englishRound);
    String getMatchStatus(JsonNode node);
    List<HashMap<String, String>> getMatchStatsCached(int id) throws JsonProcessingException;
    List<HashMap<String, String>> getMatchStatsUncached(int id) throws JsonProcessingException;
    List<HashMap<String, String>> fetchAndParseMatchStats(int id) throws JsonProcessingException;

}
