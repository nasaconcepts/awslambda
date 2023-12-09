package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class SQSLambda implements RequestHandler<SQSEvent, SQSBatchResponse> {
    @Override
    public SQSBatchResponse handleRequest(SQSEvent sqsEvent, Context context) {
        List<SQSBatchResponse.BatchItemFailure> batchItemFaileds = new ArrayList<>();
        LambdaLogger logger = context.getLogger();

        String messageId = "";
        for (SQSEvent.SQSMessage message : sqsEvent.getRecords()) {
            messageId = message.getMessageId();
            logger.log("The message Id is : " + messageId);

            try {
                if (message.getBody().contains("fail")) {
                    throw new RuntimeException("An error occurred");
                }
                logger.log("Message processed without error for  :[ " + messageId + " ]");
                logger.log("Output  :[ " + new Gson().toJson(message.getBody()) + " ]");
            } catch (Exception ex) {
                batchItemFaileds.add(new SQSBatchResponse.BatchItemFailure(messageId));
            }

        }
        return new SQSBatchResponse(batchItemFaileds);
    }
}
