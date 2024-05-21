package fp.entities;

import fp.drawing.*;
import fp.events.*;
import fp.*;

public class Laser extends Kinematic implements Entity, Drawable, EventListener {
    private int health = Main.toFrames(1500);
    private static void generate(Laser l, Transform t) {
        BuildResult br = Main.assetBuilder.execute("player.txt::<player>::LASER", t);
        l.shape = br.shape;
        l.collider = br.collider;
    }
    public Laser() {
        super(new KinParams(1, 1, 0));
        wraps = false;
        Laser.generate(this, Game.player.transform().getEquivalent());
        shape.transform.localTranslate(new Vec2(0, Game.player.nose().mag()));
        double prot = Game.player.transform().getRotation();
        Vec2 vel = new Vec2(0, 1).rotDeg(prot);
        super.setVelocity(vel.add(vel.norm().mul(Math.abs(Game.player.getVelocity().rotDeg(prot).y))));
        Game.addLaser(this);
        DrawManager.add(this);
        Observer.register(this);
    }
    public void draw() {
        shape.draw();
    }
    public void reload() {
        Laser.generate(this, shape.transform);
    }
    public void trigger(Event e) {
        if (e.type.signal()) {
            SEvent se = (SEvent)e;
            if (se.sigcode == SEvent.SIGTICK) {
                this.update(Game.delta());
            } else if (se.sigcode == Main.SIGRELD) {
                reload();
            }
        }
    }
    public void update(double dt) {
        super.update(dt);
        health -= 1;
        if (health <= 0) {
            DrawManager.remove(this);
            Observer.unregister(this);
        }
    }
    public int getHealth() {return health;}
    public void damage(int amount) {}
    public Shape getShape() {return shape;}
    public Transform transform() {return shape.transform;}
    public void destroy() {
        DrawManager.remove(this);
        Observer.unregister(this);
        Observer.signal(new StateEvent(Main.GAMEOBJS, this, "DESTROYED"));
    }
    void escaped() {
        destroy();
    }
}
