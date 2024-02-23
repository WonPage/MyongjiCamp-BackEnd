package com.won.myongjiCamp.model.application;

import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.model.board.Board;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member applicant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String url;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus firstStatus; // 글쓴이의 승인 / 거절

    @Enumerated(EnumType.STRING)
    private ApplicationFinalStatus finalStatus; // 지원자의 최종 승인 / 거절
}

