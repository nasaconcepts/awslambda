package org.example.otp;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.otp.model.OtpItem;
import org.example.otp.model.OtpRequest;
import org.example.otp.model.OtpResponse;

import java.time.Instant;
import java.util.Random;

public class GenerateOtp implements RequestHandler<OtpRequest, OtpResponse> {
    static AmazonDynamoDB dynamoClient = AmazonDynamoDBClientBuilder.standard().build();
    static DynamoDB dynamoDB = new DynamoDB(dynamoClient);
    final static String tableName = "otp_table";


    public static String generateOtp() {
        Random random = new Random();
        int min = 100000;
        int max = 999999;
        String otp = String.valueOf(random.nextInt(max - min + 1) + min);

        return otp;
    }

    public static void saveOtpDynamoDb(OtpItem otpItem) {
        Table table = dynamoDB.getTable(tableName);
        System.out.println(" 1 =>"+otpItem.emailId()+" 2 -> "+otpItem.expTime()+" 3-> "+otpItem.otp());

        Item item = new Item().withPrimaryKey("emailId", otpItem.emailId())
                .withString("otp", otpItem.otp())
                .withNumber("expTime", otpItem.expTime());
        table.putItem(item);


    }

    public static void main(String[] args) {
        System.out.println("OTP is : " + generateOtp());
        System.out.println("OTP is : " + generateOtp());
        System.out.println("OTP is : " + generateOtp());
        System.out.println("OTP is : " + generateOtp());
        System.out.println("OTP is : " + generateOtp());
        Instant epochTime = Instant.now();
        System.out.println("Epoch time " + epochTime.getEpochSecond());
        System.out.println("Epoch time " + (epochTime.getEpochSecond() + 100));
        testDynamodb();


    }

    @Override
    public OtpResponse handleRequest(OtpRequest otpRequest, Context context) {
        LambdaLogger logger = context.getLogger();
        OtpResponse response = new OtpResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.log("Email Id: "+otpRequest.getEmailId());
        final long expireInterval = 120;
        Long expireTime = Instant.now().getEpochSecond()+expireInterval;
        String otp = generateOtp();
        OtpItem item = new OtpItem(otpRequest.getEmailId(),otp,expireTime);
        try {
            saveOtpDynamoDb(item);
            response.setCode("200");
            response.setOtp(otp);
            response.setMessage("OTP generated successfully");
            return response;

        }catch (Exception ex){
            logger.log("Error occurred while saving to dynamodb "+ex.getMessage());
            logger.log("Stack Trace =>  "+ex);
            System.out.println("======================");

            response.setCode("400");
            response.setOtp(otp);
            response.setMessage("OTP failed");
            return response;
        }

    }

    public static void testDynamodb(){

        OtpResponse response = new OtpResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        OtpRequest request = new OtpRequest();

        final long expireInterval = 120;
        Long expireTime = Instant.now().getEpochSecond()+expireInterval;
        String otp = generateOtp();
        OtpItem item = new OtpItem("chiasa@gmail.com",otp,expireTime);
        try {
            saveOtpDynamoDb(item);
            response.setCode("200");
            response.setOtp(otp);
            response.setMessage("OTP generated successfully");


        }catch (Exception ex){

            System.out.println("======================");

            response.setCode("400");
            response.setOtp(otp);
            response.setMessage("OTP failed");
           ex.printStackTrace();
        }
    }
}
