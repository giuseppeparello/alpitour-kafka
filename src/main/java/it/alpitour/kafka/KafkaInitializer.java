package it.alpitour.kafka;

import java.util.UUID;

public class KafkaInitializer<T> {
    private String hostUrl;
    private String hostPort;
    private String clientId;
    private String topic;
    private Class<T> messageClass;
    private boolean usingSsl;
    private String certificatePath;
    private long blockingTimeout;

    public String getHostUrl() { return hostUrl; }
    public KafkaInitializer setHostUrl(String hostUrl) { this.hostUrl = hostUrl; return this; }

    public String getHostPort() { return hostPort; }
    public KafkaInitializer setHostPort(String hostPort) { this.hostPort = hostPort; return this; }

    /**
     * <code>UUID.randomUUID()</code> as default value
     * @return
     */
    public String getClientId() { return clientId != null ? clientId : UUID.randomUUID().toString(); }
    public KafkaInitializer setClientId(String clientId) { this.clientId = clientId; return this; }

    public String getTopic() { return topic; }
    public KafkaInitializer setTopic(String topic) { this.topic = topic; return this;}

    public Class<T> getMessageClass() { return messageClass; }
    public KafkaInitializer setMessageClass(Class<T> messageClass) { this.messageClass = messageClass; return this;}

    public boolean isUsingSsl() { return usingSsl; }
    public KafkaInitializer setUsingSsl(boolean usingSsl) { this.usingSsl = usingSsl; return this;}

    public String getCertificatePath() { return certificatePath; }
    public KafkaInitializer setCertificatePath(String certificatePath) { this.certificatePath = certificatePath; return this;}

    public long getBlockingTimeout() { return blockingTimeout; }
    public void setBlockingTimeout(long blockingTimeout) { this.blockingTimeout = blockingTimeout; }
}
