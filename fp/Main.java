package fp;

import fp.events.Event;
import fp.events.EventListener;
import fp.events.EventType;
import fp.events.KEvent;
import fp.events.MEvent;
import fp.events.SEvent;

public class Main implements EventListener {
    public void trigger(Event e) {
        if (e.type.any(EventType.MouseMove, EventType.MouseDrag)) return;
        System.out.println(e.type);
        if (e.type.key()) {
            System.out.println(((KEvent)e));
        } else if (e.type.mouse()) {
            System.out.println((MEvent)e);
        } else if (e.type.signal()) {
            System.out.println((SEvent)e);
        }
    }
    public static void main(String[] args) {
        Main m = new Main();
        // Observer.init();
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
