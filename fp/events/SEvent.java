package fp.events;

import java.util.ArrayList;
import java.util.HashMap;

public class SEvent extends Event {
    private static final HashMap<String, Integer> codemap = new HashMap<>();
    private static final ArrayList<String> namemap = new ArrayList<>();
    public static final int SIGEXIT;
    public static final int SIGTICK;
    static {
        Event.setFmt(EventType.Signal, SEvent.class);
        SIGEXIT = registerSignal("SIGEXIT");
        SIGTICK = registerSignal("SIGTICK");
    }
    public final Object sender;
    public final int sigcode;
    public static int registerSignal(String name) {
        int c = namemap.size();
        codemap.put(name, c);
        namemap.add(name);
        return c;
    }
    public SEvent(Object sender, int sigcode) {
        super(EventType.Signal);
        this.sender = sender;
        this.sigcode = sigcode;
    }
    public SEvent(Object sender, String signame) {
        super(EventType.Signal);
        this.sender = sender;
        this.sigcode = codemap.getOrDefault(signame, -1);
    }
    public String strData() {
        return String.format("%s (%d) {%s}", namemap.get(sigcode), sigcode, sender);
    }
}
