package com.allibilli.recator.consumer;

import com.allibilli.recator.model.EventModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.kafka.receiver.ReceiverOffset;
import reactor.kafka.receiver.ReceiverRecord;

import javax.annotation.PostConstruct;
import java.util.UUID;

/**
 * Created by Gopi K Kancharla
 * 7/24/18 2:32 PM
 */
@Component
@Slf4j
public class MessageProcessor {


    @Autowired
    ObjectMapper objectMapper;

    @PostConstruct
    public void reactorConsumer() {
        log.info("Message processor loaded");
    }


    public void processMessage(ReceiverRecord<Integer, String> record) {

        try {
            ReceiverOffset offset = record.receiverOffset();
            EventModel eventRecord = objectMapper.readValue(record.value(), EventModel.class);
            UUID stockId = eventRecord.getStockId();
            String stockName = eventRecord.getStockName();
            float stockPrice = eventRecord.getPrice();


            log.info("Stock details: Id: {}, Name:{}, Price: {}", stockId, stockName, stockPrice);


        } catch (Exception e) {
            log.error("Error Processing HeartBeat:", e);
        }
    }
}
