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
    @JoinColumn(name = "board_id")
    private RecruitBoard board;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private int requiredNumber; // 모집 하는 인원

    @Column(nullable = false)
    private int appliedNumber; // 모집된 인원
}
