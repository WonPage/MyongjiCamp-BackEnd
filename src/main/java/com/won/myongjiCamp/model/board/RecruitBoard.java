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
    private RecruitStatus status;

    private String preferredLocation;

    private String expectedDuration;

    @OneToMany(mappedBy = "board",cascade = CascadeType.ALL,orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<RoleAssignment> roles = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Comment> comments = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "mapping_complete_board_id")
    private Board writeCompleteBoard;

    public void connectCompleteBoard(CompleteBoard completeBoard) {
        writeCompleteBoard = completeBoard;
    }
}
