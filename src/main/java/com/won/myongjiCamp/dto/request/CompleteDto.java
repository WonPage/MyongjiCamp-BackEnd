package com.won.myongjiCamp.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class CompleteDto {
    private Long id;

    @NotEmpty
    @Length(max = 20)
    private String title;

    @NotEmpty
    @Length(max = 500)
    private String content;

    private String imageUrl;
}