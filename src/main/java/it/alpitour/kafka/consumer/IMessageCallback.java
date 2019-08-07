package it.alpitour.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface IMessageCallback<T> {
    void handleMessage(final ConsumerRecord<String, T> records);
}
