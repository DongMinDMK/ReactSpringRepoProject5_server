package org.example.springbootserver.dao;

import org.example.springbootserver.entity.Images;
import org.example.springbootserver.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
}
