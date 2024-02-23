package com.won.myongjiCamp.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class CreateMemberRequestDto {

    @NotEmpty
    @Length(max = 30)
    private String email;

    @NotEmpty
    @Length(min = 8, max = 20)
    @Pattern(regexp = "^(?=.*[!@#$%^&*])(?=\\S+$).*$")
    private String password;

    private Integer profileIcon;

    @NotEmpty
    private String nickname;
}
