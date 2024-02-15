package com.application.tennisApplication.model;

public class Tournament {
    private String name;
    private String round;
    private String rank;

    public Tournament(String name, String round, String rank) {
        this.name = name;
        this.round = round;
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public String getRound() {
        return round;
    }

    public String getRank() {
        return rank;
    }
}
