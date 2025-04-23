package com.payroll.payroll_system.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessagePreparator;

import jakarta.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.Properties;

@Configuration
public class MailConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(MailConfig.class);
    
    @Bean
    public JavaMailSender javaMailSender() {
        // In a production environment, you would configure this with real mail server settings
        // For development purposes, we're creating a mock mail sender that just logs messages
        
        if (System.getProperty("spring.profiles.active", "").contains("prod")) {
            // Use real mail configuration for production
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost("smtp.example.com");
            mailSender.setPort(587);
            mailSender.setUsername("your-username");
            mailSender.setPassword("your-password");
            
            Properties props = mailSender.getJavaMailProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            
            return mailSender;
        } else {
            // Use mock mail sender for development
            return new JavaMailSender() {
                @Override
                public MimeMessage createMimeMessage() {
                    return new JavaMailSenderImpl().createMimeMessage();
                }

                @Override
                public MimeMessage createMimeMessage(InputStream contentStream) throws MailException {
                    return new JavaMailSenderImpl().createMimeMessage(contentStream);
                }

                @Override
                public void send(MimeMessage mimeMessage) throws MailException {
                    logger.info("Mock mail sender: would send MimeMessage");
                }

                @Override
                public void send(MimeMessage... mimeMessages) throws MailException {
                    logger.info("Mock mail sender: would send {} MimeMessages", mimeMessages.length);
                }

                @Override
                public void send(MimeMessagePreparator mimeMessagePreparator) throws MailException {
                    logger.info("Mock mail sender: would send MimeMessagePreparator");
                }

                @Override
                public void send(MimeMessagePreparator... mimeMessagePreparators) throws MailException {
                    logger.info("Mock mail sender: would send {} MimeMessagePreparators", 
                            mimeMessagePreparators.length);
                }

                @Override
                public void send(SimpleMailMessage simpleMessage) throws MailException {
                    logger.info("Mock mail sender: would send SimpleMailMessage to: {}, subject: {}", 
                            String.join(", ", simpleMessage.getTo()), 
                            simpleMessage.getSubject());
                }

                @Override
                public void send(SimpleMailMessage... simpleMessages) throws MailException {
                    logger.info("Mock mail sender: would send {} SimpleMailMessages", simpleMessages.length);
                }
            };
        }
    }
} 