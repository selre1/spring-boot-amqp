package com.bhb.messaging;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AmqpItemController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AmqpItemController.class);

    private final AmqpTemplate amqpTemplate;

    public AmqpItemController(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    @PostMapping("/items")
    Mono<ResponseEntity<?>> addNewItemUsingAmqp(@RequestBody Mono<Item> itemMono){
        return itemMono
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(content -> {
                    return Mono
                            .fromCallable(() -> {
                                amqpTemplate.convertAndSend("bhb","ex",content);
                            return ResponseEntity.created(URI.create("/items")).build();
                    });
                });
    }
}
