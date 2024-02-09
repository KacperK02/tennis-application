package com.application.tennisApplication.service;

import com.application.tennisApplication.model.Player;

import java.io.IOException;
import java.util.List;

public interface PlayerService {
    public void savePlayersFromFile() throws IOException;
    public List<Player> getAllPlayers();

    public List<Player> getAllWTAPlayers();
    public List<Player> getAllATPPlayers();
}
