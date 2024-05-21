package fp.entities;

import fp.*;
import fp.drawing.*;
import fp.events.*;

public class Asteroid extends Kinematic implements Entity, EventListener, Drawable {
    private int health;
    private int size;
    private static final String[] scopes = new String[]{"<ast.small>::","<ast.medium>::","<ast.large>::"};
    private static void generate(Asteroid a, int size) {
        generate(a, size, new Transform());
    }
    private static void generate(Asteroid a, int size, Transform t) {
        BuildResult br = Main.assetBuilder.execute((String)Main.assetBuilder.selectMeta(scopes[size-1]+"names"), t);
        a.shape = br.shape;
        a.collider = br.collider;
    }
    public void reload() {
        Asteroid.generate(this, size, shape.transform);
    }
    public Asteroid() {
        super(new KinParams());
        double v = Math.random();
        size = (v < 0.05) ? 3 : ((v < 0.2) ? 2 : 1);
        health = size*100;
        Asteroid.generate(this, size);
        double ed = Math.random()*0.005+0.002;
        double ang = Math.random() * (Math.PI/2) - Math.PI/4;
        double x, y;
        if (Math.random() < 0.5) {
            ed *= -1;
        } else {
            ed += 1;
        }
        if (Math.random() < 0.5) {
            x = ed;
            y = Math.random();
        } else {
            y = ed;
            x = Math.random();
        }
        shape.transform.setTranslation(new Vec2(x, y));
        setVelocity(new Vec2(0.5, 0.5).sub(shape.transform.getTranslation()).rotRad(-ang).div(size*5));
        DrawManager.add(this);
        Observer.register(this);
    }
    public Asteroid(int size, double x, double y) {
        super(new KinParams());
        this.size = size;
        health = size*100;
        Asteroid.generate(this, size);
        shape.transform.setTranslation(new Vec2(x, y));
        DrawManager.add(this);
        Observer.register(this);
    }
    public void trigger(Event e) {
        if (e.type.signal()) {
            SEvent se = (SEvent)e;
            if (se.sigcode == Main.SIGRELD) {
                reload();
            } else if (se.sigcode == SEvent.SIGTICK) {
                update(Game.delta());
            }
        } else if (e.type.state()) {
            StateEvent se = (StateEvent)e;
            if (se.quickCode == Main.COMMANDS) {
                if (se.stateId.equals("ASTEROIDS")) {
                    if (se.stateNew.equals("CLEAR")) {
                        destroy();
                    }
                }
            }
        }
    }
    public void draw() {
        shape.draw();
    }
    public void update(double dt) {
        super.update(dt);
    }
    public int getHealth() {return health;}
    public void damage(int amount) {
        health -= amount;
        if (health <= 0) {
            destroy();
        }
    }
    public void destroy() {
        if (size > 1) {
            //
        }
        DrawManager.remove(this);
        Observer.unregister(this);
        Observer.signal(new StateEvent(Main.COUNTERS, "score", size));
        Observer.signal(new StateEvent(Main.GAMEOBJS, this, "DESTROYED"));
    }
    public Shape getShape() {return shape;}
    public Transform transform() {return shape.transform;}
}
