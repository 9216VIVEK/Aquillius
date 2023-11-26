package com.aquillius.portal.dto;

import lombok.Data;

public record UserUpdateRequest(
        String firstName,
        String lastName) {
}
