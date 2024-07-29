package org.example.springbootserver.dao;

import org.example.springbootserver.entity.Images;
import org.example.springbootserver.entity.Post;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Integer> {
    @Query("select p from Post p where p.id IN (select ph.postid from Posthash ph where ph.hashid=:hashid)")
    List<Post> getPostListByTag(@Param("hashid") int hashid);

    List<Post> findByWriterOrderByIdDesc(String writer);
}
