package com.vr.SplitEase.config;

import com.vr.SplitEase.dto.request.EmailTask;
import com.vr.SplitEase.service.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class EmailScheduler {

    private final Queue<EmailTask> emailQueue = new LinkedList<>();

    @Autowired
    private EmailService emailService;

    public void scheduleEmail(String to, String subject, Map<String, Object> templateModel) {
        EmailTask emailTask = new EmailTask(to, subject, templateModel);
        emailQueue.add(emailTask); // Add EmailTask to queue
        sendScheduledEmails();
    }

    // Method runs periodically using fixed delay
    @Scheduled(fixedDelay = 5000) //5 sec
    public void sendScheduledEmails() {
        List<EmailTask> batchEmailTasks = new ArrayList<>();
        int batchSize = 10; // Example batch size

        // Collect emails to send in batch
        while (!emailQueue.isEmpty() && batchEmailTasks.size() < batchSize) {
            EmailTask emailTask = emailQueue.poll(); // Retrieve and remove from queue
            if (emailTask != null) {
                batchEmailTasks.add(emailTask);
            }
        }

        // Process batch of emails
        for (EmailTask emailTask : batchEmailTasks) {
            try {
                emailService.sendEmail(emailTask.getTo(), emailTask.getSubject(), emailTask.getTemplateModel());
            } catch (MessagingException e) {
                // Handle exception (e.g., log error, retry logic, etc.)
                e.printStackTrace();
            }
        }
    }

    // Example of a method that runs periodically using cron expression
//    @Scheduled(cron = "0 0 12 * * ?") // Execute every day at 12 PM
//    public void runDailyTask() {
//        // Logic for daily task
//        System.out.println("Running daily task...");
//    }
}
