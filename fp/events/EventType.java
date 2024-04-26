package fp.events;

public enum EventType {
    Signal(0),Mouse(1),Key(2),State(3),Invalid(4),MouseMove(1),MouseDown(1),MouseUp(1),KeyPress(2),KeyDown(2),KeyUp(2);
    protected int superkind;
    public static final int kinds = 5;
    private EventType(int sk) {superkind = sk;}
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
            if (this.superkind == o.superkind) return true;
        }
        return false;
    }
}
