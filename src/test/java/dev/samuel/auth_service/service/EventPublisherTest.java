package dev.samuel.auth_service.service;

import dev.samuel.auth_service.configuration.RabbitMQConfig;
import dev.samuel.auth_service.response.UsuarioEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EventPublisherTest {

    @InjectMocks
    EventPublisher eventPublisher;

    @Mock
    RabbitTemplate rabbitTemplate;

    @Test
    void publicarUsuarioCadastrado() {
        UsuarioEvent event = UsuarioEvent.builder()
                .nome("Nome Teste")
                .email("Email Teste")
                .build();

       eventPublisher.publicarUsuarioCadastrado(event);

       Mockito.verify(rabbitTemplate).convertAndSend(
               RabbitMQConfig.EXCHANGE,
               RabbitMQConfig.ROUTING_KEY_USUARIO,
               event);

    }

    @Test
    void publicarUsuarioAtualizado() {
        UsuarioEvent event = UsuarioEvent.builder()
                .nome("Nome Teste")
                .email("Email Teste")
                .build();

        eventPublisher.publicarUsuarioAtualizado(event);

        Mockito.verify(rabbitTemplate).convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_USUARIO,
                event);

    }
}