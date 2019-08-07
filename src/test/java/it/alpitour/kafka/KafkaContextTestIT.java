package it.alpitour.kafka;

import it.alpitour.kafka.consumer.AlpitourKafkaConsumer;
import it.alpitour.kafka.consumer.IMessageCallback;
import it.alpitour.kafka.producer.AlpitourKafkaProducer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class KafkaContextTestIT {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private KafkaInitializer kafkaInitializer;
    private CountDownLatch lock = new CountDownLatch(1);

    @Before
    public void setUp() throws IOException {
        this.kafkaInitializer = new KafkaInitializer()
                .setTopic(IKafkaConstants.TOPIC_NAME)
                .setHostUrl(IKafkaConstants.KAFKA_BROKERS_HOST)
                .setHostPort(IKafkaConstants.KAFKA_BROKERS_PORT)
                .setClientId(IKafkaConstants.CLIENT_ID)
                .setUsingSsl(Boolean.FALSE);
    }

    @Test
    public void producesAndConsumesString() throws InterruptedException {
        this.kafkaInitializer.setMessageClass(String.class);
        final String messageString = "prova";
        Future<? extends RecordMetadata> send = new AlpitourKafkaProducer(this.kafkaInitializer).send(messageString);
        final AlpitourKafkaConsumer alpitourKafkaConsumer = new AlpitourKafkaConsumer(this.kafkaInitializer);
        alpitourKafkaConsumer.readMessages(new IMessageCallback<String>() {
            @Override
            public void handleMessage(final ConsumerRecord<String, String> record) {
                logger.warn("Record Key " + record.key());
                logger.warn("Record value " + record.value());
                logger.warn("Record partition " + record.partition());
                logger.warn("Record offset " + record.offset());
                logger.warn("Record timestamp " + new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS").format(new Date(record.timestamp())));
                assertFalse(record.value() == null);
                lock.countDown();
                alpitourKafkaConsumer.stopRead();
            }
        });
        lock.await(1, TimeUnit.MINUTES);
    }

    @Test
    public void producesAndConsumesMessage() throws InterruptedException {
        this.kafkaInitializer.setMessageClass(Message.class);
        Future<? extends RecordMetadata> send = new AlpitourKafkaProducer(this.kafkaInitializer).send(
                new Message()
                        .setBody("body")
                        .setHeader("header")
        );
        final AlpitourKafkaConsumer alpitourKafkaConsumer = new AlpitourKafkaConsumer(this.kafkaInitializer);
        alpitourKafkaConsumer.readMessages(new IMessageCallback<Message>() {
            @Override
            public void handleMessage(final ConsumerRecord<String, Message> record) {
                logger.warn("Record Key " + record.key());
                logger.warn("Record value " + record.value());
                logger.warn("Record partition " + record.partition());
                logger.warn("Record offset " + record.offset());
                logger.warn("Record timestamp " + new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS").format(new Date(record.timestamp())));
                lock.countDown();
                alpitourKafkaConsumer.stopRead();

            }
        });
        lock.await(1, TimeUnit.MINUTES);
    }

    @Test
    public void producesAndConsumesInteger() throws InterruptedException {
        this.kafkaInitializer.setMessageClass(Integer.class);
        final Integer integerMessage = 5;
        Future<? extends RecordMetadata> send = new AlpitourKafkaProducer(this.kafkaInitializer).send(integerMessage, new Callback() {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                logger.info("Message sent " + recordMetadata);
                logger.info("Message timestamp " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS").format(new Date(recordMetadata.timestamp())));
            }
        });
        final AlpitourKafkaConsumer alpitourKafkaConsumer = new AlpitourKafkaConsumer(this.kafkaInitializer);
        alpitourKafkaConsumer.readMessages(new IMessageCallback<Integer>() {
            @Override
            public void handleMessage(final ConsumerRecord<String, Integer> record) {
                logger.warn("Record Key " + record.key());
                logger.warn("Record value " + record.value());
                assertEquals(integerMessage, record.value());
                logger.warn("Record partition " + record.partition());
                logger.warn("Record offset " + record.offset());
                logger.warn("Record timestamp " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS").format(new Date(record.timestamp())));
                lock.countDown();
                alpitourKafkaConsumer.stopRead();
            }
        });
        lock.await(1, TimeUnit.MINUTES);
    }

    @Test
    public void consumeEasybookMessage() throws InterruptedException {
        this.kafkaInitializer.setMessageClass(String.class);
        this.kafkaInitializer.setTopic("easybook");
        final AlpitourKafkaConsumer alpitourKafkaConsumer = new AlpitourKafkaConsumer(this.kafkaInitializer);
        alpitourKafkaConsumer.readMessages((IMessageCallback<String>) record  -> {
            logger.warn("Record Key " + record.key());
            logger.warn("Record value " + record.value());
            logger.warn("Record partition " + record.partition());
            logger.warn("Record offset " + record.offset());
            logger.warn("Record timestamp " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS").format(new Date(record.timestamp())));
            lock.countDown();

            //alpitourKafkaConsumer.stopRead();
        });
        lock.await(1, TimeUnit.MINUTES);
    }
}