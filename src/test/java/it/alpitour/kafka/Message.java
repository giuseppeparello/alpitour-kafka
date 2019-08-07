package it.alpitour.kafka;

class Message{
    public Message() {}

    private String header;
    private String body;

    public String getHeader() { return header; }
    public Message setHeader(String header) { this.header = header; return this;}
    public String getBody() { return body; }
    public Message setBody(String body) { this.body = body; return this;}

    @Override
    public String toString() {
        return "Message{" +
                "header='" + header + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
