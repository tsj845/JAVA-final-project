package fp.drawing;

import java.awt.Color;
import java.util.LinkedList;

import fp.Game;
import fp.Observer;
import fp.StdDraw;
import fp.events.Event;
import fp.events.EventListener;
import fp.events.SEvent;

public class DrawManager implements EventListener {
    public static UI ui = new UI();
    private static LinkedList<Drawable> stack = new LinkedList<>();
    private static DrawManager dm = new DrawManager();
    static {Observer.register(dm);}
    private DrawManager() {}
    private static void draw() {
        StdDraw.clear(StdDraw.BLACK);
        for (Drawable s : stack.toArray(Drawable[]::new)) {
            s.draw();
        }
        if (Game.paused) {
            StdDraw.setPenColor(new Color(0, 0, 0, 200));
            StdDraw.filledRectangle(0.5, 0.5, 0.5, 0.5);
        }
        ui.draw();
        StdDraw.show();
    }
    public static void add(Drawable s) {
        stack.add(s);
    }
    public static boolean remove(Drawable s) {
        return stack.remove(s);
    }
    public static void clear() {
        stack.clear();
    }
    public void trigger(Event e) {
        if (e.type.signal()) {
            SEvent se = (SEvent)e;
            if (se.sigcode == SEvent.SIGDRAW) {
                DrawManager.draw();
            }
        }
    }
}
