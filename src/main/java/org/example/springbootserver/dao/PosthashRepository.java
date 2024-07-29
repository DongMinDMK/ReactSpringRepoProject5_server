package org.example.springbootserver.dao;

import org.example.springbootserver.entity.Posthash;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PosthashRepository extends JpaRepository<Posthash, Integer> {
}
