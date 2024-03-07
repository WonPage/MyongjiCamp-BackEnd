package com.won.myongjiCamp.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class PasswordDto {
    @NotEmpty
    @Length(min = 8, max = 20)
    @Pattern(regexp = "^(?=.*[!@#$%^&*])(?=\\S+$).*$")
    private String password;
}
