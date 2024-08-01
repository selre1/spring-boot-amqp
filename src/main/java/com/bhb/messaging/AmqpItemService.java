package com.bhb.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class AmqpItemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AmqpItemService.class);

    private final ItemRepository repository;

    public AmqpItemService(ItemRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(
            ackMode = "MANUAL",
            bindings = @QueueBinding(
                    value = @Queue,
                    exchange = @Exchange("bhb"),
                    key = "ex"))
    public Mono<Void> processAmqp(Item item){
        LOGGER.debug("Comsumer ===>" + item);
        return repository.save(item).then();
    }
}
