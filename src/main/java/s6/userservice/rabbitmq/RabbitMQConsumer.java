package s6.userservice.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import s6.userservice.dto.PostCreatedEvent;

@Service
public class RabbitMQConsumer {

    // Listen to user creation events
    @RabbitListener(queues = "post-create-queue")
    public void handlePostCreatedEvent(PostCreatedEvent event) {
        System.out.println("Received Post Created Event: " + event);
        // Handle user creation logic
    }

//    // Listen to user update events
//    @RabbitListener(queues = "post-update-queue")
//    public void handleUserUpdatedEvent(UserUpdatedEvent event) {
//        System.out.println("Received User Updated Event: " + event);
//        // Handle user update logic
//    }
//
//    // Listen to user deletion events
//    @RabbitListener(queues = "post-delete-queue")
//    public void handleUserDeletedEvent(UserDeletedEvent event) {
//        System.out.println("Received User Deleted Event: " + event);
//        // Handle user deletion logic
//    }
}
