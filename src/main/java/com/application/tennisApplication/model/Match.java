package com.application.tennisApplication.model;

import java.util.List;

public class Match {
    private int id;
    private String surface;
    private String nameOfTournament;
    private String rankOfTournament;
    private String round;
    private String status;
    private List<String> firstPlayerInfo; //name, country, seed
    private List<String> secondPlayerInfo;
    private int winner;
    private List<Integer> firstPlayerScore;
    private List<Integer> secondPlayerScore;
    private List<Integer> gamePoints;
    private int service;

    public Match(int id, String nameOfTournament, String rankOfTournament, String surface, String round, String status, List<String> firstPlayerInfo, List<String> secondPlayerInfo, int winner, List<Integer> firstPlayerGamesWon, List<Integer> secondPlayerGamesWon, List<Integer> gamePoints, int service) {
        this.id = id;
        this.nameOfTournament = nameOfTournament;
        this.rankOfTournament = rankOfTournament;
        this.surface = surface;
        this.round = round;
        this.status = status;
        this.firstPlayerInfo = firstPlayerInfo;
        this.secondPlayerInfo = secondPlayerInfo;
        this.winner = winner;
        this.firstPlayerScore = firstPlayerGamesWon;
        this.secondPlayerScore = secondPlayerGamesWon;
        this.gamePoints = gamePoints;
        this.service = service;
    }

    public int getService() {
        return service;
    }

    public void setService(int service) {
        this.service = service;
    }

    public List<Integer> getGamePoints() {
        return gamePoints;
    }

    public void setGamePoints(List<Integer> gamePoints) {
        this.gamePoints = gamePoints;
    }

    public String getNameOfTournament() {
        return nameOfTournament;
    }

    public void setNameOfTournament(String nameOfTournament) {
        this.nameOfTournament = nameOfTournament;
    }

    public String getRankOfTournament() {
        return rankOfTournament;
    }

    public void setRankOfTournament(String rankOfTournament) {
        this.rankOfTournament = rankOfTournament;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getFirstPlayerInfo() {
        return firstPlayerInfo;
    }

    public void setFirstPlayerInfo(List<String> firstPlayerInfo) {
        this.firstPlayerInfo = firstPlayerInfo;
    }

    public List<String> getSecondPlayerInfo() {
        return secondPlayerInfo;
    }

    public void setSecondPlayerInfo(List<String> secondPlayerInfo) {
        this.secondPlayerInfo = secondPlayerInfo;
    }

    public int getWinner() {
        return winner;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    public List<Integer> getFirstPlayerScore() {
        return firstPlayerScore;
    }

    public void setFirstPlayerScore(List<Integer> firstPlayerScore) {
        this.firstPlayerScore = firstPlayerScore;
    }

    public List<Integer> getSecondPlayerScore() {
        return secondPlayerScore;
    }

    public void setSecondPlayerScore(List<Integer> secondPlayerScore) {
        this.secondPlayerScore = secondPlayerScore;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSurface() {
        return surface;
    }

    public void setSurface(String surface) {
        this.surface = surface;
    }
}
