package fp;

import fp.drawing.DrawManager;
import fp.drawing.Shape;
import fp.drawing.ShapeBuilder;
import fp.drawing.Transform;
import fp.entities.Collider;
import fp.entities.Player;
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
        if (e.type.any(EventType.MouseMove, EventType.MouseDrag, EventType.Signal)) return;
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
    public static void tick() {
        new Thread(){
            public void run() {
                do {
                    Observer.signal(new SEvent(null, SEvent.SIGTICK));
                    try {
                        sleep(10);
                    } catch (InterruptedException E) {}
                } while (true);
            }
        }.start();
    }
    public static void coltest() {
        Transform t1 = new Transform(new FPoint(0.5, 0.5), 0.0);
        Transform t2 = new Transform(new FPoint(0.5, 0.5), 0.0);
        Shape s1 = Shape.Rect(0.2, 0.2, t1);
        Shape s2 = Shape.Circle(0.05, t2);
        s1.fill = null;
        s2.fill = null;
        s1.stroke = StdDraw.BLUE;
        s2.stroke = StdDraw.GREEN;
        s1.strokewidth = 0.02;
        s2.strokewidth = 0.02;
        DrawManager.add(s1);
        DrawManager.add(s2);
        Collider c1 = Collider.boxCollider(t1, 0.2, 0.2);
        Collider c2 = Collider.circleCollider(t2, 0.05);
        Observer.signal(new SEvent(null, SEvent.SIGTICK));
        System.out.println(c1.collides(c2));
    }
    public static void main(String[] args) {
        StdDraw.enableDoubleBuffering();
        Main m = new Main();
        Observer.register(m);
        boolean aloop = true;
        for (String arg : args) {
            if (arg.equalsIgnoreCase("noloop")) {
                aloop = false;
                break;
            }
        }
        final boolean loop = aloop;
        // new Player();
        coltest();
        // Observer.signal(new SEvent(null, SEvent.SIGTICK));
        if (loop) tick();
    }
}
