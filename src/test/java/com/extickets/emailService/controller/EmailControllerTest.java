package com.extickets.emailService.controller;

import com.extickets.emailService.model.EmailRequest;
import com.extickets.emailService.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmailController.class)
class EmailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailService emailService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void sendEmail_success() throws Exception {
        // Arrange
        EmailRequest request = new EmailRequest();
        request.setTo("test@example.com");
        request.setSubject("Test Subject");
        request.setBody("This is a test email.");

        // Mock service call (no exception)
        Mockito.doNothing().when(emailService)
                .sendEmail(anyString(), anyString(), anyString());

        // Act & Assert
        mockMvc.perform(post("/api/tickets/sendEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Email sent successfully!"));

        // Verify service was called exactly once
        Mockito.verify(emailService, times(1))
                .sendEmail("test@example.com", "Test Subject", "This is a test email.");
    }

    @Test
    void sendEmail_failure() throws Exception {
        // Arrange
        EmailRequest request = new EmailRequest();
        request.setTo("invalid@example.com");
        request.setSubject("Failing Email");
        request.setBody("This should trigger exception.");

        // Mock service to throw exception
        Mockito.doThrow(new RuntimeException("SMTP connection failed"))
                .when(emailService)
                .sendEmail(anyString(), anyString(), anyString());

        // Act & Assert
        mockMvc.perform(post("/api/tickets/sendEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to send email: SMTP connection failed"));

        // Verify service was called
        Mockito.verify(emailService, times(1))
                .sendEmail("invalid@example.com", "Failing Email", "This should trigger exception.");
    }
}
