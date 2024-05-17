package fp;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;

import fp.entities.*;
import fp.events.*;

public class Game implements EventListener {
    public static final int FRAMERATE = 60;
    public static final int FRAMEDELAY = 1000/FRAMERATE;
    public static final int VISINTERVAL = FRAMERATE/30;
    static {new Game();}
    private static final int BDIFFLEN = 10;
    public static int diffMod = 0;
    private static int diffLength = 4;
    public static Player player;
    private static LinkedList<Asteroid> asteroids = new LinkedList<>();
    private static LinkedList<Laser> lasers = new LinkedList<>();
    private static int ticker = 1;
    private static int afreq = 60;
    public static boolean gameover = false;
    private static boolean running = true;
    public static boolean freeze = false;
    public static int score = 0;
    private static Instant last;
    private static Instant curr;
    public static double delta() {
        return ((double)(curr.toEpochMilli()-last.toEpochMilli()))/1000.0d;
    }
    private Game() {Observer.register(this);}
    private static void strigger(Event e) {
        if (e.type.key()) {
            KEvent ke = (KEvent)e;
            if (ke.type == EventType.KeyUp) {
                if (ke.code == KeyEvent.VK_P) {
                    running = false;
                } else if (ke.code == KeyEvent.VK_X) {
                    freeze = !freeze;
                } else if (ke.code == KeyEvent.VK_MINUS) {
                    Observer.signal(new StateEvent(Main.COMMANDS, "ASTEROIDS", "CLEAR"));
                }
                // else if (ke.code == KeyEvent.VK_G) {
                    // rem = true;
                    // physTick();
                // }
            }
        } else if (e.type.state()) {
            StateEvent se = (StateEvent)e;
            if (se.quickCode == Main.GAMEOBJS) {
                if (se.stateNew instanceof String) {
                    String sn = (String)se.stateNew;
                    if (sn.equals("DESTROYED")) {
                        if (se.stateId instanceof Asteroid) {
                            score ++;
                            asteroids.remove(se.stateId);
                        } else if (se.stateId instanceof Laser) {
                            lasers.remove(se.stateId);
                        }
                    }
                }
            }
            if (se.quickCode == Main.COUNTERS) {
                if (se.stateId.equals("score")) {
                    score += (Integer)se.stateNew;
                }
            }
        } else if (e.type.signal()) {
            SEvent se = (SEvent)e;
            if (se.sigcode == Main.SIGOVER) {
                gameover = true;
            }
        }
    }
    public static void addLaser(Laser l) {
        lasers.add(l);
    }
    // private static boolean rem = false;
    private static void physTick() {
        HashSet<Entity> sched = new HashSet<>();
        for (Laser l : lasers) {
            for (Asteroid a : asteroids) {
                if (l.collides(a)) {
                    // System.out.println(l);
                    // System.out.println(l.getShape());
                    // System.out.println(a);
                    // System.out.println(a.getShape());
                    sched.add(l);
                    sched.add(a);
                    break;
                }
            }
        }
        for (Entity e : sched) {
            // if (rem) {
            e.destroy();
            // } else {
            //     e.getShape().getFirst().fill(new Color(230, 230, 230));
            // }
        }
        // rem = false;
        // if (!sched.isEmpty()) {
        //     freeze = true;
        // }
    }
    private static void tick() {
        ticker ++;
        if (!freeze) {
            if (ticker % afreq == 0) {
                if (asteroids.size() < (5 + (diffMod*5)))
                asteroids.addLast(new Asteroid());
                // System.out.printf("+1 A: %s\n", asteroids.getLast().transform().getTranslation());
            }
            // if (ticker % (2*afreq) == 0 && !asteroids.isEmpty()) {
            //     // System.out.println("-1 A");
            //     asteroids.getFirst().destroy();
            // }
            curr = Instant.now();
            Observer.signal(SEvent.TICK);
            physTick();
            last = Instant.now();
        } else {
            curr = Instant.now();
            last = Instant.now();
        }
        if (ticker % VISINTERVAL == 0) Observer.signal(SEvent.DRAW);
    }
    public static void start() {
        player = new Player();
        last = Instant.now();
        curr = Instant.now();
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
