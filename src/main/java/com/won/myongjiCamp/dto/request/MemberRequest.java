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

    @Data
    public static class EmailDto {
        private String email;
        private String code;
    }

    @Data
    public static class CreateMemberDto {
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

    @Data
    public static class PasswordDto {
        @NotEmpty
        @Length(min = 8, max = 20)
        @Pattern(regexp = "^(?=.*[!@#$%^&*])(?=\\S+$).*$")
        private String password;
    }

    @Data
    public static class ProfileIconRequestDto {
        private Integer profileIcon;
    }

    @Data
    public static class ProfileDto {
        @NotEmpty
        private String nickname;
    }
}
