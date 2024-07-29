package org.example.springbootserver.service;

import org.example.springbootserver.dao.*;
import org.example.springbootserver.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
public class PostService {

    @Autowired
    PostRepository postRepository;

    @Autowired
    ImagesRepository imagesRepository;

    @Autowired
    LikesRepository likesRepository;

    @Autowired
    ReplyRepository replyRepository;

    @Autowired
    HashtagRepository hashtagRepository;

    @Autowired
    PosthashRepository posthashRepository;

    public List<Images> getImages(int postid) {
        List<Images> list = imagesRepository.findByPostid(postid);
        return list;
    }

    public List<Likes> getLikes(int postid) {
        List<Likes> list = likesRepository.findByPostid(postid);
        return list;
    }

    public List<Post>  getPostList(String word) {
        // return postRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        // word로 hashtag 테이블 검색
        // 검색결과에 있는 tagid 들로  posthash 테이블에서 postid 들을 검색
        // postid 들로 post 테이블에서  post 들을 검색
        // select id from hashtag where word=?
        // select postid from posthash where hashid=?
        // select * from post where id=?

        List<Post> list = null;
        if(word == null){
            list = postRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
        }else{
            Optional<Hashtag> words = hashtagRepository.findByWord(word);

            if(words.isPresent()){ //데이터가 존재하면
                list = postRepository.getPostListByTag(words.get().getId());

            }else{ // 없으면
                list = postRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
            }
        }

        return list;
    }

    public Likes addLike(Likes likes) {
        Likes likes1 = likesRepository.findByPostidAndLikenick(likes.getPostid(), likes.getLikenick());
        return likes1;
    }

    public void insertLikes(Likes likes) {
        Likes likes1 = new Likes();
        likes1.setPostid(likes.getPostid());
        likes1.setLikenick(likes.getLikenick());
        likesRepository.save(likes1);
    }

    public void deleteLikes(Likes likes) {
        Likes likes1 = likesRepository.findByPostidAndLikenick(likes.getPostid(), likes.getLikenick());
        likesRepository.delete(likes1);
    }

    public List<Reply> getReplys(int postid) {
        List<Reply> list = replyRepository.findByPostidOrderByIdDesc(postid);
        return list;
    }

    public void insertReply(Reply reply) {
        Reply reply1 = new Reply();
        reply1.setPostid(reply.getPostid());
        reply1.setWriter(reply.getWriter());
        reply1.setContent(reply.getContent());
        replyRepository.save(reply1);
    }

    public void deleteReply(int id) {
        Optional<Reply> reply = replyRepository.findById(id);
        if(reply.isPresent()){
            replyRepository.delete(reply.get());
        }
    }

    public int insertPost(Post post) {

        int postid = 0;

        // 일단 Post 테이블에 삽입을 먼저 진행
        Post post1 = postRepository.save(post);
        postid = post1.getId();

        String content = post.getContent();

        // content 에서 해시태그들만 추출
        Matcher m = Pattern.compile("#([0-9a-zA-Z가-힣]*)").matcher(content);
        List<String> tags = new ArrayList<>();
        while(m.find()){
            tags.add(m.group(1));
        }

        // 추출된 해시테그들로 해시테그 작업
        int hashid=0;
        for(String word: tags){
            Optional<Hashtag> rec = hashtagRepository.findByWord(word);
            if(!rec.isPresent()){
                // hashtag 테이블에 데이터 추가 후 hashid 리턴
                Hashtag hashtag = new Hashtag();
                hashtag.setWord(word);
                Hashtag hashtag1 = hashtagRepository.save(hashtag);
                hashid = hashtag1.getId();
            }else{
                // hashid 리턴
                hashid = rec.get().getId();
            }
        }

        // post_hash 테이블에 삽입
        Posthash posthash = new Posthash();
        posthash.setPostid(postid);
        posthash.setHashid(hashid);
        posthashRepository.save(posthash);

        return postid;
    }

    public void insertImages(Images images) {
        imagesRepository.save(images);
    }

    public List<Post> getPostListByNickname(String writer) {
       return postRepository.findByWriterOrderByIdDesc(writer);
    }

    public List<Images> getImgListByPostid(int id) {
        return imagesRepository.findByPostid(id);
    }
}
