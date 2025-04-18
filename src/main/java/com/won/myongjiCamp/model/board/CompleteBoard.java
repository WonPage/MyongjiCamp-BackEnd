package com.won.myongjiCamp.model.board;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("Complete")
public class CompleteBoard extends Board {
    @OneToMany(mappedBy = "board", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Image> images = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "mapping_recruit_board_id")
    private Board writeRecruitBoard;

    public void connectRecruitBoard(RecruitBoard recruitBoard) {
        writeRecruitBoard = recruitBoard;
    }

    public void addImage(Image image) {
        this.images.add(image);
        image.setBoard(this);
    }

    public void removeImage(Image image) {
        this.images.remove(image);
        image.setBoard(null);
    }
}
