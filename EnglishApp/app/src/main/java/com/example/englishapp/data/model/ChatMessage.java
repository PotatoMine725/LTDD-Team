
public class ChatMessage {

    public static final int TYPE_USER = 0;
    public static final int TYPE_AI = 1;

    private String message;
    private String sender; // "user" hoặc "ai"
    private long timestamp;
    
    /**
     * Constructor rỗng bắt buộc cho Firebase Firestore
     */
    public ChatMessage() {
    }

    public ChatMessage(String message, String sender) {
        this.message = message;
        this.sender = sender;
        this.timestamp = System.currentTimeMillis();
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getViewType() {
        return sender.equals("user") ? TYPE_USER : TYPE_AI;
    }
}
