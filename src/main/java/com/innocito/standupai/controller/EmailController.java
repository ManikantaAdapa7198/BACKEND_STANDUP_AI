package com.innocito.standupai.controller;

import com.innocito.standupai.service.EmailService;
import com.innocito.standupai.service.StandupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

    private static final Logger log = LoggerFactory.getLogger(EmailController.class);
    @Autowired
    EmailService emailService;
    @Autowired
    StandupService standupService;

    @GetMapping("/send-email")
    public String sendEmail(@RequestParam String to, @RequestParam String subject, @RequestParam String text){
        log.info("Send Email......");
        standupService.generateAndSendDailySummary();
       // generateAndSendDailySummary.sendDailySummary(to,subject,text);
        log.info("Email Sent.......");
        return "Email sent successfully";
    }
}
