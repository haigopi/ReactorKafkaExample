package com.allibilli.recator.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.Collections;

/**
 * Created by Gopi K Kancharla
 * 7/23/18 2:22 PM
 */
@Component
@Slf4j
public class StockPriceConsumer {


    String kafkaTopic;

    private ReceiverOptions<Integer, String> receiverOptions;
    private Disposable disposable;

    @Autowired
    MessageProcessor messageProcessor;

    @PostConstruct
    public void reactorConsumer() {
        //Map<String, Object> props = kafkaConfigMapper.getConvertedConfigurations();
        //props.put(ConsumerConfig.GROUP_ID_CONFIG, nodeIdentity.getNodeIdentifier());

        //receiverOptions = ReceiverOptions.create(props);
        //disposable = consumeMessages(kafkaTopic);
        log.info("Heartbeat consumer loaded");
    }


    public Disposable consumeMessages(String topic) {

        ReceiverOptions<Integer, String> options = receiverOptions.subscription(Collections.singleton(topic))
                .addAssignListener(partitions -> log.debug("onPartitionsAssigned {}", partitions))
                .addRevokeListener(partitions -> log.debug("onPartitionsRevoked {}", partitions)).commitInterval(Duration.ZERO);

        Flux<ReceiverRecord<Integer, String>> kafkaFlux = KafkaReceiver.create(options).receive();

        return kafkaFlux
                .map(record -> {
                    messageProcessor.processMessage(record);
                    record.receiverOffset().commit().block();
                    return record;
                })
                .subscribe(record -> log.info("Consumer Subscribed Successfully"));

    }

    @PreDestroy
    public void preDestroy() {
        log.info("Destroying the Consumer");
        disposable.dispose();
        log.info("Consumer Subscribe Flux disposed");
    }

}