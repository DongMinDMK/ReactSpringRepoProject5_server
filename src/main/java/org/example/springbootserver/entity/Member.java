package org.example.springbootserver.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Data
public class Member {
    @Id
    private String nickname;
    private String email;
    private String pwd;
    private String provider;
    private String snsid;
    private String phone;
    private String profileimg;
    private String profilemsg;
}
