package com.application.tennisApplication.service;

import com.application.tennisApplication.model.Player;
import java.util.List;

public interface FollowService {
    List<Player> getFollowedPlayers(int userId);
    boolean isPlayerFollowedByUser(int playerID, int userID);
    void deleteFollowsOfPlayer(Player player);
}
