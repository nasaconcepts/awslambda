package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import lombok.extern.log4j.Log4j2;
import org.example.model.ResponseBase;

public class SNSLambda implements RequestHandler<SNSEvent, ResponseBase> {
    @Override
    public ResponseBase handleRequest(SNSEvent snsEvent, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("I have entered the SNS method in Java");
        snsEvent.getRecords().forEach(msg ->logger.log("SNS Queue message => "+msg));

        return new ResponseBase("Message processed successfuly","200");
    }
}
