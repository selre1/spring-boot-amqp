package com.bhb.messaging;

import static org.assertj.core.api.Assertions.*;

import reactor.test.StepVerifier;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureWebTestClient
@Testcontainers
@ContextConfiguration
public class RabbitTest {
    @Container
    final static RabbitMQContainer container = new RabbitMQContainer("rabbitmq:3.7.25-management-alpine");

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemRepository itemRepository;

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry){
        registry.add("spring.rabbitmq.hosst", container::getContainerIpAddress);
        registry.add("spring.rabbitmq.port", container::getAmqpPort);
    }


    @Test
    void verifyMessagingThroughAmqp() throws InterruptedException {
        webTestClient.post().uri("/items")
                .bodyValue(new Item("방항배","사용성", 12.32))
                .exchange()
                .expectStatus().isCreated()
                .expectBody();

        Thread.sleep(1500L);

        webTestClient.post().uri("/items")
                .bodyValue(new Item("방빼","사용성", 1552.32))
                .exchange()
                .expectStatus().isCreated()
                .expectBody();

        Thread.sleep(2000L);

        itemRepository.findAll()
                .as(StepVerifier::create)
                .expectNextMatches(item -> {
                    assertThat(item.getName()).isEqualTo("방항배");
                    assertThat(item.getDescription()).isEqualTo("사용성");
                    assertThat(item.getPrice()).isEqualTo(12.32);
                    return true;
                })
                .expectNextMatches(item -> {
                    assertThat(item.getName()).isEqualTo("방빼");
                    assertThat(item.getDescription()).isEqualTo("사용성");
                    assertThat(item.getPrice()).isEqualTo(1552.32);
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void testing() throws InterruptedException {
        webTestClient.post().uri("/items")
                .bodyValue(new Item("mj","방빼","사용성", 1552.32))
                .exchange()
                .expectStatus().isCreated()
                .expectBody();
        Thread.sleep(2000L);

        itemRepository.findById("mj")

                .as(StepVerifier::create)
                .expectNextMatches(item -> {
                    assertThat(item.getName()).isEqualTo("방빼");
                    System.out.println(">>>>>>>>>>>>>>"+item.getDescription());
                    return true;
                })
                .verifyComplete();
    }
}
