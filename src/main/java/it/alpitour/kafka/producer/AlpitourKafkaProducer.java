package it.alpitour.kafka.producer;

import it.alpitour.kafka.KafkaInitializer;
import it.alpitour.kafka.serializer.JSONSerializer;
import it.alpitour.kafka.utils.SSLUtils;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Future;

public class AlpitourKafkaProducer {
    private KafkaProducer kafkaProducer;
    private KafkaInitializer kafkaInitializer;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    public AlpitourKafkaProducer(KafkaInitializer kafkaProducerInitializer) {
        this.kafkaInitializer = kafkaProducerInitializer;
        this.kafkaProducer = this.createProducer(kafkaProducerInitializer);
    }

    private KafkaProducer createProducer(KafkaInitializer kafkaInitializer) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaInitializer.getHostUrl() + ":" + kafkaInitializer.getHostPort());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, kafkaInitializer.getClientId());
        //props.put(ProducerConfig.ACKS_CONFIG, "0");
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, kafkaInitializer.getBlockingTimeout() > 0 ? kafkaInitializer.getBlockingTimeout() : 10000);
/*        ;
        */
        //props.put(ProducerConfig.LINGER_MS_CONFIG, 50000);
        //props.put(ProducerConfig.RETRIES_CONFIG, 1);
        if (kafkaInitializer.isUsingSsl()) {
           props.putAll(SSLUtils.sslProperties(kafkaInitializer.getCertificatePath()));
        }


        return new KafkaProducer(props, new StringSerializer(), this.define(kafkaInitializer.getClass()));
    }



    private Serializer define(Class tClass) {
        if (tClass.isPrimitive() || String.class.isAssignableFrom(tClass)) {
            if (String.class.isAssignableFrom(tClass)) {
                return new StringSerializer();
            } else if (Integer.class.isAssignableFrom(tClass)) {
                return new IntegerSerializer();
            }else if (Short.class.isAssignableFrom(tClass)) {
                return new ShortSerializer();
            }else if (Double.class.isAssignableFrom(tClass)) {
                return new DoubleSerializer();
            }else if (Float.class.isAssignableFrom(tClass)) {
                return new FloatSerializer();
            }else if (Long.class.isAssignableFrom(tClass)) {
                return new LongSerializer();
            }
        }
        return new JSONSerializer();
    }

    public <T> Future<? extends RecordMetadata> send(T message) {
        return this.send(message, null);
    }

    public <T> Future<? extends RecordMetadata> send(T message, Callback callback) {
        final ProducerRecord<String, T> producesRecord =
                new ProducerRecord<String, T>(this.kafkaInitializer.getTopic(), UUID.randomUUID().toString(), message);
        if (callback == null) {
            return this.send(producesRecord);
        } else {
            return this.send(producesRecord, callback);
        }
    }

    public <K, V> Future<? extends RecordMetadata> send(ProducerRecord<K, V> producesRecord) {
        return this.send(producesRecord, new Callback() {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                if (e != null) {
                    logger.error("Exception in send", e);
                    throw new RuntimeException(e);
                } else {
                    logger.debug("Message sent " + recordMetadata);
                }
            }
        });
    }

    public  <K, V> Future<? extends RecordMetadata> send(ProducerRecord<K, V> producesRecord, Callback callback) {
        return this.kafkaProducer.send(producesRecord, callback);
    }

}
