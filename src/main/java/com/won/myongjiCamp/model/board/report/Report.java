package com.won.myongjiCamp.model.board.report;

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

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "board_id")
    private Long reportedBoardId; // 신고 당한 게시글

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "comment_id")
    private Long reportedCommentId; // 신고 당한 댓글

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_id")
    private Long reporterId; //신고 한 사람

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
