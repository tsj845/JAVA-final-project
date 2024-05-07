package fp.entities;

import fp.Main;
import fp.Observer;
import fp.StdDraw;
import fp.Vec2;
import fp.drawing.DrawManager;
import fp.drawing.Drawable;
import fp.drawing.Shape;
import fp.drawing.Transform;
import fp.events.Event;
import fp.events.EventListener;
import fp.events.StateEvent;

public class Asteroid extends Kinematic implements Entity, EventListener, Drawable {
    private int health;
    private Entity targetEnt;
    private static Shape generate(int size) {
        size /= 100;
        Shape main = Shape.Group();
        Shape working;
        working = Shape.Circle(size, new Transform(main.transform));
        working.fill(StdDraw.GRAY);
        main.addShape(working);
        return main;
    }
    public Asteroid() {
        super(new KinParams());
        health = (((int)(Math.random()*3.0d))+1)*100;
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
    }
    public void trigger(Event e) {}
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
