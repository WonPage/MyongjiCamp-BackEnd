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

    private Long reportedBoardId;

    private Long reportedCommentId;

    private Long reporterId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportReason reason;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    @Column(name = "created_date")
    @CreationTimestamp
    private Timestamp createdDate;
}
