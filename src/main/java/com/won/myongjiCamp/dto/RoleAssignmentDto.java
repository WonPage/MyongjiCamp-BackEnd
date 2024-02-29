package com.won.myongjiCamp.dto;


import lombok.Data;

@Data
public class RoleAssignmentDto {

    private String role;

    private Integer requiredNumber;

    private Integer appliedNumber;

}
