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
        // System.out.println(a.collider);
    }
    public void reload() {
        // System.out.println("RELOAD");
        Asteroid.generate(this, size, shape.transform);
    }
    public Asteroid() {
        super(new KinParams());
        size = (((int)(Math.random()*3.0d))+1);
        health = size*100;
        Asteroid.generate(this, size);
        // shape = Shape.Circle(((double)health) / 1000.0d);
        // shape.fill(StdDraw.CYAN);
        // System.out.println(shape.points[0]);
        double x, y;
        // double sx = (Math.random()>0.5d)?1:-1, sy = (Math.random()>0.5d)?1:-1;
        // x = (Math.random()*0.2+0.05)*sx + ((sx-1)/2)*-1;
        // y = (Math.random()*0.2+0.05)*sy + ((sy-1)/2)*-1;
        // double sx = (Math.random()>0.5d)?1:-1, sy = (Math.random()>0.5d)?1:-1;
        // x = (Math.random()*0.2+0.05)*sx + ((sx-1)/2)*-1;
        // y = (Math.random()*0.2+0.05)*sy + ((sy-1)/2)*-1;
        // x = (Math.random()*0.02+0.025)*sx + ((sx-1)/2)*-1;
        // y = (Math.random()*0.02+0.025)*sy + ((sy-1)/2)*-1;
        // shape.transform.setTranslation(new Vec2(x, y));
        // setVelocity(transform().getTranslation().sub(new Vec2(0.5, 0.5)).norm().mul(-0.25/size));
        setVelocity(new Vec2((Math.random()*0.15+0.1)-0.125, (Math.random()*0.15+0.1)-0.125).div(size));
        Vec2 nv = getVelocity().norm();
        double r = Math.abs(shape.maxX()-shape.minX())/2;
        // if (Math.random() > 0.5) {
        if (Math.abs(nv.x) > Math.abs(nv.y)) {
            y = Math.random()*0.5;
            if (nv.y < 0) {
                y = 1 - y;
            }
            if (nv.x > 0) {
                x = -Kinematic.EDGE_TOLERANCE/2 - r;
            } else {
                x = 1 + Kinematic.EDGE_TOLERANCE/2 + r;
            }
        } else {
            x = Math.random()*0.5;
            if (nv.x < 0) {
                x = 1 - x;
            }
            if (nv.y > 0) {
                y = -Kinematic.EDGE_TOLERANCE/2 - r;
            } else {
                y = 1 + Kinematic.EDGE_TOLERANCE/2 + r;
            }
        }
        shape.transform.setTranslation(new Vec2(x, y));
        DrawManager.add(this);
        Observer.register(this);
        if (x > 0 && x < 1 && y > 0 && y < 1) {
            Game.freeze = true;
            Observer.signal(SEvent.DRAW);
            System.out.println(getVelocity());
            System.out.println(nv);
            System.out.println(r);
        }
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
