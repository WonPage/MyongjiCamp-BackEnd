package com.won.myongjiCamp.dto;


import com.won.myongjiCamp.model.board.role.Role;
import com.won.myongjiCamp.model.board.role.RoleAssignment;
import lombok.Data;

@Data
public class RoleAssignmentDto {

    private Role role;

    private Integer requiredNumber;

    private Integer appliedNumber;

    public RoleAssignmentDto(Role role,Integer appliedNumber,Integer requiredNumber){
        this.role = role;
        this.requiredNumber = appliedNumber;
        this.appliedNumber = requiredNumber;
    }


}
