package org.example;

import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.internal.InputSubstream;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


public class S3BucketLambda implements RequestHandler<S3Event, Boolean> {
    //Initialization is outside the lambda handler so that it will not be called repeatedly
   private static final AmazonS3 s3Client = AmazonS3Client.builder().build();


    @Override
    public Boolean handleRequest(S3Event s3Event, Context context) {
        String bucketName = "";
        LambdaLogger logger = context.getLogger();
        logger.log("Invoked within lambda method:["+s3Event.getRecords().size()+"]");
        logger.log("Event payload :["+new Gson().toJson(s3Event));
        if (s3Event.getRecords().isEmpty()) {
            logger.log("There was no record found");
            return false;
        }
        for (S3EventNotification.S3EventNotificationRecord record : s3Event.getRecords()) {
            bucketName = record.getS3().getBucket().getName();
            String objectKey = record.getS3().getObject().getKey();
            logger.log("Bucket name : "+bucketName+" and object key => "+objectKey);
            //create s3 client
            //invoking get object
            //processing the input stream

            S3Object s3Object = s3Client.getObject(bucketName, objectKey);
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            //Processing CSV
            try (final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                br.lines().skip(1)
                        .forEach(line -> logger.log(line + "\n"));

            } catch (IOException ex) {
                logger.log("Error while reading file:: "+ ex.getMessage());
                return false;
            }

        }


        return true;
    }
}
