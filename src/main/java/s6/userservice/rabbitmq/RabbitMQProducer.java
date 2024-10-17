package s6.userservice.rabbitmq;

import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import s6.userservice.dto.UserCreatedEvent;
import org.springframework.stereotype.Service;
import s6.userservice.dto.UserDeletedEvent;

@Service
@AllArgsConstructor
public class RabbitMQProducer {
    private final RabbitTemplate rabbitTemplate;

    public void publishUserCreatedEvent(UserCreatedEvent userCreatedEvent) {
        rabbitTemplate.convertAndSend("user-exchange", "user.created", userCreatedEvent);
    }

    public void publishUserDeletedEvent(UserDeletedEvent userDeletedEvent){
        rabbitTemplate.convertAndSend("user-exchange", "user.deleted", userDeletedEvent);
    }
}
