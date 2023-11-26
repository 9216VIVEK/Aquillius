package com.aquillius.portal.service;


import com.aquillius.portal.entity.User;

public interface EmailService {

    void sendEmail(String toEmail, String subject, String content);

    void sendEmailWithAttachment(String toEmail, String subject, String content,
                                        String attachmentName, byte[] attachmentData);

    void sendVerificationCode(User user);
}
