package com.example.outboxexample.config;

import com.example.outboxexample.worker.OutboxMessageConsumer;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingConfig {
    private final String queueName;
    private final String exchangeName;
    private final String routeName;

    public MessagingConfig(@Value("${worker.queue.name}") final String queueName,
            @Value("${worker.exchange.name}") final String exchangeName,
            @Value("${worker.route.name}") final String routeName) {
        this.queueName = queueName;
        this.exchangeName = exchangeName;
        this.routeName = routeName;
    }

    @Bean
    Queue queue() {
        return new Queue(this.queueName, false);
    }

    @Bean
    TopicExchange topic() {
        return new TopicExchange(this.exchangeName);
    }

    @Bean
    public Binding binding(final Queue passwordUpdatedQueue,
            final TopicExchange passwordChangedNotifications) {
        return BindingBuilder.bind(passwordUpdatedQueue).to(passwordChangedNotifications)
                .with(this.routeName);
    }

    @Bean
    MessageListenerAdapter listenerAdapter(final OutboxMessageConsumer consumer) {
        return new MessageListenerAdapter(consumer, "handleMessage");
    }

    @Bean
    SimpleMessageListenerContainer container(final ConnectionFactory factory,
            final MessageListenerAdapter listenerAdapter) {
        final SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.setQueueNames(this.queueName);
        container.setMessageListener(listenerAdapter);
        return container;
    }

}
