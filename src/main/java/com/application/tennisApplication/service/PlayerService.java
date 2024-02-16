package com.application.tennisApplication.service;

import com.application.tennisApplication.model.Player;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface PlayerService {
    void savePlayersFromFile() throws IOException;
    List<Player> getAllPlayers();

    List<Player> getAllWTAPlayers();
    List<Player> getAllATPPlayers();

    Optional<Player> getPlayerById(int id);

    void updateRanking() throws IOException, InterruptedException;
}
