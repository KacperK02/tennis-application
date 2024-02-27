package com.application.tennisApplication.service;

import com.application.tennisApplication.model.Follow;
import com.application.tennisApplication.model.Player;
import com.application.tennisApplication.repository.FollowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FollowServiceImpl implements FollowService{

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private PlayerService playerService;

    @Override
    public List<Player> getFollowedPlayers(int userId) {
        List <Follow> follows = followRepository.findAll();
        List <Player> followedPlayers = new ArrayList<>();

        for (Follow follow : follows) {
            if (follow.getUser().getUserID() == userId) {
                followedPlayers.add(follow.getPlayer());
            }
        }
        return followedPlayers;
    }

    @Override
    public boolean isPlayerFollowedByUser(int playerId, int userId) {
        List <Follow> follows = followRepository.findAll();
        for (Follow follow : follows){
            if (follow.getUser().getUserID() == userId && follow.getPlayer().getPlayerID() == playerId){
                return true;
            }
        }
        return false;
    }

    @Override
    public void deleteFollowsOfPlayer(Player player) {
        List<Follow> follows = followRepository.findAll();
        Optional<Player> players = playerService.getPlayerById(player.getPlayerID());
        if (players.isPresent()){
            for (Follow follow : follows) {
                if (follow.getPlayer().getPlayerID() == player.getPlayerID()) {
                    followRepository.delete(follow);
                }
            }
        }

    }
}
