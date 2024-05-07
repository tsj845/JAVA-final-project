package fp;

import java.awt.event.KeyEvent;
import java.util.LinkedList;

import fp.entities.Asteroid;
import fp.entities.Player;
import fp.events.Event;
import fp.events.EventListener;
import fp.events.EventType;
import fp.events.KEvent;
import fp.events.SEvent;
import fp.events.StateEvent;

public class Game implements EventListener {
    private static final int FRAMERATE = 60;
    private static final int FRAMEDELAY = 1000/FRAMERATE;
    private static final Game instance = new Game();
    private static Player player;
    private static LinkedList<Asteroid> asteroids = new LinkedList<>();
    private static int ticker = 1;
    private static int afreq = 60;
    private static boolean running = true;
    private static boolean freeze = false;
    private static int score = 0;
    private Game() {Observer.register(this);}
    private static void strigger(Event e) {
        if (e.type.key()) {
            KEvent ke = (KEvent)e;
            if (ke.type == EventType.KeyUp) {
                if (ke.code == KeyEvent.VK_P) {
                    running = false;
                } else if (ke.code == KeyEvent.VK_X) {
                    freeze = !freeze;
                }
            }
        } else if (e.type.state()) {
            StateEvent se = (StateEvent)e;
            if (se.quickCode == Main.GAMEOBJS) {
                if (se.stateNew instanceof String) {
                    String sn = (String)se.stateNew;
                    if (sn.equals("DESTROYED")) {
                        score ++;
                        asteroids.remove(se.stateId);
                    }
                }
            }
        }
    }
    private static void tick() {
        ticker ++;
        if (!freeze) {
            if (ticker % afreq == 0) {
                asteroids.addLast(new Asteroid());
                System.out.printf("+1 A: %s\n", asteroids.getLast().transform().getTranslation());
            }
            if (ticker % (2*afreq) == 0) {
                // System.out.println("-1 A");
                asteroids.getFirst().destroy();
            }
            Observer.signal(SEvent.TICK);
        }
        Observer.signal(SEvent.DRAW);
    }
    public static void start() {
        player = new Player();
        new Thread(){
            public void run() {
                while (running) {
                    tick();
                    try {
                        Thread.sleep(FRAMEDELAY);
                    } catch (InterruptedException E) {System.exit(1);}
                }
                System.exit(0);
            }
        }.start();
    }
    public void trigger(Event e) {
        Game.strigger(e);
    }
}
