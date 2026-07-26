package dev.samuel.auth_service.service;

import dev.samuel.auth_service.configuration.RabbitMQConfig;
import dev.samuel.auth_service.response.UsuarioEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publicarUsuarioCadastrado(UsuarioEvent evento) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_USUARIO,
                evento
        );
    }

    public void publicarUsuarioAtualizado(UsuarioEvent evento) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_USUARIO,
                evento
        );
    }

}
