package it.alpitour.kafka.consumer;

import it.alpitour.kafka.IKafkaConstants;
import it.alpitour.kafka.KafkaInitializer;
import it.alpitour.kafka.deserializer.JSONDeserializer;
import it.alpitour.kafka.utils.SSLUtils;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;
import java.util.UUID;

public class AlpitourKafkaConsumer {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private Consumer consumer;
    private boolean stopRead = Boolean.FALSE;
    public AlpitourKafkaConsumer(KafkaInitializer kafkaInitializer) {

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaInitializer.getHostUrl() + ":" + kafkaInitializer.getHostPort());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaInitializer.getClientId());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, IKafkaConstants.MAX_POLL_RECORDS);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, IKafkaConstants.OFFSET_RESET_EARLIER);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        if (kafkaInitializer.isUsingSsl()) {
            props.putAll(SSLUtils.sslProperties(kafkaInitializer.getCertificatePath()));
        }
        this.consumer = new KafkaConsumer<String, Object>(props, new StringDeserializer(), this.define(kafkaInitializer.getMessageClass()));
        consumer.subscribe(Collections.singletonList(kafkaInitializer.getTopic()));

    }

    private Deserializer define(Class tClass) {
        if (tClass.isPrimitive() || String.class.isAssignableFrom(tClass)) {
            if (String.class.isAssignableFrom(tClass)) {
                return new StringDeserializer();
            } else if (Integer.class.isAssignableFrom(tClass)) {
                return new IntegerDeserializer();
            }else if (Short.class.isAssignableFrom(tClass)) {
                return new ShortDeserializer();
            }else if (Double.class.isAssignableFrom(tClass)) {
                return new IntegerDeserializer();
            }else if (Float.class.isAssignableFrom(tClass)) {
                return new FloatDeserializer();
            }else if (Long.class.isAssignableFrom(tClass)) {
                return new LongDeserializer();
            }
        }
        return new JSONDeserializer(tClass);
    }

    public <T> void readMessages(IMessageCallback<T> callback) {

        try {
            while (true) {
                if (!this.stopRead) {
                    ConsumerRecords<String, T> consumerRecords = this.consumer.poll(10);
                    Iterator<ConsumerRecord<String, T>> iterator = consumerRecords.iterator();
                    while (iterator.hasNext()) {
                        ConsumerRecord<String, T> record =  iterator.next();
                        callback.handleMessage(record);

                    }
                    // commits the offset of record to broker.
                    consumer.commitAsync();
                }else{
                    break;
                }
            }

        } catch (Exception e) {
            logger.error("Error in readMessage while(true)", e);
        } finally {
            consumer.commitSync();
            consumer.close();
        }
    }

    public void stopRead() {
        this.stopRead = Boolean.TRUE;
    }

}
