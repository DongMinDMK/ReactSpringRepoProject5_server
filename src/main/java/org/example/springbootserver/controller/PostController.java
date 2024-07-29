package org.example.springbootserver.controller;

import jakarta.servlet.ServletContext;
import org.example.springbootserver.entity.Images;
import org.example.springbootserver.entity.Likes;
import org.example.springbootserver.entity.Post;
import org.example.springbootserver.entity.Reply;
import org.example.springbootserver.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping("/getPostList")
    public HashMap<String, Object> getPostList(@RequestParam(value="word", required = false) String word){
        HashMap<String, Object> hm = new HashMap<>();

        hm.put("postList", postService.getPostList(word));

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

    @Autowired
    ServletContext context;
    @PostMapping("/imgup")
    public HashMap<String, Object> imgup(@RequestParam("image") MultipartFile file){
        HashMap<String, Object> result = new HashMap<String, Object>();
        String path = context.getRealPath("/uploads");
        Calendar today = Calendar.getInstance();
        long dt = today.getTimeInMillis();
        String filename = file.getOriginalFilename();
        String fn1 = filename.substring(0, filename.indexOf(".") );
        String fn2 = filename.substring(filename.indexOf(".") );
        String uploadPath = path + "/" + fn1 + dt + fn2;
        try {
            file.transferTo( new File(uploadPath) );
            result.put("savefilename", fn1 + dt + fn2);
        } catch (IllegalStateException | IOException e) {e.printStackTrace();}
        return result;

    }

    @PostMapping("/insertPost")
    public HashMap<String, Object> insertPost(@RequestBody Post post){
        HashMap<String, Object> hm = new HashMap<>();

        int postid = postService.insertPost(post);

        hm.put("id", postid);

        return hm;
    }

    @PostMapping("/insertImages")
    public HashMap<String, Object> insertImages(@RequestBody Images images){
        HashMap<String, Object> hm = new HashMap<>();

        postService.insertImages(images);

        hm.put("message", "OK");

        return hm;
    }

    @GetMapping("/getMyPost")
    public HashMap<String, Object> getMyPost(@RequestParam("writer") String writer){
        HashMap<String, Object> hm = new HashMap<>();

       List<Post> list = postService.getPostListByNickname(writer);
       List<String> imgList = new ArrayList<>();

       for(Post p : list){
           List<Images> imgl = postService.getImgListByPostid(p.getId());
           String imgName = imgl.get(0).getSavefilename();
           imgList.add(imgName);
       }

       hm.put("postList", list);
       hm.put("imgList", imgList);

        return hm;
    }
}
