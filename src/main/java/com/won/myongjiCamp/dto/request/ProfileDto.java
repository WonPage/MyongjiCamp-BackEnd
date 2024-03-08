package com.won.myongjiCamp.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ProfileDto {

    @NotEmpty
    private String nickname;
}
