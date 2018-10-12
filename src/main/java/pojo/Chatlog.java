package pojo;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Umber Kapur.
 */

public class Chatlog {

    private String message;

    private Long timestamp;

    private Boolean isSent;

    private Long messageId;

    public Chatlog(String message, Long timestamp, Boolean isSent, Long messageId) {
        this.message = message;
        this.timestamp = timestamp;
        this.isSent = isSent;
        this.messageId = messageId;
    }

    public void setIsSent(final Boolean isSent) {
        this.isSent = isSent;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Long getMessageId() {
        return messageId;
    }

    public String getMessage() {
        return message;
    }

}
