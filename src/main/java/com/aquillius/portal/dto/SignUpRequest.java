package com.aquillius.portal.dto;

import lombok.Data;


public record SignUpRequest(

        String firstName,
        String lastName,
        String companyName,
        String email,
        String password,
        String phoneNumber) {
}