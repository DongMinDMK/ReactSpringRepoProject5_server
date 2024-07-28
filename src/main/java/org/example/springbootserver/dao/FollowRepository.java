package org.example.springbootserver.dao;

import org.example.springbootserver.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Integer> {
    List<Follow> findByFfrom(String nickname);

    List<Follow> findByFto(String nickname);
}
