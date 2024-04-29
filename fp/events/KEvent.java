package fp.events;

public class KEvent extends Event {
    public final int code;
    public final char key;
    static {
        Event.setFmt(EventType.Key, KEvent.class);
    }
    public KEvent(EventType et, char key) {
        super(et);
        if (!et.equals(EventType.Key)) throw new IllegalArgumentException("KEvent must be of key event type");
        code = 0;
        this.key = key;
    }
    public KEvent(EventType et, char key, int code) {
        super(et);
        if (!et.equals(EventType.Key)) throw new IllegalArgumentException("KEvent must be of key event type");
        this.code = code;
        this.key = key;
    }
    protected String strData() {
        if (type == EventType.KeyPress) {
            return Character.toString(key);
        } else {
            return Integer.toString(code) + " (" + Character.toString(key) + ")";
        }
    }
}
