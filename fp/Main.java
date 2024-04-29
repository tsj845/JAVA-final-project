package fp;

import fp.events.Event;
import fp.events.EventListener;
import fp.events.KEvent;
import fp.events.MEvent;

public class Main implements EventListener {
    public void trigger(Event e) {
        System.out.println(e.type);
        if (e.type.key()) {
            System.out.println(((KEvent)e));
        } else if (e.type.mouse()) {
            System.out.println((MEvent)e);
        }
    }
    public static void main(String[] args) {
        Main m = new Main();
        Observer.register(m);
    }
}
