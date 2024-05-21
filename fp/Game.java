package fp;

import java.awt.event.KeyEvent;
import java.time.Instant;
import java.util.HashSet;
import java.util.LinkedList;

import fp.drawing.DrawManager;
import fp.entities.*;
import fp.events.*;

public class Game implements EventListener {
    public static final int FRAMERATE = 60;
    public static final int FRAMEDELAY = 1000/FRAMERATE;
    public static final int VISINTERVAL = FRAMERATE/30;
    static {new Game();}
    private static final int BDIFFLEN = 100;
    public static int diffMod = 0;
    private static int diffLength = 4000;
    public static Player player;
    private static LinkedList<Asteroid> asteroids = new LinkedList<>();
    private static LinkedList<Laser> lasers = new LinkedList<>();
    private static int ticker = 1;
    private static int afreq = 60;
    public static boolean gameover = false;
    private static boolean running = true;
    public static boolean freeze = false, paused = false;
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
                if (gameover) {
                    if (ke.code == KeyEvent.VK_Q) {
                        running = false;
                    } else if (ke.code == KeyEvent.VK_A) {
                        again();
                    }
                    return;
                }
                if (Main.DEBUGMDODE) {
                    if (ke.code == KeyEvent.VK_P) {
                        running = false;
                    } else if (ke.code == KeyEvent.VK_X) {
                        freeze = !freeze;
                    } else if (ke.code == KeyEvent.VK_MINUS) {
                        Observer.signal(new StateEvent(Main.COMMANDS, "ASTEROIDS", "CLEAR"));
                    } else if (ke.code == KeyEvent.VK_L) {
                        gameover = true;
                        Observer.signal(Main.OVER);
                    }
                }
                if (ke.code == KeyEvent.VK_ESCAPE) {
                    paused = !paused;
                    freeze = paused;
                }
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
                DrawManager.clear();
            }
        }
    }
    public static void addLaser(Laser l) {
        lasers.add(l);
    }
    private static void physTick() {
        HashSet<Entity> sched = new HashSet<>();
        boolean go = false;
        for (Asteroid a : asteroids) {
            boolean x = true;
            for (Laser l : lasers) {
                if (sched.contains(l)) continue;
                if (l.collides(a)) {
                    x = false;
                    sched.add(l);
                    sched.add(a);
                    break;
                }
            }
            if (x) {
                if (a.collides(player)) {
                    go = true;
                }
            }
        }
        for (Entity e : sched) {
            e.destroy();
        }
        if (go) {
            gameover = true;
            Observer.signal(Main.OVER);
        }
    }
    private static void tick() {
        ticker ++;
        if (!gameover) {
            if (!freeze) {
                if (ticker % Math.max(1, afreq) == 0) {
                    if (asteroids.size() < (5 + (diffMod*5)))
                    asteroids.addLast(new Asteroid());
                    diffLength --;
                    if (diffLength == 0) {
                        diffMod ++;
                        diffLength = BDIFFLEN * diffMod;
                        afreq --;
                    }
                }
                curr = Instant.now();
                Observer.signal(SEvent.TICK);
                physTick();
                last = Instant.now();
            } else {
                curr = Instant.now();
                last = Instant.now();
            }
        }
        if (ticker % VISINTERVAL == 0) Observer.signal(SEvent.DRAW);
    }
    private static void again() {
        freeze = false;
        paused = false;
        diffMod = 0;
        diffLength = 4;
        afreq = 60;
        score = 0;
        asteroids.clear();
        lasers.clear();
        last = Instant.now();
        curr = Instant.now();
        player = new Player();
        ticker = 1;
        gameover = false;
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
                        Thread.sleep(Math.max(0, FRAMEDELAY - (last.toEpochMilli() - curr.toEpochMilli())));
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
