package com.vr.SplitEase.service.impl;

import com.vr.SplitEase.service.EmailService;
import jakarta.mail.MessagingException;
import org.hibernate.cfg.Environment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmailServiceImplTest {

    @Autowired
    private EmailService emailService;

    @Test
    void sendEmail() {
        emailService.sendEmail("vasuji378@gmail.com", "Test from splitease", "This is the email sent from split ease");
    }

    @Test
    void testSendEmail() throws MessagingException {
        Map<String, Object> template = new HashMap<>();
        template.put("recipientName", "Vasu Aggarwal");
        template.put("senderName", "Split ease");
        template.put("senderEmail", "split.ease01@gmail.com");
        template.put("groupName", "Dayara Bugyal");
        emailService.sendEmail("vasuji378@gmail.com", "Test 2 from splitease", template);
    }
}