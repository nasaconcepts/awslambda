package org.example.otp;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;

import com.amazonaws.services.simpleemail.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.util.function.Consumer;

public class SendMailHandler implements RequestHandler<DynamodbEvent, Void> {
    // SesClient sesClient = SesClient.builder().build();
    AmazonSimpleEmailService sesClient = AmazonSimpleEmailServiceClientBuilder.standard().build();

    @Override
    public Void handleRequest(DynamodbEvent dynamodbEvent, Context context) {
        System.out.println("I got into the handler");
        try {
            System.out.println("value: =>" + new ObjectMapper().writeValueAsString(dynamodbEvent));
        } catch (JsonProcessingException e) {
            System.out.println("Error::: " + e.getMessage());
            throw new RuntimeException(e);
        }
        try {
            System.out.println("Output: =>"+new ObjectMapper().writeValueAsString(dynamodbEvent.getRecords().get(0).getDynamodb()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
//
//        String emailId = dynamodbEvent.getRecords().get(0).getDynamodb().getKeys().get("emailId").getS();
//        String otp = dynamodbEvent.getRecords().get(0).getDynamodb().getKeys().get("otp").getS();
        LambdaLogger logger = context.getLogger();


        for (DynamodbEvent.DynamodbStreamRecord record : dynamodbEvent.getRecords()) {

            String email = record.getDynamodb().getNewImage().get("emailId").getS();
            String otpValue = record.getDynamodb().getNewImage().get("otp").getS();
            logger.log("Feedback from email-id: " + otpValue + " otp => " + otpValue);
            if (record.getEventName() == "INSERT") {
                doSendEmail(email, otpValue, logger);
            }
        }


        return null;
    }

    public void doSendEmail(String emailId, String otp, LambdaLogger logger) {
        try {
            SendEmailRequest request = new SendEmailRequest()
                    .withSource("chinasa.obichere@gmail.com")
                    .withDestination(new Destination().withToAddresses(emailId))
                    .withMessage(new Message()
                            .withSubject(new Content("AWS Notification"))
                            .withBody(new Body().withText(new Content("Your Otp is " + otp))));


            sesClient.sendEmail(request);
            logger.log("Email has been sent successfully");
        } catch (Exception ex) {
            logger.log("Error of sending email: " + ex.getMessage());
        }

    }
}