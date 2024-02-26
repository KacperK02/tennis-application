package com.application.tennisApplication.model;

import java.util.List;

public class Match {
    private String nameOfTournament;
    private String rankOfTournament;
    private String round;
    private String status;
    private List<String> firstPlayerInfo; //name, country, seed
    private List<String> secondPlayerInfo;
    private int winner;
    private List<Integer> firstPlayerScore;
    private List<Integer> secondPlayerScore;

    public Match(String nameOfTournament, String rankOfTournament, String round, String status, List<String> firstPlayerInfo, List<String> secondPlayerInfo, int winner, List<Integer> firstPlayerGamesWon, List<Integer> secondPlayerGamesWon) {
        this.nameOfTournament = nameOfTournament;
        this.rankOfTournament = rankOfTournament;
        this.round = round;
        this.status = status;
        this.firstPlayerInfo = firstPlayerInfo;
        this.secondPlayerInfo = secondPlayerInfo;
        this.winner = winner;
        this.firstPlayerScore = firstPlayerGamesWon;
        this.secondPlayerScore = secondPlayerGamesWon;
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
}
