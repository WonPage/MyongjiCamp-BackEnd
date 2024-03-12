package com.won.myongjiCamp.model.board;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@DiscriminatorValue("Complete")
public class CompleteBoard extends Board {

//    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Image> images = new ArrayList<>();
    private String imageUrl;
}
