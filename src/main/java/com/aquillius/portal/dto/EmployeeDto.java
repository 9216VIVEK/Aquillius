package com.aquillius.portal.dto;

import com.aquillius.portal.enums.UserRole;

import lombok.Data;


@Data
public class EmployeeDto {

    long id;
    String profilePic;
    String firstName;
    String lastName;
    UserRole role;
}
