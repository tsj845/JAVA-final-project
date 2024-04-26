package fp.events;

public class KEvent extends Event {
    public final int code;
    public final char key;
    static {
        Event.setFmt(EventType.Key, KEvent.class);
    }
    public KEvent(EventType et, Object data) {
        super(et);
        if (data instanceof Integer) {
            code = (Integer)data;
            key = 0;
        } else if (data instanceof Character) {
            code = 0;
            key = (Character)data;
        } else {
            throw new IllegalArgumentException();
        }
    }
    protected String strData() {
        if (type == EventType.KeyPress) {
            return Character.toString(key);
        } else {
            return Integer.toString(code);
        }
    }
}
