package com.won.myongjiCamp.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String email;

    @Column(nullable = false)
    private String password;

    private Integer profileIcon;

    @Column(nullable = false, unique = true)
    private String nickname;

    @CreationTimestamp
    private Timestamp createdDate;

    @OneToMany(mappedBy = "member")
    private List<Resume> resumes = new ArrayList<>();
}
