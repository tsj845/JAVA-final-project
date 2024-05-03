package fp;

import java.awt.event.KeyEvent;

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
    private static final int FRAMERATE = 60;
    private static final int FRAMEDELAY = 1000/FRAMERATE;
    private static int quickCode = StateEvent.getCode();
    public static Collider[] cols = new Collider[3];
    public static Shape[] shps = new Shape[3];
    private static Vec2 right=new Vec2(0.01,0),left=new Vec2(-0.01,0),up=new Vec2(0,0.01),down=new Vec2(0,-0.01);
    private static boolean coltestdo = false;
    public void trigger(Event e) {
        Main.strigger(e);
    }
    public static void strigger(Event e) {
        if (e.type.any(EventType.MouseMove, EventType.MouseDrag, EventType.Signal)) return;
        // System.out.println(e.type);
        if (e.type.key()) {
            if (!coltestdo) return;
            KEvent ke = (KEvent)e;
            int N = 0;
            if (ke.type == EventType.KeyUp) {
                if (ke.code == KeyEvent.VK_RIGHT) {
                    shps[N].transform.translate(right);
                } else if (ke.code == KeyEvent.VK_LEFT) {
                    shps[N].transform.translate(left);
                } else if (ke.code == KeyEvent.VK_UP) {
                    shps[N].transform.translate(up);
                } else if (ke.code == KeyEvent.VK_DOWN) {
                    shps[N].transform.translate(down);
                } else if (ke.code == KeyEvent.VK_Q) {
                    shps[N].transform.rotate(-5);
                } else if (ke.code == KeyEvent.VK_E) {
                    shps[N].transform.rotate(5);
                } else {
                    return;
                }
                Observer.signal(SEvent.TICK);
                System.out.println("TICK");
                System.out.println(cols[0].collides(cols[1]));
                System.out.println(cols[0].collides(cols[2]));
            }
            // System.out.println(((KEvent)e));
        } else if (e.type.mouse()) {
            // MEvent me = (MEvent)e;
            // System.out.println(me);
            // if (me.type.equals(EventType.MouseUp) && me.pos.x < 0.5 && me.pos.y < 0.5) {
            //     Observer.signal(new StateEvent(Main.quickCode, this, "Mouse Top Left"));
            // }
        } else if (e.type.signal()) {
            System.out.println((SEvent)e);
        } else if (e.type.state()) {
            StateEvent ste = (StateEvent)e;
            if (ste.quickCode == quickCode) {
                System.out.println(ste);
            }
        }
    }
    public static int toFrames(int time) {
        return Math.max(1, time / FRAMEDELAY);
    }
    public static double toSecs(int frames) {
        return ((double)frames)/((double)FRAMERATE);
    }
    public static void tick() {
        new Player();
        new Thread(){
            public void run() {
                do {
                    Observer.signal(new SEvent(null, SEvent.SIGTICK));
                    try {
                        sleep(10);
                    } catch (InterruptedException E) {}
                } while (false);
            }
        }.start();
    }
    public static void coltest() {
        Transform t1 = new Transform(new Vec2(0.5, 0.5), 0.0);
        Transform t2 = new Transform(new Vec2(0.5, 0.5), 0.0);
        Transform t3 = new Transform(new Vec2(0.5, 0.5), 0.0);
        // Shape s1 = Shape.Rect(0.2, 0.2, t1);
        Vec2[] tp2 = Vec2.normalize(new Vec2[]{new Vec2(), new Vec2(0.05, 0.0), new Vec2(0.025, 0.1)});
        Shape s1 = Shape.Poly(tp2, t1);
        Shape s2 = Shape.Circle(0.05, t2);
        Vec2[] tripoints = Vec2.normalize(new Vec2[]{new Vec2(), new Vec2(0.05,0.0), new Vec2(0.025,0.1)});
        Shape s3 = Shape.Poly(tripoints, t3);
        s1.fill(null);
        s2.fill(null);
        s3.fill(null);
        s1.stroke(StdDraw.BLUE);
        s2.stroke(StdDraw.GREEN);
        s3.stroke(StdDraw.RED);
        s1.strokewidth(0.002);
        s2.strokewidth(0.002);
        s3.strokewidth(0.002);
        DrawManager.add(s1);
        DrawManager.add(s2);
        DrawManager.add(s3);
        // Collider c1 = Collider.boxCollider(t1, 0.2, 0.2);
        Collider c1 = Collider.triangleCollider(t1, tp2[0], tp2[1], tp2[2]);
        Collider c2 = Collider.circleCollider(t2, 0.05);
        Collider c3 = Collider.triangleCollider(t3, tripoints[0], tripoints[1], tripoints[2]);
        cols[0] = c1;
        cols[1] = c2;
        cols[2] = c3;
        shps[0] = s1;
        shps[1] = s2;
        shps[2] = s3;
        coltestdo = true;
        Observer.signal(new SEvent(null, SEvent.SIGTICK));
        System.out.println(c1.collides(c2));
        System.out.println(c1.collides(c3));
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
        // Observer.signal(new SEvent(null, SEvent.SIGTICK));
        if (loop) tick();
        else coltest();
    }
}
