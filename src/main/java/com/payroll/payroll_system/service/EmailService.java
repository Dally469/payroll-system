package com.payroll.payroll_system.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username:no-reply@example.com}")
    private String fromEmail;
    
    @Autowired
    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Async
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            logger.info("Simple email sent to {}", to);
        } catch (Exception e) {
            logger.error("Failed to send simple email to {}: {}", to, e.getMessage());
        }
    }

    @Async
    public void sendPasswordResetEmail(String to, String resetLink, String name) throws MessagingException {
        try {
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
            logger.info("Password reset email sent to {}", to);
        } catch (Exception e) {
            logger.error("Failed to send password reset email to {}: {}", to, e.getMessage());
        }
    }
    
    @Async
    public void sendPasswordChangedEmail(String to, String name) throws MessagingException {
        try {
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
            logger.info("Password changed email sent to {}", to);
        } catch (Exception e) {
            logger.error("Failed to send password changed email to {}: {}", to, e.getMessage());
        }
    }
    
    @Async
    public void sendWelcomeEmail(String to, String name, String username) throws MessagingException {
        try {
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
            logger.info("Welcome email sent to {}", to);
        } catch (Exception e) {
            logger.error("Failed to send welcome email to {}: {}", to, e.getMessage());
        }
    }
} 