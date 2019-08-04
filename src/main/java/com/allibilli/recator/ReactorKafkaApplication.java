package com.allibilli.recator;

import com.allibilli.recator.publisher.KafkaProducer;
import com.allibilli.recator.model.EventModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Gopi K Kancharla
 * 7/23/18 2:22 PM
 */

@SpringBootApplication
@RestController
@Slf4j
@EnableKafka
@EmbeddedKafka
public class ReactorKafkaApplication {


    @Autowired
    private KafkaProducer producer;


    public static void main(String[] args) {
        SpringApplication.run(ReactorKafkaApplication.class, args);
    }

    @RequestMapping(value = "/sendMessage", method = RequestMethod.POST)
    public ResponseEntity recieveRequest(@RequestBody EventModel incomingMessage) {
        log.info("Message Recieved: {}", incomingMessage);

        //
        //producer.sendMessage(incomingMessage);

        //


        return ResponseEntity.ok("THANKS");
    }

}



