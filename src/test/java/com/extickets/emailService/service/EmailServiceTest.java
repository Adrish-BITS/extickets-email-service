package com.extickets.emailService.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Mock
    private MimeMessage mimeMessage;

    @Captor
    private ArgumentCaptor<MimeMessage> messageCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void sendEmail_success() throws Exception {
        // Act
        emailService.sendEmail("test@example.com", "Hello", "This is a test email");

        // Assert
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void sendEmail_messagingException_shouldThrowRuntimeException() throws Exception {
        // Arrange: Force MimeMessageHelper to throw MessagingException
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Mime creation failed"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                emailService.sendEmail("fail@example.com", "Bad", "Failure expected")
        );

        assertTrue(exception.getMessage().contains("Failed to send email"));
    }

    @Test
    void sendEmail_helperThrowsException_shouldThrowRuntimeException() throws Exception {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Use spy to partially mock MimeMessageHelper
        try (MockedConstruction<MimeMessageHelper> mocked = Mockito.mockConstruction(
                MimeMessageHelper.class,
                (mock, context) -> {
                    doThrow(new MessagingException("helper error")).when(mock).setTo(anyString());
                }
        )) {
            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    emailService.sendEmail("bad@example.com", "Error", "Body")
            );

            assertEquals("Failed to send email", exception.getMessage());
        }
    }
}
