package com.aquillius.portal.dto;




public record PasswordResetRequest (

    String currentPassword,

    String newPassword)

{}
