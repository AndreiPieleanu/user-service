package s6.userservice.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // User-related configuration
    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange("user-exchange");
    }

    @Bean
    public Queue userCreateQueue() {
        return new Queue("user-create-queue", false);
    }

    @Bean
    public Queue userUpdateQueue() {
        return new Queue("user-update-queue", false);
    }

    @Bean
    public Queue userDeleteQueue() {
        return new Queue("user-delete-queue", false);
    }

    @Bean
    public Binding userCreateBinding(Queue userCreateQueue, TopicExchange userExchange) {
        return BindingBuilder.bind(userCreateQueue).to(userExchange).with("user.created");
    }

    @Bean
    public Binding userUpdateBinding(Queue userUpdateQueue, TopicExchange userExchange) {
        return BindingBuilder.bind(userUpdateQueue).to(userExchange).with("user.updated");
    }

    @Bean
    public Binding userDeleteBinding(Queue userDeleteQueue, TopicExchange userExchange) {
        return BindingBuilder.bind(userDeleteQueue).to(userExchange).with("user.deleted");
    }

    // Post-related configuration
    @Bean
    public TopicExchange postExchange() {
        return new TopicExchange("post-exchange");
    }

    @Bean
    public Queue postCreateQueue() {
        return new Queue("post-create-queue", false);
    }

    @Bean
    public Queue postUpdateQueue() {
        return new Queue("post-update-queue", false);
    }

    @Bean
    public Queue postDeleteQueue() {
        return new Queue("post-delete-queue", false);
    }

    @Bean
    public Binding postCreateBinding(Queue postCreateQueue, TopicExchange postExchange) {
        return BindingBuilder.bind(postCreateQueue).to(postExchange).with("post.created");
    }

    @Bean
    public Binding postUpdateBinding(Queue postUpdateQueue, TopicExchange postExchange) {
        return BindingBuilder.bind(postUpdateQueue).to(postExchange).with("post.updated");
    }

    @Bean
    public Binding postDeleteBinding(Queue postDeleteQueue, TopicExchange postExchange) {
        return BindingBuilder.bind(postDeleteQueue).to(postExchange).with("post.deleted");
    }

    // Message converter for both exchanges
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // RabbitTemplate for sending messages
    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
