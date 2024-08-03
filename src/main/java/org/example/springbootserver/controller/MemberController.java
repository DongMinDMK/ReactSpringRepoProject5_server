package org.example.springbootserver.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.springbootserver.dto.KakaoProfile;
import org.example.springbootserver.dto.OAuthToken;
import org.example.springbootserver.entity.Member;
import org.example.springbootserver.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;

@RestController
@RequestMapping("/members")
public class MemberController {

    @Autowired
    MemberService memberService;

    @PostMapping("/loginLocal")
    private HashMap<String ,Object> loginLocal(@RequestBody Member member, HttpServletRequest request){
        HashMap<String, Object> hm = new HashMap<>();

        Member member1 = memberService.getMember(member.getEmail());

        if(member1 == null){
            hm.put("message", "아이디가 존재하지 않습니다.");
        }else if(!member1.getPwd().equals(member.getPwd())){
            hm.put("message", "비밀번호가 일치하지 않습니다.");
        }else{
            HttpSession session = request.getSession();
            session.setAttribute("loginUser", member1);
            hm.put("message", "OK");
        }

        return hm;

    }

    @GetMapping("/getLoginUser")
    public HashMap<String, Object> getLoginUser(HttpServletRequest request){
        HashMap<String, Object> hm = new HashMap<>();

        HttpSession session = request.getSession();
        Member member = (Member) session.getAttribute("loginUser");

        hm.put("loginUser", member);
        hm.put("followings", memberService.getFollowings(member.getNickname()));
        hm.put("followers", memberService.getFollowers(member.getNickname()));


        return hm;
    }

    @Autowired
    ServletContext context;

    @PostMapping("/fileupload")
    public HashMap<String, Object> fileupload(@RequestParam("image") MultipartFile file){
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
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @PostMapping("/emailCheck")
    public HashMap<String, Object> emailCheck(@RequestParam("email") String email){
        HashMap<String, Object> hm = new HashMap<>();

        Member member = memberService.getMember(email);

        if(member != null){
            hm.put("message", "NO");
        }else{
            hm.put("message", "OK");
        }
        return hm;
    }

    @PostMapping("/nickNameCheck")
    public HashMap<String, Object> nickNameCheck(@RequestParam("nickname") String nickname){
        HashMap<String, Object> hm = new HashMap<>();

        Member member = memberService.getNickname(nickname);

        if(member != null){
            hm.put("message", "NO");
        }else{
            hm.put("message", "OK");
        }
        return hm;
    }

    @PostMapping("/insertMember")
    public HashMap<String, Object> insertMember(@RequestBody Member member){
        HashMap<String, Object> hm = new HashMap<>();

        memberService.insertMember(member);

        hm.put("message", "OK");
        return hm;
    }

    @Value("${kakao.client_id}")
    private String client_id;
    @Value("${kakao.redirect_uri}")
    private String redirect_uri;

    @RequestMapping("/kakaostart")
    public @ResponseBody String kakaostart(){
        String a = "<script type='text/javascript'>"
                + "location.href='https://kauth.kakao.com/oauth/authorize?"
                + "client_id=" + client_id + "&"
                + "redirect_uri=" + redirect_uri + "&"
                + "response_type=code';" + "</script>";
        return a;
    }
    @RequestMapping("/kakaoLogin")
    public void loginKakao( HttpServletRequest request, HttpServletResponse response) throws IOException {
        String code = request.getParameter("code");
        String endpoint = "https://kauth.kakao.com/oauth/token";
        URL url = new URL(endpoint);
        String bodyData = "grant_type=authorization_code&";
        bodyData += "client_id=" + client_id + "&";
        bodyData += "redirect_uri=" + redirect_uri + "&";
        bodyData += "code=" + code;

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        conn.setDoOutput(true);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
        bw.write(bodyData);
        bw.flush();
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        String input = "";
        StringBuilder sb = new StringBuilder();
        while ((input = br.readLine()) != null) {
            sb.append(input);
        }
        Gson gson = new Gson();
        OAuthToken oAuthToken = gson.fromJson(sb.toString(), OAuthToken.class);
        String endpoint2 = "https://kapi.kakao.com/v2/user/me";
        URL url2 = new URL(endpoint2);

        HttpsURLConnection conn2 = (HttpsURLConnection) url2.openConnection();
        conn2.setRequestProperty("Authorization", "Bearer " + oAuthToken.getAccess_token());
        conn2.setDoOutput(true);
        BufferedReader br2 = new BufferedReader(new InputStreamReader(conn2.getInputStream(), "UTF-8"));
        String input2 = "";
        StringBuilder sb2 = new StringBuilder();
        while ((input2 = br2.readLine()) != null) {
            sb2.append(input2);
            System.out.println(input2);
        }
        Gson gson2 = new Gson();
        KakaoProfile kakaoProfile = gson2.fromJson(sb2.toString(), KakaoProfile.class);
        KakaoProfile.KakaoAccount ac = kakaoProfile.getAccount();
        KakaoProfile.KakaoAccount.Profile pf = ac.getProfile();
        System.out.println("id : " + kakaoProfile.getId());
        // System.out.println("KakaoAccount-Email : " + ac.getEmail());
        System.out.println("Profile-Nickname : " + pf.getNickname());

        Member member = memberService.getMemberBySnsid( kakaoProfile.getId() );
        if( member == null) {
            member = new Member();
            //member.setEmail( pf.getNickname() );
            member.setEmail( pf.getNickname() );  // 전송된 이메일이 없으면 pf.getNickname()
            member.setNickname( pf.getNickname() );
            member.setProvider( "KAKAO" );
            member.setPwd( "KAKAO" );
            member.setSnsid( kakaoProfile.getId() );
            memberService.insertMember(member);
        }
        HttpSession session = request.getSession();
        session.setAttribute("loginUser", member);
        response.sendRedirect("http://localhost:3000/kakaosaveinfo");
    }

    @GetMapping("/logout")
    public HashMap<String, Object> logout(HttpServletRequest request){
        HashMap<String, Object> hm = new HashMap<>();

        HttpSession session = request.getSession();
        session.removeAttribute("loginUser");

        hm.put("message", "OK");

        return hm;
    }

    @PostMapping("/follow")
    public HashMap<String, Object> follow(@RequestParam("ffrom") String ffrom, @RequestParam("fto") String fto){
        HashMap<String, Object> result = new HashMap<>();

        memberService.onFollow(ffrom, fto);
        result.put("message", "OK");

        return result;
    }

    @PostMapping("/updateMember")
    public HashMap<String, Object> updateMember(@RequestBody Member member, HttpServletRequest request){
        HashMap<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        memberService.updateMember(member);
        session.setAttribute("loginUser", member );
        result.put("message", "OK");

        return result;
    }
}
