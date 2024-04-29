package fp.events;

public class StateEvent extends Event {
    private static int nextCode = 0;
    public final int quickCode;
    public final Object stateId;
    public final Object stateNew;
    static {
        Event.setFmt(EventType.State, StateEvent.class);
    }
    public static int getCode() {
        return nextCode++;
    }
    public StateEvent(int quickCode, Object stateId, Object stateNew) {
        super(EventType.State);
        this.quickCode = quickCode;
        this.stateId = stateId;
        this.stateNew = stateNew;
    }
    public String strData() {
        return String.format("(%d) {%s}->{%s}", quickCode, stateId, stateNew);
    }
}
