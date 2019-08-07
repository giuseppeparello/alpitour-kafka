package it.alpitour.kafka.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class JSONSerializer implements Serializer<Object>{
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void configure(Map<String, ?> map, boolean b) {
    }

    @Override
    public byte[] serialize(String s, Object o) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            if(String.class.isAssignableFrom(o.getClass()))
                return ((String)o).getBytes();
            return objectMapper.writeValueAsString(o).getBytes();
        } catch (Exception e) {
            logger.error("Serialization error", e);
            throw new RuntimeException(e);
        }
    }

    @Override public void close() {
        logger.debug("close serializer");
    }
}
