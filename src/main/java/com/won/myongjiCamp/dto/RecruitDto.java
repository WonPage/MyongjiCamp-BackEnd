package com.won.myongjiCamp.dto;


import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.model.board.RecruitStatus;
import com.won.myongjiCamp.model.board.role.RoleAssignment;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
public class RecruitDto {

    @NotEmpty
    @Length(max = 20)
    private String title;

    @NotEmpty
    @Length(max = 500)
    private String content;

//    private Integer scrapCount;

/*    @NotEmpty
    private RecruitStatus status; //모집 중 or 모집 완료*/

    @NotEmpty
    private String preferredLocation; //활동 지역

    @NotEmpty
    private String expectedDuration; //예상 기간

    @NotEmpty
    private List<RoleAssignmentDto> roleAssignments;
}
