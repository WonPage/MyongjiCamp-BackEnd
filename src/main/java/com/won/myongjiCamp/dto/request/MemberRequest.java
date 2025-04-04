package com.won.myongjiCamp.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

public class MemberRequest {
    @Data
    public static class loginMember {
        @NotEmpty
        @Length(min = 11, max = 30)
        private String username;
        @NotEmpty
        @Length(min = 8, max = 20)
        @Pattern(regexp = "^(?=.*[!@#$%^&*])(?=\\S+$).*$")
        private String password;
    }
}
