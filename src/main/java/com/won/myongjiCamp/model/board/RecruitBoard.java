package com.won.myongjiCamp.model.board;

import com.won.myongjiCamp.model.board.role.RoleAssignment;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@DiscriminatorValue("Recruit")
public class RecruitBoard extends Board {

    @Enumerated(EnumType.STRING)
    private RecruitStatus status;  // 모집중, 모집 완료 상태 구분

    private String preferredLocation; //활동 지역

    private String expectedDuration; //예상 기간

    // OneToMany에서는 many가 주인
    @OneToMany(mappedBy = "board",cascade = CascadeType.ALL,orphanRemoval = true) //RecruitBoard가 연관관계의 주인이 아니다.
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<RoleAssignment> roles = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Comment> comments = new ArrayList<>();

    // 개발완료 글
    @OneToOne
    @JoinColumn(name = "mapping_complete_board_id")
    private Board writeCompleteBoard;

    public void connectCompleteBoard(CompleteBoard completeBoard) {
        writeCompleteBoard = completeBoard;
    }
}
