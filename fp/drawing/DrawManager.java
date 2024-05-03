package fp.drawing;

import java.util.LinkedList;

import fp.Observer;
import fp.StdDraw;
import fp.events.Event;
import fp.events.EventListener;
import fp.events.SEvent;

public class DrawManager implements EventListener {
    private static LinkedList<Drawable> stack = new LinkedList<>();
    private static DrawManager dm = new DrawManager();
    static {Observer.register(dm);}
    private DrawManager() {
    }
    private static void draw() {
        StdDraw.clear();
        for (Drawable s : stack) {
            s.draw();
        }
        StdDraw.show();
    }
    public static void add(Drawable s) {
        stack.add(s);
    }
    public static boolean remove(Drawable s) {
        return stack.remove(s);
    }
    public void trigger(Event e) {
        if (e.type.signal()) {
            SEvent se = (SEvent)e;
            if (se.sigcode == SEvent.SIGTICK) {
                DrawManager.draw();
            }
        }
    }
}
