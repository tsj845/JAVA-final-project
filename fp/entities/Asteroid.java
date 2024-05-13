package fp.entities;

import fp.*;
import fp.drawing.*;
import fp.events.*;

public class Asteroid extends Kinematic implements Entity, EventListener, Drawable {
    private int health;
    private int size;
    private Entity targetEnt;
    private static Shape generate(int size) {
        return generate(size, new Transform());
    }
    private static Shape generate(int size, Transform t) {
        if (size == 100) {
            return Main.assetBuilder.execute((String)Main.assetBuilder.selectMeta("<ast.small>::names"), t);
        } else if (size == 200) {
            return Main.assetBuilder.execute((String)Main.assetBuilder.selectMeta("<ast.medium>::names"), t);
        } else if (size == 300) {
            return Main.assetBuilder.execute((String)Main.assetBuilder.selectMeta("<ast.large>::names"), t);
        } else {
            throw new IllegalArgumentException();
        }
    }
    public void reload() {
        // System.out.println("RELOAD");
        shape = Asteroid.generate(size*100, shape.transform);
    }
    public Asteroid() {
        super(new KinParams());
        size = (((int)(Math.random()*3.0d))+1);
        health = size*100;
        shape = Asteroid.generate(health);
        // shape = Shape.Circle(((double)health) / 1000.0d);
        // shape.fill(StdDraw.CYAN);
        // System.out.println(shape.points[0]);
        double sx = (Math.random()>0.5d)?1:-1, sy = (Math.random()>0.5d)?1:-1;
        double x, y;
        x = (Math.random()*0.2+0.05)*sx + ((sx-1)/2)*-1;
        y = (Math.random()*0.2+0.05)*sy + ((sy-1)/2)*-1;
        shape.transform.setTranslation(new Vec2(x, y));
        DrawManager.add(this);
        Observer.register(this);
    }
    public Asteroid(int size, double x, double y) {
        super(new KinParams());
        this.size = size;
        health = size*100;
        shape = Asteroid.generate(size*100);
        shape.transform.setTranslation(new Vec2(x, y));
        DrawManager.add(this);
        Observer.register(this);
    }
    public void trigger(Event e) {
        if (e.type.signal()) {
            SEvent se = (SEvent)e;
            if (se.sigcode == Main.SIGRELD) {
                reload();
            }
        }
    }
    public void draw() {
        shape.draw();
    }
    public void update() {
        super.update();
    }
    public int getHealth() {return health;}
    public void damage(int amount) {
        health -= amount;
        if (health <= 0) {
            destroy();
        }
    }
    public void setTarget(Entity t) {targetEnt = t;}
    public Entity getTarget() {return targetEnt;}
    public void destroy() {
        DrawManager.remove(this);
        Observer.signal(new StateEvent(Main.GAMEOBJS, this, "DESTROYED"));
    }
    public Shape getShape() {return shape;}
    public Transform transform() {return shape.transform;}
}
