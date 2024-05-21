package fp.entities;

import java.awt.event.KeyEvent;

import fp.*;
import fp.drawing.*;
import fp.events.*;

public class Player extends Kinematic implements Entity, EventListener, Drawable {
    private static final double STOP_TIME = 2.5;
    private static final int FCOOLDOWN = Main.toFrames(100);
    private static final int BCOOLDOWN = Main.toFrames(1000);
    private static final int RCOOLDOWN = Main.toFrames(500);
    private static final double SSCALE = (double)Main.toFrames(100);
    // private static final double SSCALE = 1;
    private static final double PBSPEED = 0.3;
    private static final double PSPEED = PBSPEED*Game.FRAMEDELAY;
    private int health;
    private int FCOOL = Player.FCOOLDOWN;
    private int BCOOL = Player.BCOOLDOWN;
    private int RCOOL = Player.RCOOLDOWN;
    private int MSHOT = 15;
    private int shots = 15;
    private int cooldown = 0; // cooldown for attack
    private int bnkc = 0;
    private int relc = 0;
    private boolean[] inputbuf = new boolean[8], held = new boolean[8];
    private double rotspeed = 6;
    private Vec2 mov = new Vec2(0,Player.PSPEED).div(Player.SSCALE);
    private Vec2 BLINKDIST = new Vec2(0, 0.25);
    private Vec2 nose;
    private void findNose() {
        Shape r = shape.getFirst();
        for (Vec2 v : r.points) {
            if (v.y > 0 && v.equals(new Vec2(0, v.y))) {
                nose = v;
                break;
            }
        }
    }
    private static void generate(Player p, Transform t) {
        BuildResult br = Main.assetBuilder.execute((String)Main.assetBuilder.selectMeta("<player>::names", 0), t);
        p.shape = br.shape;
        p.collider = br.collider;
        p.findNose();
        // System.out.println(p.collider);
    }
    public Player() {
        super(new KinParams(1, 1, 1.0/Player.STOP_TIME));
        // ShapeBuilder sb = new ShapeBuilder();
        // sb.push(new FPoint(0, 0));
        // sb.push(new FPoint(0.01, 0));
        // sb.push(new FPoint(0.01, 0.01));
        // sb.push(new FPoint(0, 0.01));
        // shape = sb.toShape();
        Player.generate(this, new Transform());
        // shape = Shape.Poly(new Vec2[]{new Vec2(0.0125,0), new Vec2(0.05,0.025), new Vec2(0.0875,0), new Vec2(0.05,0.0875)});
        // shape = Shape.Poly(new Vec2[]{new Vec2(0.0125,0), new Vec2(0.05,0.025), new Vec2(0.05,0.0875)});
        // shape = Shape.Rect(0.1, 0.1);
        // shape.fill(new Color(0, 255, 0));
        // shape.fill(StdDraw.GRAY);
        // shape.stroke(StdDraw.BLACK);
        // shape.strokewidth(0.01);
        shape.transform.setTranslation(new Vec2(0.5, 0.65));
        findNose();
        // System.out.println(shape);
        DrawManager.add(this);
        health = 100;
        Observer.register(this);
    }
    public void reload() {
        Player.generate(this, shape.transform);
        findNose();
    }
    public void trigger(Event e) {
        if (e.type.signal()) {
            SEvent se = (SEvent)e;
            if (se.sigcode == SEvent.SIGTICK) {
                this.update(Game.delta());
            } else if (se.sigcode == Main.SIGRELD) {
                reload();
            }
        } else if (e.type.key()) {
            KEvent ke = (KEvent)e;
            if (ke.type == EventType.KeyPress) return;
            int which = -1;
            switch (ke.code) {
                case KeyEvent.VK_LEFT:
                    which = 5;
                    break;
                case KeyEvent.VK_RIGHT:
                    which = 6;
                    break;
                case KeyEvent.VK_UP:
                    which = 2;
                    break;
                case KeyEvent.VK_DOWN:
                    which = 3;
                    break;
                case KeyEvent.VK_SPACE:
                    which = 4;
                    break;
                case KeyEvent.VK_SHIFT:
                    which = 7;
                    break;
                default:
                    break;
            }
            if (Main.DEBUGMDODE) {
                if (ke.code == KeyEvent.VK_U) {
                    rotspeed += 0.1;
                }
                if (ke.code == KeyEvent.VK_Y) {
                    rotspeed -= 0.1;
                }
            }
            if (which == -1) return;
            if (ke.type == EventType.KeyDown) {
                inputbuf[which] = true;
                held[which] = true;
            } else {
                inputbuf[which] = false;
                held[which] = false;
            }
        }
    }
    public void draw() {
        shape.draw();
        // Shape sh = shape.getFirst();
        // Transform t = sh.transform;
        // Vec2 f = t.getTranslation();
        // Vec2[] tl = t.apply(sh.points);
        // StdDraw.setPenColor(StdDraw.WHITE);
        // StdDraw.textRight(1, 0.25, String.format("Vx=%f, Vy=%f", super.getVelocity().x, super.getVelocity().y));
        // StdDraw.textLeft(0, 0.25, String.format("X=%f, Y=%f", f.x, f.y));
        // StdDraw.textLeft(0, 0.35, String.format("<X=%f, <Y=%f", Vec2.minX(tl), Vec2.minY(tl)));
        // StdDraw.textLeft(0, 0.45, String.format(">X=%f, >Y=%f", Vec2.maxX(tl), Vec2.maxY(tl)));
    }
    private void fire() {
        // new Thread(){public void run(){new Laser();}}.start();
        if (shots > 0) {
            shots --;
            new Laser();
        }
    }
    private void blink() {
        shape.transform.localTranslate(BLINKDIST);
    }
    public void update(double dt) {
        super.update(dt);
        if (relc > 0) relc --;
        else {
            relc = RCOOL;
            shots = Math.min(shots + 1, MSHOT);
        }
        if (bnkc > 0) bnkc --;
        else {
            if (inputbuf[3]) {
                inputbuf[3] = held[3];
                bnkc = BCOOL;
                blink();
            }
        }
        if (cooldown > 0) cooldown --;
        else {
            if (inputbuf[4]) {
                inputbuf[4] = held[4];
                cooldown = FCOOL;
                fire();
            }
        }
        Transform t = this.shape.transform;
        if (inputbuf[7]) {
            Vec2 ov = super.getVelocity().mul(-1);
            super.accelLinear(new Vec2(Math.signum(ov.x)*Math.sqrt(Math.abs(ov.x)*2), Math.signum(ov.y)*Math.sqrt(Math.abs(ov.y)*2)), dt);
            // super.accelLinear(new Vec2(ov.x/2, ov.y/2), dt);
            inputbuf[7]=held[7];
        } else if (inputbuf[2]) {
            super.accelLinear(mov.rotDeg(t.getRotation()), dt);
            inputbuf[2]=held[2];}
        // if (inputbuf[3]) {
        //     super.accelLinear(down, dt);
        //     inputbuf[3]=held[3];}
        if (inputbuf[5]) {
            t.rotate(-rotspeed);
            inputbuf[5]=held[5];}
        if (inputbuf[6]) {
            t.rotate(rotspeed);
            inputbuf[6]=held[6];}
        // if (inputbuf[0]) {t.localTranslate(left);inputbuf[0]=held[0];}
        // if (inputbuf[1]) {t.localTranslate(right);inputbuf[1]=held[1];}
        // if (inputbuf[2]) {t.localTranslate(up);inputbuf[2]=held[2];}
        // if (inputbuf[3]) {t.localTranslate(down);inputbuf[3]=held[3];}
        // if (inputbuf[5]) {t.rotate(-rotspeed);inputbuf[5]=held[5];}
        // if (inputbuf[6]) {t.rotate(rotspeed);inputbuf[6]=held[6];}
    }
    public Vec2 nose() {
        return nose;
    }
    public void damage(int amount) {
        health -= amount;
        if (health <= 0) {
            destroy();
        }
    }
    public int getHealth() {
        return health;
    }
    public void destroy() {
        Observer.signal(Main.OVER);
    }
    public Shape getShape() {
        return shape;
    }
    public Transform transform() {
        return shape.transform;
    }
}
