package com.won.myongjiCamp.model.board;

import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.model.Scrap;
import com.won.myongjiCamp.model.board.report.ReportStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "board_type")
public abstract class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "board",cascade = CascadeType.ALL)
    private List<Scrap> scraps = new ArrayList<>();

    private Integer scrapCount=0; //스크랩 수

    private Integer commentCount=0; //댓글 수

    private Integer reportCount=0 ; //신고 수


    @Column(name = "created_date")
    @CreationTimestamp
    private Timestamp createDate;

    @Column(name = "modified_date")
    @CreationTimestamp
    private Timestamp modifiedDate;

    @Enumerated(EnumType.STRING)
    private ReportStatus reportStatus;

}

