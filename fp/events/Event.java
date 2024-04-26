package fp.events;

public class Event {
    public final EventType type;
    @SuppressWarnings("unchecked")
    private static final Class<? extends Event>[] fmts = (Class<? extends Event>[])new Class[EventType.kinds];
    public Event() {
        type = EventType.Invalid;
    }
    public Event(EventType type) {
        this.type = type;
    }
    protected static void setFmt(EventType e, Class<? extends Event> f) {
        fmts[e.superkind] = f;
    }
    public final String toString() {
        String r;
        if (fmts[type.superkind] == null) {
            r = "{NO FORMATTER}";
        } else {
            try {
                r = (String)fmts[type.superkind].getDeclaredMethod("strData").invoke(this);
            } catch (Exception E) {
                E.printStackTrace();
                throw new IllegalArgumentException();
            }
        }
        return String.format("EVENT (%s): %s", type, r);
    }
}
