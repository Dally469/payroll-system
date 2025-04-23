package com.payroll.payroll_system.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    @Async
    public void sendPasswordResetEmail(String to, String resetLink, String name) throws MessagingException {
        Context context = new Context();
        context.setVariable("resetLink", resetLink);
        context.setVariable("name", name);
        
        String emailContent = templateEngine.process("password-reset-email", context);
        
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject("Password Reset Request");
        helper.setText(emailContent, true);
        
        mailSender.send(message);
    }
    
    @Async
    public void sendPasswordChangedEmail(String to, String name) throws MessagingException {
        Context context = new Context();
        context.setVariable("name", name);
        
        String emailContent = templateEngine.process("password-changed-email", context);
        
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject("Password Changed");
        helper.setText(emailContent, true);
        
        mailSender.send(message);
    }
    
    @Async
    public void sendWelcomeEmail(String to, String name, String username) throws MessagingException {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("username", username);
        
        String emailContent = templateEngine.process("welcome-email", context);
        
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject("Welcome to Payroll System");
        helper.setText(emailContent, true);
        
        mailSender.send(message);
    }
} 