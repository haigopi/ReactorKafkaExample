package com.allibilli.recator.publisher;

import com.allibilli.recator.functional.ErrorHandlerFunction;
import com.allibilli.recator.functional.SuccessHandlerFunction;
import com.allibilli.recator.model.EventModel;
import com.allibilli.recator.model.Payload;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;

import javax.annotation.PreDestroy;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by Gopi K Kancharla
 * 7/23/18 2:22 PM
 */
@Component
@Slf4j
public class KafkaProducer {


    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String TOPIC = "demo-topic";

    private final KafkaSender<Integer, String> sender;
    private final SimpleDateFormat dateFormat;

    public void close() {
        sender.close();
    }


    @PreDestroy
    public void destory() {
        close();
    }

    private void handleError(Throwable e, EventModel stockModel, ErrorHandlerFunction function) {
        log.error("Send failed: {}, {}", stockModel.getStockId(), e.getMessage());
        function.errorHandlerCallback(stockModel);
    }

    private void handleOnNext(SenderResult result, EventModel stockModel, ErrorHandlerFunction errorHandlerCallback) {
        log.debug(" SendResult : {}", result);
        Exception e = result.exception();
        if (!Objects.isNull(e)) {
            handleError(e, stockModel, errorHandlerCallback);
        }
    }

    private void handleSubscription(SenderResult<String> result, String key, SuccessHandlerFunction function) {
        RecordMetadata metadata = result.recordMetadata();

        if (metadata == null) return;
        log.debug("Sent Success. EventId: {}, correlationMetadata: {}, topic: {}, partition: {}, offset: {}, timestamp: {}",
                key,
                result.correlationMetadata(),
                metadata.topic(),
                metadata.partition(),
                metadata.offset(),
                dateFormat.format(new Date(metadata.timestamp())));

        function.successHandlerCallback(key, metadata, result);
    }

    public KafkaProducer() {

        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "sample-producer");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        SenderOptions<Integer, String> senderOptions = SenderOptions.create(props);

        sender = KafkaSender.create(senderOptions);
        dateFormat = new SimpleDateFormat("HH:mm:ss:SSS z dd MMM yyyy");
    }


    public void sendMessage(EventModel model) {
        log.info("Sending to Kafka");

        String correlationId = "Transaction_"+ UUID.randomUUID();
        String key = "Key"+UUID.randomUUID();


        Payload payload = new Payload();
        payload.setKey(key);
        payload.setValue(model);


        Flux<Payload> srcFlux = Flux.just(payload);

        try {
            //ew ProducerRecord<>("", p1.key, p1.value);
            ProducerRecord producerRecord = new ProducerRecord("StockPrice", model);
            sender.send(srcFlux.map(p1 -> SenderRecord.create(producerRecord, correlationId)))
                    .log()
                    //.doOnNext(result -> handleOnNext(result, eventRecord, errorHandlerCallback))
                    .doOnError(e -> log.error("->", e))
                    .parallel(3)
                    .subscribe(r -> log.info("-> {}", r));
        } catch (Exception ex) {
            log.error("Error Sending/Constructing Producer/Data: {}, {}");

        }

    }
}

