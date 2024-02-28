package com.won.myongjiCamp.model.board.role;

import com.won.myongjiCamp.model.board.RecruitBoard;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class RoleAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board")
    private RecruitBoard board;


    @Enumerated(EnumType.STRING) //엔티티의 필드가 열거형일 때 사용
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "requiredNumber", nullable = false)
    private int requiredNumber; // 모집 하는 인원

    @Column(name = "appliedNumber", nullable = false)
    private int appliedNumber; // 모집된 인원

    @Column(name = "isFull",nullable = false)
    private boolean isFull; // 다 찾는지

}
