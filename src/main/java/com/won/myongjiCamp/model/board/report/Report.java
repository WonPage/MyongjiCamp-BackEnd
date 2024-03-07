package com.won.myongjiCamp.model.board.report;

import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.model.board.Board;
import com.won.myongjiCamp.model.board.Comment;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportTargetType targetType; // 게시글 신고 or 댓글 신고

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board reportedBoard; // 신고 당한 게시글

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment reportedComment; // 신고 당한 댓글

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member reporter; //신고 한 사람

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportReason reason;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportStatus status; // 신고 진행 상태

//    private Integer reportCount ; //신고 수


    @Column(name = "created_date")
    @CreationTimestamp
    private Timestamp createDate;


}
