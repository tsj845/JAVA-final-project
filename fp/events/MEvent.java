package fp.events;

import fp.Vec2;

public class MEvent extends Event {
    public final Vec2 pos;
    static {
        Event.setFmt(EventType.Mouse, MEvent.class);
    }
    public MEvent(EventType et, Vec2 pos) {
        super(et);
        if (!et.equals(EventType.Mouse)) throw new IllegalArgumentException("MEvent must be of mouse event type");
        this.pos = pos;
    }
    public String strData() {
        return String.format("(%3f, %3f)", pos.x, pos.y);
    }
}
