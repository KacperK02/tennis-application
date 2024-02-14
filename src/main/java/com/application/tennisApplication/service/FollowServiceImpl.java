package com.application.tennisApplication.service;

import com.application.tennisApplication.model.Follow;
import com.application.tennisApplication.model.Player;
import com.application.tennisApplication.repository.FollowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class FollowServiceImpl implements FollowService{

    @Autowired
    private FollowRepository followRepository;

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
}
