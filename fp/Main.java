package fp;

import fp.events.Event;
import fp.events.EventListener;
import fp.events.EventType;
import fp.events.KEvent;
import fp.events.MEvent;
import fp.events.SEvent;
import fp.events.StateEvent;

public class Main implements EventListener {
    private static int quickCode = StateEvent.getCode();
    public void trigger(Event e) {
        if (e.type.any(EventType.MouseMove, EventType.MouseDrag)) return;
        System.out.println(e.type);
        if (e.type.key()) {
            System.out.println(((KEvent)e));
        } else if (e.type.mouse()) {
            MEvent me = (MEvent)e;
            System.out.println(me);
            if (me.type.equals(EventType.MouseUp) && me.pos.x < 0.5 && me.pos.y < 0.5) {
                Observer.signal(new StateEvent(Main.quickCode, this, "Mouse Top Left"));
            }
        } else if (e.type.signal()) {
            System.out.println((SEvent)e);
        } else if (e.type.state()) {
            StateEvent ste = (StateEvent)e;
            if (ste.quickCode == Main.quickCode) {
                System.out.println(ste);
            }
        }
    }
    public static void main(String[] args) {
        Main m = new Main();
        Observer.register(m);
        int repcode = SEvent.registerSignal("MAINREP");
        new Thread(){
            public void run() {
                do {
                    Observer.signal(new SEvent(this, repcode));
                    try {
                        sleep(1000);
                    } catch (InterruptedException E) {}
                } while (false);
            }
        }.start();
    }
}
