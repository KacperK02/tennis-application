package com.application.tennisApplication.repository;

import com.application.tennisApplication.model.Follow;
import com.application.tennisApplication.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Integer> {

}
