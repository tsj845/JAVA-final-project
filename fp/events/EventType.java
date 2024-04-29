package fp.events;

public class EventType {
    public static final EventType Signal,Mouse,Key,State,Invalid,MouseMove,MouseDrag,MouseDown,MouseUp,KeyPress,KeyDown,KeyUp;
    static {
        Signal = new EventType(0, 0, "Signal");
        Mouse = new EventType(1, 0, "Mouse");
        Key = new EventType(2, 0, "Key");
        State = new EventType(3, 0, "State");
        Invalid = new EventType(4, 0, "Invalid");
        MouseMove = new EventType(1, 1, "MouseMove");
        MouseDrag = new EventType(1, 2, "MouseDrag");
        MouseDown = new EventType(1, 3, "MouseDown");
        MouseUp = new EventType(1, 4, "MouseUp");
        KeyPress = new EventType(2, 1, "KeyPress");
        KeyDown = new EventType(2, 2, "KeyDown");
        KeyUp = new EventType(2, 3, "KeyUp");
    }
    protected final int superkind;
    protected final long subcode;
    private final String name;
    public static final int kinds = 5;
    private EventType(int sk, long sc, String name) {superkind = sk;subcode = (sc == 0)?0:(1L<<(sc-1));this.name=name;}
    public String toString() {
        return name;
    }
    public boolean equals(Object o) {
        if (o instanceof EventType) {
            return (superkind == ((EventType)o).superkind && (((subcode & ((EventType)o).subcode) != 0) || (subcode == 0 || ((EventType)o).subcode == 0)));
        }
        return false;
    }
    public boolean signal() {
        return this.superkind == Signal.superkind;
    }
    public boolean mouse() {
        return this.superkind == Mouse.superkind;
    }
    public boolean key() {
        return this.superkind == Key.superkind;
    }
    public boolean state() {
        return this.superkind == State.superkind;
    }
    public boolean invalid() {
        return this.superkind == Invalid.superkind;
    }
    public boolean any(EventType...k) {
        for (EventType o : k) {
            if (this.superkind == o.superkind && this.subcode == o.subcode) return true;
        }
        return false;
    }
    /*
     * returns a new EventType that will match any of the given others in addition to the original
     */
    public EventType also(EventType...o) {
        long codes = subcode;
        StringBuilder nam = new StringBuilder("UNION("+name+"|");
        for (EventType e : o) {
            if (e.superkind != superkind) continue;
            codes |= e.subcode;
            nam.append(e.name + "|");
        }
        nam.append(")");
        return new EventType(superkind, codes, nam.toString());
    }
}
