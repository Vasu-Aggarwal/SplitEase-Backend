package com.vr.SplitEase.config;

import com.vr.SplitEase.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class EmailSchedulerTest {

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailScheduler emailScheduler;

    @Test
    void scheduleEmail() {
        Map<String, Object> template = new HashMap<>();
        template.put("recipientName", "Vasu Aggarwal");
        template.put("senderName", "Split ease");
        template.put("senderEmail", "split.ease01@gmail.com");
        template.put("groupName", "Dayara Bugyal");
        log.info("Scheduling the email");
        emailScheduler.scheduleEmail("vasuji378@gmail.com", "Send via scheduler", template);
    }
}