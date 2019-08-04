package com.allibilli.recator.functional;

import org.apache.kafka.clients.producer.RecordMetadata;
import reactor.kafka.sender.SenderResult;

/**
 * Created by Gopi K Kancharla
 * 7/23/18 2:22 PM
 */
@FunctionalInterface
public interface SuccessHandlerFunction {

    void successHandlerCallback(String event, RecordMetadata metadata, SenderResult<String> result);
}
