package fp;

import java.awt.event.KeyEvent;

import fp.drawing.*;
import fp.events.*;

public class Main implements EventListener {
    public static boolean DEBUGMDODE = false;
    private static final int FRAMERATE = 60;
    private static final int FRAMEDELAY = 1000/FRAMERATE;
    public static final int GAMEOBJS = StateEvent.getCode();
    public static final int COUNTERS = StateEvent.getCode();
    public static final int COMMANDS = StateEvent.getCode();
    public static final int SIGOVER;
    public static final SEvent OVER;
    public static final int SIGRELD;
    public static final SEvent RELD;
    static {
        SIGOVER = SEvent.registerSignal("SIGOVER");
        OVER = new SEvent(null, SIGOVER);
        SIGRELD = SEvent.registerSignal("SIGRELD");
        RELD = new SEvent(null, SIGRELD);
    }
    public static ShapeBuilder assetBuilder = new ShapeBuilder("fp/res/entry.txt");
    public void trigger(Event e) {
        Main.strigger(e);
    }
    public static void strigger(Event e) {
        if (e.type.key()) {
            KEvent ke = (KEvent)e;
            if (ke.type == EventType.KeyUp) {
                if (ke.code == KeyEvent.VK_R) {
                    assetBuilder = new ShapeBuilder("fp/res/entry.txt");
                    Observer.signal(RELD);
                    Observer.signal(SEvent.DRAW);
                }
                if (ke.code == KeyEvent.VK_O) {
                    System.exit(0);
                }
            }
        }
    }
    public static int toFrames(int time) {
        return Math.max(1, time / FRAMEDELAY);
    }
    public static double toSecs(int frames) {
        return ((double)frames)/((double)FRAMERATE);
    }
    public static void main(String[] args) {
        StdDraw.setTitle("Java FP");
        StdDraw.enableDoubleBuffering();
        Main m = new Main();
        Observer.register(m);
        for (String arg : args) {
            if (arg.equalsIgnoreCase("dbg")) {
                DEBUGMDODE = true;
            }
        }
        Game.start();
    }
}
