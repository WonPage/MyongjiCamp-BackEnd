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

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "requiredNumber", nullable = false)
    private int requiredNumber;

    @Column(name = "appliedNumber", nullable = false)
    private int appliedNumber;

    @Column(name = "isFull",nullable = false)
    private boolean isFull;

    public void addAppliedNumber() {
        appliedNumber += 1;
    }
}
