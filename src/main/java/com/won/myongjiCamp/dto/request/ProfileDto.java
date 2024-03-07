package com.won.myongjiCamp.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ProfileDto {

    private Integer profileIcon;

    @NotEmpty
    private String nickname;
}
