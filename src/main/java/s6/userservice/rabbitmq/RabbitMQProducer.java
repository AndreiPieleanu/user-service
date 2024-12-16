package s6.userservice.rabbitmq;

import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import s6.userservice.dto.UserCreatedEvent;
import org.springframework.stereotype.Service;
import s6.userservice.dto.UserDeletedEvent;
import s6.userservice.dto.UserUpdatedEvent;

@Service
@AllArgsConstructor
public class RabbitMQProducer {
    private final RabbitTemplate rabbitTemplate;

    public void publishUserCreatedEvent(UserCreatedEvent userCreatedEvent) {
        rabbitTemplate.convertAndSend("user-exchange", "user.created", userCreatedEvent);
    }

    public void publishUserUpdatedEvent(UserUpdatedEvent userUpdatedEvent){
        rabbitTemplate.convertAndSend("user-exchange", "user.updated", userUpdatedEvent);
    }

    public void publishUserDeletedEvent(UserDeletedEvent userDeletedEvent){
        rabbitTemplate.convertAndSend("user-exchange", "user.deleted", userDeletedEvent);
    }
}
