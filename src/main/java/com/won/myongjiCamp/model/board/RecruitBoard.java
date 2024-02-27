package com.won.myongjiCamp.model.board;

import com.won.myongjiCamp.model.board.role.RoleAssignment;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(nullable = false)
    private String preferredLocation; //활동 지역

    @Column(nullable = false)
    private String expectedDuration; //예상 기간

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<RoleAssignment> roleAssignments; //모집 하는 분야(태그 검색 위해 필요, 몇명 구하는지는 RoleAssignment테이블에 따로 저장됨)
}
