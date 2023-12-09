package org.example.otp.model;

public class OtpResponse {
    public String getCode() {
        return code;
    }

    private String otp;

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCode(String code) {
        this.code = code;
    }

    private String code;
    private String message;
}
