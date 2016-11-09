package srv;

public class Connect4 {

    private final long id;
    private final String content;

    public Connect4(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
}
