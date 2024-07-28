package org.example.springbootserver.dao;

import org.example.springbootserver.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikesRepository extends JpaRepository<Likes, Integer> {
    List<Likes> findByPostid(int postid);

    Likes findByPostidAndLikenick(int postid, String likenick);
}
