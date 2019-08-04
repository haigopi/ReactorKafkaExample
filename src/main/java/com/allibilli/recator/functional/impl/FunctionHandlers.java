package com.allibilli.recator.functional.impl;

import com.allibilli.recator.functional.ErrorHandlerFunction;
import com.allibilli.recator.functional.SuccessHandlerFunction;
import com.allibilli.recator.model.EventModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import reactor.kafka.sender.SenderResult;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Gopi K Kancharla
 * 7/23/18 2:26 PM
 */
@Slf4j
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FunctionHandlers implements ErrorHandlerFunction, SuccessHandlerFunction {

    @Value("${spring.application.time}")
    private String dateFormatString;

    SimpleDateFormat dateFormat;

    @PostConstruct
    public void failureJournalWriter() {
        dateFormat = new SimpleDateFormat(dateFormatString);
    }

    @Override
    public void errorHandlerCallback(EventModel event) {

        log.error("publish failed event: {}", event);

        // Handle or divert the way you want - or save to DB for future processing.
    }

    @Override
    public void successHandlerCallback(String eventId, RecordMetadata metadata, SenderResult<String> result) {
        log.info("Sent Success. EventId: {}, correlationMetadata: {}, topic: {}, partition: {}, offset: {}, timestamp: {}",
                eventId,
                result.correlationMetadata(),
                metadata.topic(),
                metadata.partition(),
                metadata.offset(),
                dateFormat.format(new Date(metadata.timestamp())));
        // Handle or divert the way you want
    }
}
