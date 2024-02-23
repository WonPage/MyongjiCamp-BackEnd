package com.won.myongjiCamp.model.board;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@DiscriminatorValue("Complete")
public class CompleteBoard extends Board {

    private String imageUrl;
}
