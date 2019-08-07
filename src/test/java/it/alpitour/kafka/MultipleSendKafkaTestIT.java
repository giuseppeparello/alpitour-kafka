package it.alpitour.kafka;

import it.alpitour.kafka.consumer.AlpitourKafkaConsumer;
import it.alpitour.kafka.consumer.IMessageCallback;
import it.alpitour.kafka.producer.AlpitourKafkaProducer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MultipleSendKafkaTestIT {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private KafkaInitializer kafkaInitializer;
    private static final Integer MESSAGE_NUMBER = 10;
    private static final Integer CLIENT_NUMBER = 2;
    private CountDownLatch lock = new CountDownLatch(MESSAGE_NUMBER * CLIENT_NUMBER);
    private int totalCounter = 0;
    @Before
    public void setUp() throws IOException {
        this.kafkaInitializer = new KafkaInitializer()
                .setTopic(IKafkaConstants.TOPIC_NAME)
                .setHostUrl(IKafkaConstants.KAFKA_BROKERS_HOST)
                .setHostPort(IKafkaConstants.KAFKA_BROKERS_PORT);

    }

    @Test
    public void producesAndConsumesMultipleMessage() throws InterruptedException {
        this.kafkaInitializer.setMessageClass(String.class);
        final String messageString = "prova";

        for (int i = 0; i < MESSAGE_NUMBER; i++) {
            new AlpitourKafkaProducer(this.kafkaInitializer).send(messageString + " " + i);
        }

        for (int i = 0; i < CLIENT_NUMBER; i++) {
            final int k = i + 1;
            this.kafkaInitializer.setClientId(k + "");
            final AlpitourKafkaConsumer alpitourKafkaConsumer = new AlpitourKafkaConsumer(this.kafkaInitializer);
            alpitourKafkaConsumer.readMessages(new IMessageCallback<String>() {
                @Override
                public void handleMessage(final ConsumerRecord<String, String> record) {

                    logger.info("Consumer " + k + ": " + record.value() + ", offset " + record.offset());
                    lock.countDown();
                    totalCounter++;
                    if (lock.getCount() == ((MESSAGE_NUMBER * CLIENT_NUMBER) - (MESSAGE_NUMBER * k))) {
                        alpitourKafkaConsumer.stopRead();
                    }
                }
            });
        }
        logger.info("Message total counter: " + this.totalCounter);
        lock.await(10, TimeUnit.SECONDS);
    }
}