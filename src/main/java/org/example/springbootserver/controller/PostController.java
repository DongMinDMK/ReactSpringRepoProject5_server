package org.example.springbootserver.controller;

import org.example.springbootserver.entity.Images;
import org.example.springbootserver.entity.Likes;
import org.example.springbootserver.entity.Reply;
import org.example.springbootserver.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping("/getPostList")
    public HashMap<String, Object> getPostList(){
        HashMap<String, Object> hm = new HashMap<>();

        hm.put("postList", postService.getPostList());

        return hm;
    }

    @GetMapping("/getImages/{postid}")
    public List<Images> getImages(@PathVariable("postid") int postid){
        List<Images> list = postService.getImages(postid);


        return list;
    }

    @GetMapping("/getLikes/{postid}")
    public List<Likes> getLikes(@PathVariable("postid") int postid){
        List<Likes> list = postService.getLikes(postid);
        return list;
    }

    @PostMapping("/addLike")
    public HashMap<String, Object> addLike(@RequestBody Likes likes){
        HashMap<String, Object> hm = new HashMap<>();

        Likes likes1 = postService.addLike(likes);

        if(likes1 == null){
            // 데이터 삽입
            postService.insertLikes(likes);
        }else{
            // 데이터 삭제
            postService.deleteLikes(likes);

        }


        return hm;
    }

    @GetMapping("/getReplys/{postid}")
    public HashMap<String, Object> getReplys(@PathVariable("postid") int postid){
        HashMap<String, Object> hm = new HashMap<>();

        hm.put("replyList", postService.getReplys(postid));

        return hm;
    }

    @PostMapping("/insertReply")
    public HashMap<String, Object> insertReply(@RequestBody Reply reply){
        HashMap<String, Object> hm = new HashMap<>();

        postService.insertReply(reply);

        hm.put("message", "OK");

        return hm;
    }

    @DeleteMapping("/deleteReply/{id}")
    public HashMap<String, Object> deleteReply(@PathVariable("id") int id){
        HashMap<String, Object> hm = new HashMap<>();

        postService.deleteReply(id);

        hm.put("message", "OK");

        return hm;
    }
}
