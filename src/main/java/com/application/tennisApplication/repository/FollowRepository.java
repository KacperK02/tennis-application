package com.application.tennisApplication.repository;

import com.application.tennisApplication.model.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Integer> {
}
