package com.application.tennisApplication.repository;

import com.application.tennisApplication.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer> {
    List<Player> findByGender(String gender);
    Player getPlayerByTeamid(int teamId);
}
