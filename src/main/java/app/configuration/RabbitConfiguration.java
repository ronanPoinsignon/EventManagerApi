package app.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {

    // Configuration Rabbit

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory cf) {
        RabbitAdmin admin = new RabbitAdmin(cf);
        admin.setAutoStartup(true);
        return admin;
    }

    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, JacksonJsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    // Création des Queues

    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange("notifications.exchange");
    }

    @Bean
    public Queue discordQueue() {
        System.out.println("Creating discord queue");
        return new Queue("discord.notifications", true);
    }

    @Bean
    public Queue webQueue() {
        System.out.println("Creating web queue");
        return new Queue("web.notifications", true);
    }

    @Bean
    public Binding discordBinding(Queue discordQueue, DirectExchange notificationExchange) {
        return BindingBuilder.bind(discordQueue).to(notificationExchange).with("discord");
    }

    @Bean
    public Binding webBinding(Queue webQueue, DirectExchange notificationExchange) {
        return BindingBuilder.bind(webQueue).to(notificationExchange).with("web");
    }

}