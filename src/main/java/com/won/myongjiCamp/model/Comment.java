package com.won.myongjiCamp.model;

import com.won.myongjiCamp.model.board.Board;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") // 댓글 작성자
    private Member writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id") // 원글
    private Board board;

    @Column(name = "created_date",nullable = false)
    @CreationTimestamp
    private Timestamp createDate;

    @Column(name = "modified_date",nullable = false)
    @UpdateTimestamp
    private Timestamp modifiedDate ;


    @Column(name = "c_dept",nullable = false)
//    @ColumnDefault("0")
    private int cDepth; //commentDept, 0이 기본값, 0이면 부모 댓글, 1이면 대댓글


    //???
    @ManyToOne(fetch = FetchType.LAZY) // 대댓글 - 원 댓글
    @JoinColumn(name = "parent_id")
    private Comment parent;


    @Column(name = "c_parentId") //기본 값 0
    private Long cParentId; //대댓글일 경우 모댓글의 ci값 저장해 누구의 대댓글인지 확인 가능



    @Column(name = "is_delete", nullable = false)
    private boolean isDelete = false; // 삭제된 상태인지 아닌지


    @OneToMany(mappedBy = "parent") // 대댓글 모음집
    private List<Comment> children = new ArrayList<>(); //부모가 삭제되도 자식은 남아있음


    //댓글인지 대댓글인지 구분을 깊이 0 또는 1로 해서 0이면 댓글 1이면 대댓글?

}

