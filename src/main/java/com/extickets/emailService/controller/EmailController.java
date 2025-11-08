package com.extickets.emailService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.extickets.emailService.model.EmailRequest;
import com.extickets.emailService.service.EmailService;

@RestController
@RequestMapping("/api/tickets")
public class EmailController {

	@Autowired
	private EmailService emailService;
	
	@PostMapping("/sendEmail")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest request) {
		try {
            emailService.sendEmail(request.getTo(), request.getSubject(), request.getBody());
            return ResponseEntity.ok("Email sent successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send email: " + e.getMessage());
        }
    }
}
