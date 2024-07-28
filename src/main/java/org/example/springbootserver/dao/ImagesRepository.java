package org.example.springbootserver.dao;

import org.example.springbootserver.entity.Images;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImagesRepository extends JpaRepository<Images, Integer> {
    List<Images> findByPostid(int postid);
}
