package it.alpitour.kafka.utils;

import it.alpitour.kafka.producer.AlpitourKafkaProducer;

import java.io.File;
import java.util.Properties;

public class SSLUtils {
    private static String getCertificatePath() {
        return new File(AlpitourKafkaProducer.class.getClassLoader().getResource("kafka.client.truststore.jks").getFile()).getAbsolutePath();
    }

    public static Properties sslProperties(String certificatePath) {
        Properties props = new Properties();
        props.put("security.protocol",  "SASL_SSL");
        props.put("ssl.truststore.location", certificatePath != null ? certificatePath : SSLUtils.getCertificatePath());
        props.put("ssl.truststore.password",  "test1234");
        props.put("ssl.endpoint.identification.algorithm",  "");
        props.put("sasl.mechanism",  "PLAIN");
        props.put("sasl.jaas.config",  "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"client1\" password=\"client1-password\";");
        return props;
    }

}
