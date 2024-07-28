package org.example.springbootserver.dao;

import org.example.springbootserver.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Integer> {
    List<Reply> findByPostidOrderByIdDesc(int postid);
}
