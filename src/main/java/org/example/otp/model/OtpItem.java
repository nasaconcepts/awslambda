package org.example.otp.model;

import java.time.LocalDateTime;

public record OtpItem(String emailId, String otp, Long expTime) {
}
