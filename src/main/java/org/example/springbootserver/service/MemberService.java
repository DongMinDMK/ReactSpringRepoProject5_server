package org.example.springbootserver.service;

import org.example.springbootserver.dao.FollowRepository;
import org.example.springbootserver.dao.MemberRepository;
import org.example.springbootserver.entity.Follow;
import org.example.springbootserver.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MemberService {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    FollowRepository followRepository;

    public Member getMember(String email) {
        Optional<Member> member = memberRepository.findByEmail(email);
        if(!member.isPresent()){
            return null;
        }else{
            return member.get();
        }
    }

    public Member getNickname(String nickname) {
        Optional<Member> list = memberRepository.findByNickname(nickname);

        if(!list.isPresent()){
            return null;
        }else{
            return list.get();
        }
    }

    public Object getFollowings(String nickname) {
        List<Follow> followingList = followRepository.findByFfrom(nickname);
        return followingList;
    }

    public Object getFollowers(String nickname) {
        List<Follow> followersList = followRepository.findByFto(nickname);
        return  followersList;
    }

    public void insertMember(Member member) {
        Member member1 = new Member();

        member1.setEmail(member.getEmail());
        member1.setPwd(member.getPwd());
        member1.setNickname(member.getNickname());
        member1.setPhone(member.getPhone());
        member1.setProfilemsg(member.getProfilemsg());
        member1.setProfileimg(member.getProfileimg());

        memberRepository.save(member1);
    }

    public Member getMemberBySnsid(String id) {
        Optional<Member> member = memberRepository.findBySnsid(id);

        if(!member.isPresent()){
            return null;
        }else{
            return member.get();
        }
    }

    public void onFollow(String ffrom, String fto) {
        // ffrom 과 fto 로 전달된 값으로 레코드가 있는지 검사
        Optional<Follow> record = followRepository.findByFfromAndFto(ffrom, fto);

        if(!record.isPresent()){
            // 레코드가 없는 것이므로 추가
            Follow follow = new Follow();
            follow.setFfrom(ffrom);
            follow.setFto(fto);
            followRepository.save(follow);
        }
    }
}
