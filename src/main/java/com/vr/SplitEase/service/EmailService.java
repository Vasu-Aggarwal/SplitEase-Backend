package com.vr.SplitEase.service;

import jakarta.mail.MessagingException;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface EmailService {

    //send email to single user
    void sendEmail(String to, String subject, String message);

    //send email to multiple users
    void sendEmail(List<String> to, String subject, String message);
    void sendEmail(String to, String subject, Map<String, Object> templateModel) throws MessagingException;

}
