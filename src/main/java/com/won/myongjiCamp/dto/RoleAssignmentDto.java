package com.won.myongjiCamp.dto;


import com.won.myongjiCamp.model.board.role.RoleAssignment;
import lombok.Data;

@Data
public class RoleAssignmentDto {

    private String role;

    private Integer requiredNumber;

    private Integer appliedNumber;

/*    public RoleAssignmentDto(RoleAssignment role){
        this.role = role.getRole().name(); //이거 맞냐 이거 뭐냐 진짜
        this.requiredNumber = role.getRequiredNumber();
        this.appliedNumber = role.getAppliedNumber();
    }*/

}
