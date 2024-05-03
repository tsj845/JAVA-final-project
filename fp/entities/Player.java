package fp.entities;

import java.awt.Color;
import java.awt.event.KeyEvent;

import fp.Vec2;
import fp.Main;
import fp.Observer;
import fp.StdDraw;
import fp.drawing.DrawManager;
import fp.drawing.Drawable;
import fp.drawing.Shape;
import fp.drawing.Transform;
import fp.events.Event;
import fp.events.EventListener;
import fp.events.EventType;
import fp.events.KEvent;
import fp.events.SEvent;

public class Player extends Kinematic implements Entity, EventListener, Drawable {
    private static final int FCOOLDOWN = Main.toFrames(5);
    private static final double SSCALE = (double)Main.toFrames(100);
    // private static final double SSCALE = 1;
    private static final double PSPEED = 0.1;
    private int health;
    private int FCOOL = Player.FCOOLDOWN;
    private int cooldown = 0; // cooldown for attack
    private boolean[] inputbuf = new boolean[7], held = new boolean[7];
    private double rotspeed = 2.5;
    private Vec2 left=new Vec2(-Player.PSPEED,0).div(Player.SSCALE),right=new Vec2(Player.PSPEED,0).div(Player.SSCALE),up=new Vec2(0,Player.PSPEED).div(Player.SSCALE),down=new Vec2(0,-Player.PSPEED).div(Player.SSCALE);
    public Player() {
        super();
        // ShapeBuilder sb = new ShapeBuilder();
        // sb.push(new FPoint(0, 0));
        // sb.push(new FPoint(0.01, 0));
        // sb.push(new FPoint(0.01, 0.01));
        // sb.push(new FPoint(0, 0.01));
        // shape = sb.toShape();
        shape = Shape.Poly(new Vec2[]{new Vec2(0.0125,0), new Vec2(0.05,0.025), new Vec2(0.0875,0), new Vec2(0.05,0.0875)});
        // shape = Shape.Poly(new Vec2[]{new Vec2(0.0125,0), new Vec2(0.05,0.025), new Vec2(0.05,0.0875)});
        // shape = Shape.Rect(0.1, 0.1);
        // shape.fill(new Color(0, 255, 0));
        shape.fill(StdDraw.GRAY);
        shape.stroke(StdDraw.BLACK);
        shape.strokewidth(0.01);
        shape.transform.setTranslation(new Vec2(0.5, 0.5));
        DrawManager.add(this);
        health = 100;
        Observer.register(this);
    }
    public void trigger(Event e) {
        if (e.type.signal() && ((SEvent)e).sigcode == SEvent.SIGTICK) {
            this.update();
            this.draw();
        } else if (e.type.key()) {
            KEvent ke = (KEvent)e;
            if (ke.type == EventType.KeyPress) return;
            int which = -1;
            switch (ke.code) {
                case KeyEvent.VK_LEFT:
                    which = 0;
                    break;
                case KeyEvent.VK_RIGHT:
                    which = 1;
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
                case KeyEvent.VK_Q:
                    which = 5;
                    break;
                case KeyEvent.VK_E:
                    which = 6;
                    break;
                default:
                    break;
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
        Transform t = shape.transform;
        Vec2 f = t.getTranslation();
        Vec2[] tl = t.apply(shape.points);
        StdDraw.textLeft(0, 0.25, String.format("X=%f, Y=%f", f.x, f.y));
        StdDraw.textLeft(0, 0.35, String.format("<X=%f, <Y=%f", Vec2.minX(tl), Vec2.minY(tl)));
        StdDraw.textLeft(0, 0.45, String.format(">X=%f, >Y=%f", Vec2.maxX(tl), Vec2.maxY(tl)));
    }
    private void fire() {}
    public void update() {
        super.update();
        if (cooldown > 0) cooldown --;
        else {
            if (inputbuf[4]) {
                inputbuf[4] = held[4];
                cooldown = FCOOL;
                fire();
            }
        }
        Transform t = this.shape.transform;
        if (inputbuf[0]) {t.translate(left);inputbuf[0]=held[0];}
        if (inputbuf[1]) {t.translate(right);inputbuf[1]=held[1];}
        if (inputbuf[2]) {t.translate(up);inputbuf[2]=held[2];}
        if (inputbuf[3]) {t.translate(down);inputbuf[3]=held[3];}
        if (inputbuf[5]) {t.rotate(-rotspeed);inputbuf[5]=held[5];}
        if (inputbuf[6]) {t.rotate(rotspeed);inputbuf[6]=held[6];}
        if (Vec2.minX(t.apply(shape.points)) > 1) {
            t.translate(new Vec2(-1,0));
        }
        if (Vec2.maxX(t.apply(shape.points)) < 0) {
            t.translate(new Vec2(1, 0));
        }
        if (Vec2.minY(t.apply(shape.points)) > 1) {
            t.translate(new Vec2(0, -1));
        }
        if (Vec2.maxY(t.apply(shape.points)) < 0) {
            t.translate(new Vec2(0, 1));
        }
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
    public void setTarget(Entity t) {}
    public Entity getTarget() {return null;}
    public void destroy() {
        DrawManager.remove(shape);
    }
    public Shape getShape() {
        return shape;
    }
    public Transform transform() {
        return shape.transform;
    }
}
