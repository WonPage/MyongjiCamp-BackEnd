package com.won.myongjiCamp.dto.request;

import com.won.myongjiCamp.dto.response.BoardResponse;
import com.won.myongjiCamp.model.board.RecruitStatus;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

public class BoardRequest {
    @Data
    public static class RecruitDto {
        private Long id;

        @NotEmpty
        @Length(max = 20)
        private String title;

        @NotEmpty
        @Length(max = 500)
        private String content;

        private RecruitStatus status;

        @NotEmpty
        private String preferredLocation; //활동 지역

        @NotEmpty
        private String expectedDuration;

        @NotEmpty
        private List<BoardResponse.RoleAssignmentDto> roleAssignments;
    }

    @Data
    public static class CompleteDto {
        private Long id; //userId

        @NotEmpty
        @Length(max = 20)
        private String title;

        @NotEmpty
        @Length(max = 500)
        private String content;

        List<MultipartFile> images;
    }

    @Data
    public static class BoardSearchDto {
        private List<String> roles;
        private String keyword;
        private int pageNum;
        private String direction;
        private String boardType;
        private String status;
    }

}
